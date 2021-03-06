package networking.client;

import networking.game.UDPClient;
import physics.GameStateClient;
import physics.InputHandler;
import players.ClientPlayer;
import rendering.Renderer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Sends user inputs(client-sided) to the server.
 *
 * @author Alexandra Paduraru
 * @author Filippo Galli
 *
 */
public class ClientInputSender {

	// sending stats delay
	public static final long DELAY_MILLISECONDS = 25;
	// for testing purposes
	private static final boolean debug = false;
	// counter of the step being sent
	public static long counterFrame = 0;
	//amount of packets sent per second
	private static double fps = 1000/DELAY_MILLISECONDS;
	//integer that represent a step for synchronization Client-Server
	public static int step =  (int) ((Renderer.TARGET_FPS / fps) * 10);
	private InputHandler handler;
	private ClientPlayer player;
	private int actualTimes;
	private UDPClient udpClient;

	/**
	 * Initialises a new input sender.
	 *
	 * @param udpClient
	 *            The client sender used by networking to send all information
	 *            to the server.
	 * @param handler
	 *            The handler which handles all user inputs in the player.
	 * @param playerId
	 *            The current player's id.
	 */
	public ClientInputSender(UDPClient udpClient, InputHandler handler, ClientPlayer player) {
		this.udpClient = udpClient;
		this.handler = handler;
		this.player = player;
	}

	/**
	 * Starts sending client inputs to the server at a rate of 30 frames per
	 * second.
	 */
	public void startSending() {

		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		Runnable sender = new Runnable() {
			public void run() {
				actualTimes++;
				sendServer();

				if (!udpClient.isActive())
					scheduler.shutdown();
			}
		};

		ScheduledFuture<?> senderHandler = scheduler.scheduleAtFixedRate(sender, 0, DELAY_MILLISECONDS,
				TimeUnit.MILLISECONDS);

		Runnable frameCounter = new Runnable() {
			public void run() {
				sendCurrentTime();
			}
		};

		ScheduledFuture<?> frameCounterHandle = scheduler.scheduleAtFixedRate(frameCounter, 0, 1, TimeUnit.SECONDS);

		// for testing purposes:

		/*
		 * Runnable frameCounter = new Runnable() { public void run() {
		 * System.out.println("cliend Sending times " + times); times = 0;
		 *
		 * } };
		 *
		 * ScheduledFuture<?> frameCounterHandle =
		 * scheduler.scheduleAtFixedRate(frameCounter, 0, 1, TimeUnit.SECONDS);
		 */
	}

	/**
	 * Checks to see if the player's position has changed. If so, it sends to
	 * the server the user action (player goes up/down, left/right, mouse
	 * position changes) through the protocol.
	 */
	private void sendServer() {
		// Protocol: "0:id:counterFrame:" + Up/Down/Left/Right/Shooting + ":Angle:<angle>",
		// depending on the player's action

		counterFrame += step;

		player.setUp(handler.isUp());
		player.setDown(handler.isDown());
		player.setLeft(handler.isLeft());
		player.setRight(handler.isRight());

		player.getBufferReconciliation().add(new GameStateClient(counterFrame, player.getLayoutX(), player.getLayoutY(),
				handler.isUp(), handler.isDown(), handler.isLeft(), handler.isRight()));

		String toBeSent;
		toBeSent = "0:" + player.getPlayerId() + ":" + counterFrame + ":";
		// did player move up?
		if (handler.isUp())
			toBeSent += "Up:";

		// did player move down?
		if (handler.isDown())
			toBeSent += "Down:";

		// did player move left?
		if (handler.isLeft())
			toBeSent += "Left:";

		// did player move right?
		if (handler.isRight())
			toBeSent += "Right:";

		// is the player shooting?
		if (handler.isShooting())
			toBeSent += "Shoot:";

		// did the mouse move?
		toBeSent += "Angle:" + player.getAngle();

		udpClient.sendMessage(toBeSent);
	}

	/**
	 * Method to test network latency, by calculating the time a client message
	 * reaches the server.
	 */
	private void sendCurrentTime() {
		String toSend;
		toSend = "3:" + player.getPlayerId() + ":";
		if (debug)
			System.out.println("client Sending time: " + System.currentTimeMillis());
		toSend += System.currentTimeMillis();
		udpClient.sendMessage(toSend);
	}

}
