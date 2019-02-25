import java.io.*;
import java.util.*;
import java.net.Socket;

/**
 * Handles communication between the server and one client, for SketchServer
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012; revised Winter 2014 to separate SketchServerCommunicator
 * @author Thomas Monfre, Dartmouth CS 10, Winter 2018; handle messages from clients and send requests
 */
public class SketchServerCommunicator extends Thread {
	private Socket sock;					// to talk with client
	private BufferedReader in;				// from client
	private PrintWriter out;				// to client
	private SketchServer server;			// handling communication for

	public SketchServerCommunicator(Socket sock, SketchServer server) {
		this.sock = sock;
		this.server = server;
	}

	/**
	 * Sends a message to the client
	 * @param msg
	 */
	public void send(String msg) {
		out.println(msg);
	}
	
	/**
	 * Keeps listening for and handling (your code) messages from the client
	 */
	public void run() {
		try {
			System.out.println("someone connected");
			
			// Communication channel
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new PrintWriter(sock.getOutputStream(), true);

			// Tell the client the current state of the world
			Map<Integer,Shape> map = server.getSketch().getMap();
			
			for (Integer id : map.keySet()) {
				SendRequests.requestAddShape(map.get(id), out);
			}

			// Handle messages from client
			// send command to server's sketch then broadcast command to all threads connected to the server
			String line;
			while ((line = in.readLine()) != null) {
							
				// split the input line into an array by spaces
				String[] tokens = line.split("\\ ");
								
				// get the first major command (draw, move, recolor, or delete)
				String command = tokens[0];
						
				// update the server's state of the world
				// call helper methods to perform that command from HandleRequests class
				if (command.equals("draw")) HandleRequests.drawHelper(tokens, server.getSketch());
				else if (command.equals("move")) HandleRequests.moveHelper(tokens, server.getSketch());
				else if (command.equals("recolor")) HandleRequests.recolorHelper(tokens, server.getSketch());
				else if (command.equals("delete")) HandleRequests.deleteHelper(tokens, server.getSketch());

				// have server broadcast the command to all threads connected to it
				server.broadcast(line);
			}	
		
			// Clean up -- note that also remove self from server's list so it doesn't broadcast here
			server.removeCommunicator(this);
			out.close();
			in.close();
			sock.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
