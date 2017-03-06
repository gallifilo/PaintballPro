package integrationClient;

import java.util.ArrayList;

import javafx.scene.transform.Rotate;
import players.GhostPlayer;
import players.ServerMinimumPlayer;

/**
 * Client-sided class which receives an action imposed by the server on the
 * player and executes it.
 *
 * @author Alexandra Paduraru
 *
 */
public class ClientGameStateReceiver {

	private ArrayList<GhostPlayer> players;

	/**
	 * Initializes a new action receiver with a player which will be controlled
	 * by the actions received from the server.
	 *
	 * @param player
	 *            The player upon which the actions take place.
	 */
	public ClientGameStateReceiver(ArrayList<GhostPlayer> players) {
		this.players = players;
	}

	public void updatePlayer(int id, double x, double y, double angle, boolean visible){

		GhostPlayer playerToBeUpdated = getPlayerWithId(id);

		playerToBeUpdated.setSyncX(x);
		playerToBeUpdated.setSyncY(y);
		playerToBeUpdated.setRotationAngle(angle);
		playerToBeUpdated.setSyncVisible(visible);
	}

	/**
	 * Helper method to find the player with a specific id from the entire list of players in the game.
	 * @param id The player's id.
	 */
	private GhostPlayer getPlayerWithId(int id){
		for (GhostPlayer p : players){
			if (p.getPlayerId() == id)
				return p;
		}
		return null;
	}



}
