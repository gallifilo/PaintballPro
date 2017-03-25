package physics;

import enums.TeamEnum;
import gui.GUIManager;
import integration.server.GameUpdateListener;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import logic.server.CaptureTheFlagMode;
import logic.server.Team;
import players.EssentialPlayer;
import rendering.Map;

import java.util.ArrayList;
import java.util.List;

import static gui.GUIManager.renderer;

/**
 *
 * @author gallifilippo
 *
 */
public class CollisionsHandler
{
	private static final boolean debug = false;

	public boolean isLocal = false;

	private ArrayList<Rectangle> propsWalls;
	private Rectangle spawnAreaRed;
	private Rectangle spawnAreaBlue;
	private ArrayList<EssentialPlayer> redTeam;
	private ArrayList<EssentialPlayer> blueTeam;
	private Flag flag;
	private Powerup[] powerups;

	private Team red;
	private Team blue;

	private GameUpdateListener listener;

	public CollisionsHandler(Map map)
	{
		propsWalls = map.getRecProps();
		propsWalls.addAll(map.getRecWalls());
		redTeam = new ArrayList<>();
		blueTeam = new ArrayList<>();
		red = new Team(TeamEnum.RED);
		blue = new Team(TeamEnum.BLUE);
		spawnAreaBlue = map.getRecSpawn(TeamEnum.BLUE);
		spawnAreaRed = map.getRecSpawn(TeamEnum.RED);
		flag = map.getFlag();
		powerups = map.getPowerups();
	}

	public void handlePropWallCollision(EssentialPlayer p){
		boolean collUp = false;
		boolean collDown = false;
		boolean collRight = false;
		boolean collLeft = false;
		ArrayList<Double> angles = new ArrayList<>();
		double playerCenterX = p.getLayoutX() + p.getImage().getWidth() / 2;
		double playerCenterY = p.getLayoutY() + p.getImage().getHeight() / 2;

		for(Rectangle propWall : propsWalls)
		{
			double propX = propWall.getX();
			double propY = propWall.getY();

			for(Pellet pellet : p.getBullets())
			{
				if(pellet.getBoundsInParent().intersects(propWall.getBoundsInParent())){
					pellet.disable(propWall);
				}

			}

			//filter out walls and props far away from the player
			if(Math.abs(propX - p.getLayoutX()) > 72 || Math.abs(propY - p.getLayoutY()) > 72)
				continue;

			//System.out.println("collsionsss maybee");
			//it returns a path with the collision with walls
			//System.out.println("bound player " + p.getPlayerId() + " : " + p.getPolygonBounds().toString());
			Path tmp = (Path) Shape.intersect(p.getPolygonBounds(), propWall);
			if(!tmp.getBoundsInLocal().isEmpty())
			{
				//System.out.println("Collision of :" +  propWall.toString()+  " and " + p.getPolygonBounds());
				double propWidth = propWall.getWidth();
				double propHeight = propWall.getHeight();

				//find angle between center of player and center of the prop
				//MoveTo moveToValues = (MoveTo) tmp.getElements().get(0);
				double propCenterX = (propX + propWidth / 2);
				double propCenterY = (propY + propHeight / 2);
				double deltax = propCenterX - playerCenterX;
				double deltay = playerCenterY - propCenterY;

				double tempAngle = Math.atan2(deltay, deltax);
				double propAngle = Math.toDegrees(tempAngle);

				angles.add(propAngle);
				if(propAngle < 135 && propAngle >= 45)
				{
					collUp = true;
				}
				if(propAngle < 45 && propAngle >= -45)
				{
					collRight = true;
				}
				if(propAngle < -45 && propAngle >= -135)
				{
					collDown = true;
				}
				if(propAngle < -135 || propAngle >= 135)
				{
					collLeft = true;
				}
			}
		}
		if(!angles.isEmpty())
		{
			double mean = getMeanAngle(angles);
			//System.out.println("mean: " + mean);
			if(mean < 135 && mean >= 45)
			{
				collUp = true;
			}
			if(mean < 45 && mean >= -45)
			{
				collRight = true;
			}
			if(mean < -45 && mean >= -135)
			{
				collDown = true;
			}
			if(mean < -135 || mean >= 135)
			{
				collLeft = true;
			}
		}
		//System.out.println("CollisionsHandler " + p.getPlayerId() + " collup :" + collUp + " collDown:" + collDown + " collLeft:" + collLeft + " collRight: " + collRight );

		p.setCollUp(collUp);
		p.setCollDown(collDown);
		p.setCollLeft(collLeft);
		p.setCollRight(collRight);

	}

	public void handleBulletCollision(EssentialPlayer p)
	{
		switch(p.getTeam())
		{
			case RED:
			{
				checkBulletsAgainstATeam(p, blueTeam);
				break;
			}
			case BLUE:
			{
				checkBulletsAgainstATeam(p, redTeam);
				break;
			}
			default:
				//System.out.println("the player does not have a team");
				break;
		}

	}

	public void handleFlagCollision(EssentialPlayer p){
		if(flag != null){

			//check if the player touches the flag
			if(!flag.isCaptured() &&
					p.getPolygonBounds().getBoundsInParent().intersects(flag.getBoundsInParent()) &&
					!p.isEliminated()){
				if(debug) System.out.println("Caught the flag");
				if (isLocal) GUIManager.audio.playSFX(GUIManager.audio.sfx.flagcollect, (float)1.0);
				flag.setFlagCarrier(p);
				flag.setCaptured(true);
				flag.setVisible(false);
				p.setHasFlag(true);
				if(renderer != null)
					renderer.getHud().setFlagStatus(p.getTeam());

				if(listener != null)
					listener.onFlagCaptured(p.getPlayerId());

			}
			//check if the player got shot so leave the flag in the player position
			if(p.isEliminated() && p.hasFlag()){
				if(debug) System.out.println("Dropped the flag");
				flag.relocate(p.getLayoutX(), p.getLayoutY());
				flag.setCaptured(false);
				flag.setVisible(true);
				if(renderer != null)
					renderer.getHud().setFlagStatus(p.getTeam());
				if(listener != null)
					listener.onFlagDropped(p.getPlayerId());
				p.setHasFlag(false);

//				if (red.containsPlayer(p))
//					blue.incrementScore(CaptureTheFlagMode.LOST_FLAG_SCORE);
//				else
//					red.incrementScore(CaptureTheFlagMode.LOST_FLAG_SCORE);

			//check if the player has brought the flag back to his base
			}if(p.hasFlag()){
				boolean baseTouched = false;
				switch(p.getTeam())
				{
					case RED: baseTouched = p.getPolygonBounds().getBoundsInParent().intersects(spawnAreaRed.getBoundsInParent());
						break;
					case BLUE: baseTouched = p.getPolygonBounds().getBoundsInParent().intersects(spawnAreaBlue.getBoundsInParent());
						break;
					default: break;
				}
				if(baseTouched && !p.isEliminated()){
					if(debug) System.out.println("Brought the flag back");
					flag.resetPosition();
					flag.setCaptured(false);
					flag.setVisible(true);
					p.setHasFlag(false);

					if (red.containsPlayer(p))
						red.incrementScore(CaptureTheFlagMode.FLAG_SCORE);
					else
						blue.incrementScore(CaptureTheFlagMode.FLAG_SCORE);
					if(renderer != null)
						renderer.getHud().setFlagStatus(p.getTeam());
					if(listener != null)
						listener.onFlagRespawned(p.getPlayerId());
				}
			}
		}
	}

	public void handlePowerUpCollision(EssentialPlayer p){

		for(Powerup powerup : powerups)
		{
			if(p.getPolygonBounds().getBoundsInParent().intersects(powerup.getBoundsInParent()))
			{
				if(powerup.isVisible())
				{
					powerup.take();
					if (isLocal) GUIManager.audio.playSFX(GUIManager.audio.sfx.pickup, (float)1.0);
					if(powerup.getType() == PowerupType.SHIELD)
					{
						p.setShield(true);
						if(listener != null)
							listener.onPowerupAction(PowerupType.SHIELD, p.getPlayerId());
					}
					else if(powerup.getType() == PowerupType.SPEED)
					{
						p.setSpeed(true);
						if(listener != null)
							listener.onPowerupAction(PowerupType.SPEED, p.getPlayerId());
					}
				}
			}
		}
	}

	private void checkBulletsAgainstATeam(EssentialPlayer p, ArrayList<EssentialPlayer> opponents){
		for(EssentialPlayer enemy : opponents){

			for(Pellet pellet : enemy.getBullets())
			{
				if(pellet.isActive() && p.getPolygonBounds().getBoundsInParent().intersects(pellet.getBoundsInParent()) && !p.isEliminated())
				{

					//System.out.println("Been shot ");
					pellet.disable();
					if(listener != null){
						listener.onBulletKills(enemy.getPlayerId(), pellet.getPelletId());
					}
					//check if the player has the shield power up
					if(p.getShieldActive()){
						//shield absorbs a bullet
						p.setShield(false);
						if(listener != null)
							listener.onPowerupAction(PowerupType.SHIELD, p.getPlayerId());
					} else {
						//if the player has no shield, the player is eliminated
						p.beenShot();
					}
					return;
				}
			}
		}
	}

	// source file can be found here https://rosettacode.org/wiki/Averages/Mean_angle#Java
  private double getMeanAngle(List<Double> sample) {

    double x_component = 0.0;
    double y_component = 0.0;
    double avg_d, avg_r;

    for (double angle_d : sample) {
      double angle_r;
      angle_r = Math.toRadians(angle_d);
      x_component += Math.cos(angle_r);
      y_component += Math.sin(angle_r);
    }
    x_component /= sample.size();
    y_component /= sample.size();
    avg_r = Math.atan2(y_component, x_component);
    avg_d = Math.toDegrees(avg_r);

    return avg_d;
  }

	public void setRedTeam(Team red) {
		this.red = red;
		redTeam = red.getMembers();
	}

	public void setBlueTeam(Team blue) {
		this.blue = blue;
		blueTeam = blue.getMembers();
	}

	public void setPlayers(ArrayList<EssentialPlayer> players){
		for(EssentialPlayer p : players)
		{
			if(p.getTeam() == TeamEnum.RED){
				redTeam.add(p);
				red.addMember(p);
			}
			else{
				blueTeam.add(p);
				blue.addMember(p);
			}

		}
	}

	public void setListener(GameUpdateListener listener)
	{
		this.listener = listener;
	}

	public Flag getFlag(){
		return flag;
	}

	public Powerup getSpeedPowerup(){
		return powerups[1];
	}

	public Powerup getShieldPowerup(){
		return powerups[0];
	}

}
