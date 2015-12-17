/**
 * Static class for reading in the data from the directory
 */

package readin;

import java.io.IOException;
import java.nio.file.*;

import model.WordHashTable;

public final class DataReader {
	// The data folder is ... data. I'm not creative
	private static final String data_folder = "data";
	
	/**
	 * Reads the data. This class is only here for this function
	 * @param root_table
	 * @return
	 */
	public static boolean readData(WordHashTable root_table) {
		Path dir = FileSystems.getDefault().getPath(data_folder);
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.txt")) {
			for (Path entry : stream) {
				// Passes the heavy lifting on to the other guy
				ReadFile rf = new ReadFile(root_table, entry);
				if (!rf.readWordsFromFile()) {
					System.out.println("Failed to read data in file: " + entry.getFileName().toString());
				}
			}
			
			return true;
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
