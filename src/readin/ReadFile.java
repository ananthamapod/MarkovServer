/**
 * Reads the tokens in from a file and throws them into the dataset
 */

package readin;

import model.WordHashTable;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.Locale;

public class ReadFile {
	private WordHashTable root_table;
	private Path file_path;
	
	// Only match words
	private static final String pattern = "[^a-z]+";
	
	/**
	 * Newly created for each file
	 * @param table
	 * @param file_path
	 */
	public ReadFile(WordHashTable table, Path file_path) {
		root_table = table;
		this.file_path = file_path;
	}
	
	/**
	 * Break up the line into words and throw it in the dataset
	 * @param line
	 * @return
	 */
	private boolean addWordsToSystem(String line) {
		line = line.toLowerCase(Locale.ROOT);
		String[] tokens = line.split(pattern);
		for (String token : tokens) {
			if (!token.equals(""))
				root_table.put(token);
		}
		return true;
	}
	
	/**
	 * Reads the file using the nio package
	 * @return
	 */
	public boolean readWordsFromFile() {

		System.out.println(file_path.toString());
		Charset charset = Charset.forName("US-ASCII");
		try (BufferedReader reader = Files.newBufferedReader(file_path, charset)) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (!addWordsToSystem(line)){
					System.out.println("Error in word adding, line " + line);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}
