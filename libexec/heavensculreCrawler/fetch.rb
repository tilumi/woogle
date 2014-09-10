# encoding: UTF-8
require 'rubygems'
require 'mechanize'
require 'sqlite3'
require 'fileutils'
require 'parallel'


if ARGV.length != 1
	p "Usage: ruby fetch.rb [RootDir]"
	exit
end
ROOT_DIR = ARGV[0]


@agent = Mechanize.new
page = @agent.get('http://heavensculture.com/')

# @db = SQLite3::Database.new( "/tmp/heavensculture.sqlite" )
# rows = @db.execute( "create table if not exists fetched_docs (attach_id TEXT, file_name TEXT)")


login_form =  page.form('frmLogin')
login_form.user = 'tilumi'
login_form.passwrd = 'tilumiwords'
page = @agent.submit(login_form, login_form.buttons.first)

exts_to_download = ['.doc','.docx','.ppt','.pptx','.xls','.xlsx','.pdf']

@agent.pluggable_parser['application/msword'] = Mechanize::Download
@agent.pluggable_parser['application/vnd.ms-powerpoint'] = Mechanize::Download
@agent.pluggable_parser['application/vnd.ms-excel'] = Mechanize::Download
@agent.pluggable_parser['application/vnd.openxmlformats-officedocument.wordprocessingml.document'] = Mechanize::Download
@agent.pluggable_parser['application/vnd.openxmlformats-officedocument.presentationml.presentation'] = Mechanize::Download
@agent.pluggable_parser['application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'] = Mechanize::Download
@agent.pluggable_parser.pdf = Mechanize::Download
@agent.pluggable_parser['text/plain']= Mechanize::Download
@agent.pluggable_parser['application/txt'] = Mechanize::Download

boards = [
	{:title => '啟示', :id => 61},
	{:title => '主日週三', :id => 68},
	{:id => 116, :title => '清晨'},
	{:id => 106, :title => '聖靈運動'}
]

def has_td_chinese_version?(page)
	result = false
	page.links.each do |link|
		if link.text.include? '繁體'
			result = true
		end
	end
	result
end

def get_last_page_number(page)
	max = 1
	page.search('.navPages').each do |link|
		max = link.children[0].content.to_i if link.children[0].content.to_i > max
	end
	max.to_i
end

def goto_page(board_page, page_num)
	board_page.search('a.navPages').each do |link|
		if link.children[0].content.to_i == page_num
			p link['href']
			board_page = @agent.get(link['href'])
			break
		end
	end
	board_page
end

def download(board_title, link)
	dest_dir = File.join(ROOT_DIR, board_title)
	FileUtils.mkdir_p(dest_dir) unless File.exists? dest_dir
	dest_file_path = File.join(ROOT_DIR, board_title, link.text.strip)
	if link.href.index('attach=')
		attach_id = link.href[link.href.index('attach=')+'attach='.length..-1]
		# rows = @db.execute( " select * from fetched_docs where attach_id = ? ", attach_id)
		unless File.exist?(dest_file_path)
			@agent.get(link.href).save(dest_file_path)
			p "Downloaded: " + link.text.strip + " from " + link.href
		end
	end
	false
end

boards.each do |board|
	board_title = board[:title]
	p "board: #{board_title}"

	board_page = @agent.get("http://heavensculture.com/index.php?board=#{board[:id]}.0")
	max_page_number = get_last_page_number(board_page)
	p "last_page: #{max_page_number}"
	(1..max_page_number).each do |page_num|
		p page_num
		if page_num > 1
			board_page = goto_page(board_page, page_num)
		end
		Parallel.each(board_page.search('.subject a'), :in_threads => 10) do |link|
			unless link['href'].nil?
				page = @agent.get(link['href'])
				if has_td_chinese_version? page
					page.links.each do |link|
						if link.text.rindex('.')
							if exts_to_download.include? link.text[link.text.rindex('.')..-1] and link.text.include? '繁體'
								download(board_title, link)
							end
						end
					end
				else
					page.links.each do |link|
						if link.text.rindex('.')
							if exts_to_download.include? link.text[link.text.rindex('.')..-1]
								download(board_title, link)
							end
						end
					end
				end
			end
		end
	end
end
