/**
 * DataNode class. Stores the data for each word
 */

package model;

public class DataNode {

	public int num_requests;
	public int num_occurrences;
	public String word;
	
	public DataNode(String word) {
		this.word = word;
		num_requests = 0;
		// Assumes DataNode instance only created if a word is found
		num_occurrences = 1;
	}
	
	/**
	 * Increment requests
	 */
	public void newRequest() {
		num_requests++;
	}
	
	/**
	 * Increment occurrences
	 */
	public void newOccurrence() {
		num_occurrences++;
	}
	
	/**
	 * Autogen getter
	 */
	public int getNumRequests() {
		return num_requests;
	}

	/**
	 * Autogen setter
	 */
	public void setNumRequests(int num_requests) {
		this.num_requests = num_requests;
	}

	/**
	 * Autogen getter
	 */
	public int getNumOccurrences() {
		return num_occurrences;
	}

	/**
	 * Autogen setter
	 */
	public void setNumOccurrences(int num_occurrences) {
		this.num_occurrences = num_occurrences;
	}

	/**
	 * Autogen getter
	 */
	public String getWord() {
		return word;
	}

	/**
	 * Autogen setter
	 */
	public void setWord(String word) {
		this.word = word;
	}

	
	/**
	 * Autogen hash
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + num_occurrences;
		result = prime * result + num_requests;
		result = prime * result + ((word == null) ? 0 : word.hashCode());
		return result;
	}

	/**
	 * Autogen equals
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataNode other = (DataNode) obj;
		if (num_occurrences != other.num_occurrences)
			return false;
		if (num_requests != other.num_requests)
			return false;
		if (word == null) {
			if (other.word != null)
				return false;
		} else if (!word.equals(other.word))
			return false;
		return true;
	}

	/**
	 * Prints the data about the node in JSON format
	 */
	@Override
	public String toString() {
		return "{\"word\": \"" + word + "\", \"occurrences\": "
				+ num_occurrences + ", \"requests\": " + num_requests + "}";
	}
}
