package networkingInterfaces;

import java.util.ArrayList;

import enums.TeamEnum;
import logic.CaptureTheFlagMode;
import logic.EscortMode;
import logic.GameMode;
import logic.KingOfTheHillMode;
import logic.ServerPlayer;
import logic.Team;
import logic.TeamMatchMode;
import networkingServer.ServerMsgReceiver;
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
	private ServerMsgReceiver serverReceiver;

	/**
	 * The server will run a specific game mode, given as an integer argument in
	 * this constructor and then translated into the corresponding game mode
	 * according to the protocol.
	 * 
	 * @param game
	 *            The game mode that will be started.
	 */
	public ServerGame(int gameMode, Team red, Team blue, ServerMsgReceiver receiver) {
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
		this.serverReceiver = receiver;

	}
	
	/**
	 * Starts a new game in the given mode and notifies all players involved in the game that the game has started.
	 */
	public void startGame(){
		game.start();
		//String msgToClients = "StartGame";
		//serverReceiver.sendToAll(msgToClients);
	}
	
	/**
	 * Checks to see when a game has ended and sends the appropriate message to all clients.
	 */
	public void endGame(TeamEnum t){
		String toBeSent = "EndGame:";
		
		if (t== TeamEnum.RED){
			toBeSent += "Red";
			System.out.println("Winner: Red");
		}
		else{
			toBeSent += "Blue";
			System.out.println("Winner: Blue");
		}
		
		if (game.isGameFinished())
			serverReceiver.sendToAll(toBeSent);
		
		
	}
	
	public void endGame(){
		if (game.isGameFinished())
			serverReceiver.sendToAll("EndGame");
	}
	
	/*Getters and setters*/
	public GameMode getGame() {
		return game;
	}
	
	public ArrayList<ServerPlayer> getRedTeamPlayers(){
		return game.getFirstTeam().getMembers();
	}
	
	public ArrayList<ServerPlayer> getBlueTeamPlayers(){
		return game.getSecondTeam().getMembers();
	}
	
	public ArrayList<ServerPlayer> getAllPlayers(){
		ArrayList<ServerPlayer> redTeam = getRedTeamPlayers();
		ArrayList<ServerPlayer>  blueTeam = getBlueTeamPlayers();
		
		redTeam.addAll(blueTeam);
		
		return redTeam;
	}
}
