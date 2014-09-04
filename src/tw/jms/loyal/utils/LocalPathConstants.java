package tw.jms.loyal.utils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalPathConstants {
	public final static Path WORDS_DIR = Paths.get("/Users/lucasmf/Copy/Words");
	public final static Path HTML_OUTPUT_DIR = WORDS_DIR.resolve("Html");
	public final static Path HTML_MEDIA_OUTPUT_DIR = HTML_OUTPUT_DIR.resolve("Media");
}
