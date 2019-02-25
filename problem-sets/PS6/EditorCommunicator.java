import java.io.*;
import java.net.Socket;
import java.awt.*;
import java.util.*;

/**
 * Handles communication to/from the server for the editor
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 * @author Chris Bailey-Kellogg; overall structure substantially revised Winter 2014
 * @author Travis Peters, Dartmouth CS 10, Winter 2015; remove EditorCommunicatorStandalone (use echo server for testing)
 * @author Thomas Monfre, Dartmouth CS 10, Winter 2018; handle messages and requests
 */
public class EditorCommunicator extends Thread {
	private PrintWriter out;		// to server
	private BufferedReader in;		// from server
	protected Editor editor;		// handling communication for

	/**
	 * Establishes connection and in/out pair
	 */
	public EditorCommunicator(String serverIP, Editor editor) {
		this.editor = editor;
		System.out.println("connecting to " + serverIP + "...");
		try {
			Socket sock = new Socket(serverIP, 4242);
			out = new PrintWriter(sock.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			System.out.println("...connected");
		}
		catch (IOException e) {
			System.err.println("couldn't connect");
			System.exit(-1);
		}
	}

	/**
	 * Sends message to the server
	 */
	public void send(String msg) {
		out.println(msg);
	}
	
	/**
	 * Get the PrintWriter object of this communicator
	 */
	public PrintWriter getOut() {
		return out;
	}

	/**
	 * Keeps listening for and handling (your code) messages from the server
	 */
	public void run() {
		try {
			// Handle messages
			String line;
			while ((line = in.readLine()) != null) {
				
				// split the input line into an array by spaces
				String[] tokens = line.split("\\ ");
				
				// get the first major command (draw, move, recolor, or delete)
				String command = tokens[0];
												
				// call helper methods to perform that command from HandleRequests class
				if (command.equals("draw")) HandleRequests.drawHelper(tokens, editor.getSketch());
				else if (command.equals("move")) HandleRequests.moveHelper(tokens, editor.getSketch());
				else if (command.equals("recolor")) HandleRequests.recolorHelper(tokens, editor.getSketch());
				else if (command.equals("delete")) HandleRequests.deleteHelper(tokens, editor.getSketch());
				
				// repaint the editor's window after helper completion
				editor.repaint();
			}
		}
		// handle exceptions
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			System.out.println("server hung up");
		}
	}	
}
