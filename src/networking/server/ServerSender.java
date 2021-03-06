package networking.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

import networking.shared.Message;
import networking.shared.MessageQueue;

// Continuously reads from message queue for a particular client,
// forwarding to the client.
/**
 * Class to send messages to a client.
 * 
 * @author Matthew Walters
 */
public class ServerSender extends Thread {

	private boolean m_running = true;
	private MessageQueue queue;
	private PrintStream client;
	private Socket socket;

	/**
	 * Construct the class, setting passed variables to local objects.
	 * 
	 * @param queue
	 *            Message queue for a client.
	 * @param clientStream
	 *            Stream to print out to the client.
	 * @param socket
	 *            Socket to the client.
	 */
	public ServerSender(MessageQueue queue, PrintStream clientStream, Socket socket) {
		this.queue = queue;
		this.client = clientStream;
		this.socket = socket;
	}

	/**
	 * Sets the global variable is_running to false. Will stop the thread from
	 * running its loop in run().
	 */
	public void stopThread() {
		m_running = false;
	}

	/**
	 * The main method running in this class, runs when the class is started
	 * after initialisation.
	 */
	public void run() {
		while (m_running) {
			// Get messages from the message queue.
			Message msg = queue.take();
			// System.out.println("Sending:" + msg.getText());
			// Print to the client stream.
			client.println(msg.getText());
		}
		// If stopped, close the client stream.
		client.close();
		try {
			// Attempt to close the socket to the client.
			socket.close();
		} catch (IOException e) {
			//
		}
		return;
	}
}