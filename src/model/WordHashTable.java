/**
 * A structure of my own devising. Sort of like a telescopic hash table of 26[27], 
 * it implements a trie in a way with much better access time whenever the population of words around that node isn't dense
 */

package model;

public class WordHashTable {
	
	// ASCII value of 'a' . Minus 1
	public static int OFFSET = 96;
	
	// Need around 26 because we are only required to index/accept searches for English words
	// However need one extra to store words that don't have a length beyond the position index
	private Object[] buckets = new Object[27];
	
	private int pos_index = 0;
	
	/**
	 * Takes in an index to know which position in the word to pivot on
	 * @param index
	 */
	public WordHashTable(int index) {
		for (int i = 0; i < 27; i++) {
			buckets[i] = null;
		}
		pos_index = index;
	}
	
	/**
	 * Private function for manually setting the buckets' tenants
	 * @param key
	 * @param value
	 * @return
	 */
	private boolean set(String key, Object value) {
		int hash_key = 0;
		if(key.length() > pos_index) {
			hash_key = (int)(key.charAt(pos_index)) - OFFSET;
		}
		buckets[hash_key] = value;
		return true;
	}
	
	/**
	 * Put the key in the table. Slight variation from standard hashing
	 * @param key
	 * @return
	 */
	public boolean put(String key) {

		int hash_key = 0;
		if(key.length() > pos_index) {
			hash_key = (int)(key.charAt(pos_index)) - OFFSET;
		}
		Object temp = buckets[hash_key];
		
		// Nothing in the bucket, create a node and store it there
		if (temp == null) {
			DataNode new_word_node = new DataNode(key);
			buckets[hash_key] = new_word_node;
		}
		// Contains a table, put into that table to telescope and avoid collisions
		else if (temp instanceof WordHashTable) {
			WordHashTable table = (WordHashTable)(temp);
			table.put(key);
		}
		// Contains a node, either a match or a collision
		else if (temp instanceof DataNode) {
			DataNode word_node = (DataNode)temp;
			String word = word_node.getWord();
			
			// If the same word is already there
			if (key.equals(word)) {
				word_node.newOccurrence();
			}
			// If word there is a collision, 
			// telescope by making another hash table for the differing letter
			else {
				WordHashTable table = new WordHashTable(pos_index+1);
				table.set(word, temp);
				DataNode new_word_node = new DataNode(key);
				table.set(key, new_word_node);
				
				buckets[hash_key] = table;
			}
		}
		
		return true;
	}
	
	/**
	 * Gets a DataNode based on key. Also slightly different from ordinary hash get, for obvious reasons
	 * @param key
	 * @return
	 */
	public DataNode get(String key) {
		
		if (key == null) {
			return null;
		}
		
		int hash_key = 0;
		if(key.length() > pos_index) {
			hash_key = (int)(key.charAt(pos_index)) - OFFSET;
		}
		Object temp = buckets[hash_key];
		
		DataNode ret_val = null;
		
		// Nothing in the bucket, return null
		if (temp == null) {
		}
		// Contains a table, return what you get from table
		else if (temp instanceof WordHashTable) {
			WordHashTable table = (WordHashTable)(temp);
			ret_val = table.get(key);
		}
		// Contains a node, either a match or a collision
		else if (temp instanceof DataNode) {
			DataNode word_node = (DataNode)temp;
			String word = word_node.getWord();
			
			// If the same word is already there
			if (key.equals(word)) {
				ret_val = word_node;
				word_node.newRequest();
			}
			// If word isn't there, return null
			else {
				ret_val = null;
			}
		}
		
		return ret_val;
	}
	
	/**
	 * Size function in case the functionality is ever needed
	 * @return
	 */
	public int size() {
		int size = 0;
		for (Object obj : buckets) {
			if (obj == null)
				continue;
			// If bucket has a table within, add the size of the table
			if (obj instanceof WordHashTable) {
				WordHashTable table = (WordHashTable)obj;
				size += table.size();
			}
			// If there is only a node there add 1
			else if (obj instanceof DataNode) {
				size++;
			}
		}
		
		return size;
	}
	
}
