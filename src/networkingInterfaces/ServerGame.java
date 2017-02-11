package networkingInterfaces;

import java.util.ArrayList;

import logic.CaptureTheFlagMode;
import logic.EscortMode;
import logic.GameMode;
import logic.KingOfTheHillMode;
import logic.Team;
import logic.TeamMatchMode;
import networkingSharedStuff.Message;

/**
 * Server side integration to play a game in a specific game mode. Will be
 * instantiated in the lobby.
 * 
 * @author Alexandra Paduraru
 *
 */
public class ServerGame {

	private GameMode game;

	/**
	 * The server will run a specific game mode, given as an integer argument in
	 * this constructor and then translated into the corresponding game mode
	 * according to the protocol.
	 * 
	 * @param game
	 *            The game mode that will be started.
	 */
	public ServerGame(int gameMode, Team red, Team blue) {
		switch (gameMode) {
		case 1:
			game = new TeamMatchMode(red, blue);
			break;
		case 2:
			game = new KingOfTheHillMode(red, blue);
			break;
		case 3:
			game = new CaptureTheFlagMode(red, blue);
			break;
		case 4:
			game = new EscortMode(red, blue);
			break;
		default:
			game = new TeamMatchMode(red, blue);
			break;
		}

	}

	/**
	 * Sends a given message to all the players involved in the current game.
	 * @param msg The message to be sent to the players.
	 */
	public void sendMsgToAll(String msg){
		ArrayList<ClientPlayer> allPlayers = getAllPlayers();
		
		for(ClientPlayer p: allPlayers){
			p.getQueue().offer(new Message(msg));
		}
	}
	
	/**
	 * Starts a new game in the given mode and notifies all players involved in the game that the game has started.
	 */
	//TODO: call this from GameClient when the player is ready to start the game. After, lunch the game window for the player to start.
	public void startGame(){
		game.start();
		String msgToClients = "StartGame";
		sendMsgToAll(msgToClients);
	}
	
	/*Getters and setters*/
	public GameMode getGame() {
		return game;
	}
	
	public ArrayList<ClientPlayer> getRedTeamPlayers(){
		return game.getFirstTeam().getMembers();
	}
	
	public ArrayList<ClientPlayer> getBlueTeamPlayers(){
		return game.getSecondTeam().getMembers();
	}
	
	public ArrayList<ClientPlayer> getAllPlayers(){
		ArrayList<ClientPlayer> redTeam = getRedTeamPlayers();
		ArrayList<ClientPlayer>  blueTeam = getBlueTeamPlayers();
		
		redTeam.addAll(blueTeam);
		
		return redTeam;
	}
}
