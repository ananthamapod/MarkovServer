/**
 * Main class
 * 
 * The class where the magic happens.
 * Sets up the server/CLI and goes from there
 */

package controller;

import java.util.Scanner;

import model.DataNode;
import model.WordHashTable;
import readin.DataReader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
	
	
	private ServerSocket server;
	private Socket socket;
	private BufferedWriter out;
	private BufferedReader in;
	public static final int PORT = 1234;
	
	public static final String pattern = "[^a-z]+";
	
	// endpoint GET parameter
	public static final String query = "word=";
	
	// reference to the compiled hash table
	public WordHashTable root_table;
	
	/**
	 * Administrative work to compile the dataset
	 */
	public Main() {
		root_table = new WordHashTable(0);
		
		// Read in the data files and print them on screen
		DataReader.readData(root_table);
		System.out.println(root_table.size());
	}
	
	/**
	 * Setting up the server. And then, loop!
	 */
	public void startServer() {
		try {
			server = new ServerSocket(1234);
			System.out.println("Server requests are to be GET requests with a \"word\" parameter");
			while (true) {
				try {
					// Idle state
					waitForCall();
					
					// Setting up the streams
					setIOStreams();
					
					// Evaluating the request
					processCall();
				}
				catch (EOFException e) {
					System.out.println("Server connection closed");
				}
				finally {
					//Disconnect
					endCall();
				}

			}
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Gets references to the IO streams once the socket is opened
	 * @throws IOException
	 */
	private void setIOStreams() throws IOException {
		out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		out.flush();
		
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		System.out.println("IO Stream is ready");
	}
	
	
	/**
	 * Idle server. The waiting before/between requests
	 * @throws IOException
	 */
	private void waitForCall() throws IOException {
		System.out.println("Waiting for call");
		// Execution stops here until a socket connection is seen by the server socket
		socket = server.accept();
		System.out.println("Connection from " + socket.getInetAddress().getHostName());
	}
	
	/**
	 * Reads in input and generates query to search the dataset
	 */
	private void processCall() {
		String word = null;
		// Checks each line in the request, "just in case"
		while (true) {
			try {
				word = in.readLine();
				if (word == null || word.trim().length() == 0) {
					break;
				}
				
				// Check if start of GET HTTP Request
				if(word.indexOf("GET") > -1) {
					word = extractQuery(word);
					DataNode word_node = null;
					
					// If the query word was invalid, no need to try searching
					if(word == null || word.equals("")) {
						word_node = new DataNode("");
					}
					else {
						word_node = root_table.get(word.toLowerCase());
					}
					
					// Send response
					send(word_node);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Utility for finding the search parameter in the GET request
	 * @param line
	 * @return
	 */
	private String extractQuery(String line) {
		String ret_str = null;
		String request = line.toLowerCase();
		
		// Finds the query parameter, if the right one is given
		int query_loc = request.indexOf(query);
		
		if(query_loc > -1) {
			request = request.substring(query_loc+query.length());
			String[] words = request.split(pattern);
			// Got lazy about the if statements. Some inline conditionals
			ret_str = (words.length > 0? words[0] : "");
		}
		
		return ret_str;
	}
	
	/**
	 * Send response back to client
	 * @param word_node
	 */
	private void send(DataNode word_node) {
		try {
			// Turns out didn't really need this to be a StringBuffer. I didn't know that at the time
			StringBuffer sb = new StringBuffer();
			sb.append(word_node.toString());
			String msg = sb.toString();
			
			// The full response header
			String http_header = "HTTP/1.1 200 OK\r\n" +
					"Server: Server\r\n" +
					"Content-Type: application/json\r\n" +
					"Content-Length: " + msg.length() + "\r\n" +
					"Connection: close\r\n\r\n";
			
			// Add the body of the response
			String response = http_header + msg;
			
			// Send it!
			out.write(response);
			out.flush();
			
			// And we're in the clear
			System.out.println("Server# " + word_node);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Wraps up. Closes the socket and the streams
	 */
	private void endCall() {
		System.out.println("Connection closed");
		try {
			out.close();
			in.close();
			socket.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Main entry point
	 * @param args
	 */
	public static void main(String[] args) {
		Main main  = new Main();
		Scanner sc = new Scanner(System.in);
		System.out.println("Press 'T' to enter the interactive console for testing. Press any other key to launch the server");
		System.out.print(">>>");
		char command = sc.next().charAt(0);
		
		sc.close();
		
		if (command == 'T' || command == 't') {
			interactive(main.root_table);
		} else {
			main.startServer();
		}
	}

	/**
	 * Small testing unit
	 * @param root_table
	 */
	public static void interactive(WordHashTable root_table) {
		Scanner sc = new Scanner(System.in);
		String input;
		System.out.println("Search for a word. Use -q to quit");
		
		while (true) {
			System.out.print(">>>");
			input = sc.nextLine();
			if (input.equals("-q")) {
				break;
			}
			String[] words = input.split(pattern);
			// Got lazy about the if statements. Some inline conditionals
			String testcase = (words.length > 0? words[0] : "");
			System.out.println(testcase.equals("")? "Invalid search term." : root_table.get(testcase.toLowerCase()));
		}
		
		sc.close();
	}
}
