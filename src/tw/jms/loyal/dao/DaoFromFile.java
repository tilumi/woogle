package tw.jms.loyal.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;

public class DaoFromFile implements Dao {

	public static List<String> validUsers;

	public DaoFromFile() {
		try {
			validUsers = FileUtils.readLines(new ClassPathResource(
					"validUsers.txt").getFile());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean isValidUser(String user) {
		return validUsers.contains(user);
	}

}