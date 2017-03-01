package serverLogic;

import java.util.ArrayList;

import enums.TeamEnum;
import players.ServerPlayer;
import rendering.Map;

/**
 * Class to represent a team of players in the game.
 * @author Alexandra Paduraru
 *
 */
public class Team {

	private ArrayList<ServerPlayer> members;
	private int score;
	private TeamEnum colour;

	/**
	 * Initialises a new empty team, with 0 members and no score.
	 */
	public Team(){
		members = new ArrayList<>();
		score = 0;
	}

	/**
	 * Increments the score of the team with a given number of points
	 * @param additionalScore The new points gained by the team
	 */
	public void incrementScore(int additionalScore){
		score += additionalScore;
	}

	/**
	 * Adds another player to the team and increments the number of team players.
	 * @param p The new team player.
	 */
	public void addMember(ServerPlayer p){
		members.add(p);
		colour = p.getColour();
	}

	/**
	 * Adds players as a member of the team.
	 * @param teamPlayers The array of players to team.
	 */
	public void setMembers(ArrayList<ServerPlayer> teamPlayers) {
		for (ServerPlayer p : teamPlayers)
			addMember(p);
		colour = teamPlayers.get(0).getColour();
	}
//	public void updatePlayerLocation(ClientPlayer p, int newXCoord, int newYCoord){
//		p.getsetXCoord(newXCoord);
//		p.setYCoord(newYCoord);
//	}

	// Probably not needed here.
//	 /**
//	 * Change one of the team player's location.
//	 * @param p The player to be moved.
//	 * @param newXCoord The new x coordinate of the player.
//	 * @param newYCoord The new y coordinate of the player.
//	 */
//	public void updatePlayerLocation(Player p, int newXCoord, int newYCoord){
//		p.setXCoordinate(newXCoord);
//		p.setYCoordinate(newYCoord);
//	}

	/* Getters and setters */

	public int getMembersNo() {
		return members.size();
	}

	public int getScore() {
		return score;
	}

	public void setScore(int newScore){
		score = newScore;
	}


	public ArrayList<ServerPlayer> getMembers(){
		return members;
	}

	public TeamEnum getColour(){
		return colour;
	}

//	public void setMap(Map map){
//		for (ServerPlayer p : members){
//			p.setMap(map);
//		}
//	}
	
	public void setColour(TeamEnum c){
		colour = c;
	}


}
