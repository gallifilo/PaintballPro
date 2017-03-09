package serverLogic;

import enums.TeamEnum;
import logic.GameMode;
import logic.RoundTimer;

/**
 * Creates a new game in the Team Match mode. In the Team Match mode, the teams
 * play against each other for a set amount of time. The winning team is the one
 * with the biggest number of points.
 *
 * @author Alexandra Paduraru
 */
public class TeamMatchMode extends GameMode {

	private RoundTimer timer;
	private static final long gameTime = 180; // in seconds
	
	//debugging
	private boolean debug = false;

	/**
	 * Initialises the game with two teams and starts the count-down.
	 *
	 * @param t1
	 *            The first team.
	 * @param t2
	 *            The second team.
	 */
	public TeamMatchMode(Team t1, Team t2) {
		super(t1, t2);
		if (debug) System.out.println("First team colour is : " + t1.getColour());
		if (debug) System.out.println("second team colour is : " + t2.getColour());
		timer = new RoundTimer(gameTime);
	}

	/**
	 * Checks if the game has finished.
	 */
	@Override
	public boolean isGameFinished() {
		return timer.isTimeElapsed();
	}

		/**
	 * Returns the winner team.
	 *
	 * @return The team who has won the game. The method returns null in case if
	 *         the game is a draw. However, the game should not stop, as 30 more
	 *         seconds are allocated for the game to finish.
	 */
	@Override
	public Team whoWon() {
		if (getRedTeam().getScore() > getBlueTeam().getScore())
			return getRedTeam();
		else if (getRedTeam().getScore() < getBlueTeam().getScore())
			return getBlueTeam();
		else{
			//allocate 30 more seconds to the game.
			timer = new RoundTimer(10);
			timer.startTimer();
			while (!timer.isTimeElapsed()){
				if (debug) System.out.println("Timer running for more " + timer.getTimeLeft() + " seconds ...");
			}
			return whoWon();
		}
	}

	@Override
	public void start() {
		timer.startTimer();
	}
	
	public RoundTimer getTimer(){
		return timer;
	}

	@Override
	public long getRemainingTime() {
		return timer.getTimeLeft();
	}
	
}
