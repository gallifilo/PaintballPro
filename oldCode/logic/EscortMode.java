package oldCode.logic;

import logic.GameMode;
import logic.server.Team;

public class EscortMode extends GameMode {

	public EscortMode(Team t1, Team t2) {
		super(t1, t2);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isGameFinished() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Team whoWon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getRemainingTime() {
		// TODO Auto-generated method stub
		return 0;
	}

}
