package integrationClient;

import javafx.application.Platform;
import physics.Flag;
import players.GhostPlayer;

import java.util.ArrayList;

/**
 * Client-sided class which receives an action imposed by the server on the
 * player and executes it.
 *
 * @author Alexandra Paduraru
 *
 */
public class ClientGameStateReceiver {

	private static final boolean debug = false;
	private ArrayList<GhostPlayer> players;
	private Flag flag;


	/**
	 * Initialises a new action receiver with a player which will be controlled
	 * by the actions received from the server.
	 *
	 * @param players The list of all players in the game.
	 *
	 */
	public ClientGameStateReceiver(ArrayList<GhostPlayer> players) {
		this.players = players;
	}

	/**
	 * Initialises a new action receiver with a player which will be controlled
	 * by the actions received from the server.
	 *
	 * @param players The list of all players in the game.
	 * @param flag The flag of the capture the flag mode
	 */
	public ClientGameStateReceiver(ArrayList<GhostPlayer> players, Flag flag) {
		this.players = players;
		this.flag = flag;
	}

	/**
	 * Updates a ghost player's location.
	 * @param id The id of the player which has changed location.
	 * @param x The new x coordinate of the player.
	 * @param y The new y coordinate of the player.
	 * @param angle The new angle of the player.
	 * @param visible Whether or not the player is visible(i.e. it has been eliminated>
	 */
	public void updatePlayer(int id, double x, double y, double angle, boolean visible){

		GhostPlayer playerToBeUpdated = getPlayerWithId(id);
		//System.out.println("angle :" + angle);
		Platform.runLater(() ->
		{
			playerToBeUpdated.relocatePlayer(x, y);
			playerToBeUpdated.setRotationAngle(angle);
			playerToBeUpdated.setVisible(visible);
		});
		if (debug) System.out.println("updated player with id : " + id);
	}

	/**
	 * Update a player's active bullets.
	 * @param id The id of the player.
	 * @param bullets String which contains the coordinates and the angle of the bullets fired by this player,
	 * 				  according to the protocol.
	 */
	public void updateBullets(int id, String[] bullets){
		GhostPlayer p = getPlayerWithId(id);

		if (p != null) { // the player is not us

//			ArrayList<GhostBullet> firedBullets = new ArrayList<>();
			for (int i = 0; i < bullets.length - 2; i = i + 3) {

				int bulletId = Integer.parseInt(bullets[i]);
				double x = Double.parseDouble(bullets[i+1]);
				double y = Double.parseDouble(bullets[i + 2]);

//				firedBullets.add(new GhostBullet(bulletId, x, y, p.getTeam()));
				Platform.runLater(() -> p.updateSingleBullet(bulletId, x, y));

			}
//			p.getFiredBullets().clear();
//			p.setFiredBullets(firedBullets);
		}
	}

	public void updateFlag(int id){

		GhostPlayer player = getPlayerWithId(id);
		player.setFlagStatus(true);
		flag.setVisible(false);
		
		System.out.println("Player " + id + " captured the flag");
	}
	
	public void lostFlag(int id){

		GhostPlayer player = getPlayerWithId(id);
		player.setFlagStatus(false);
		flag.setVisible(true);
		flag.relocate(player.getLayoutX(), player.getLayoutY());
		
		System.out.println("Player " + id + " lost the flag");

	}
	
	public void respawnFlag(int id, double x, double y){
		flag.setVisible(true);
		flag.relocate(x, y);

		GhostPlayer player = getPlayerWithId(id);
		player.setFlagStatus(false);
		System.out.println("Flag has been respawned");
		
	}


	/**
	 * Helper method to find the player with a specific id from the entire list of players in the game.
	 * @param id The player's id.
	 */
	public GhostPlayer getPlayerWithId(int id){
		for (GhostPlayer p : players){
			if (p.getPlayerId() == id)
				return p;
		}
		return null;
	}



}
