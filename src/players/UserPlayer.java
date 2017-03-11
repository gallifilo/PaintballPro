package players;

import java.util.ArrayList;


import enums.TeamEnum;
import javafx.scene.image.Image;
import physics.CollisionsHandler;
import rendering.Spawn;
import serverLogic.Team;
/**
 *  The player, represented by an ImageView
 */
public class UserPlayer extends EssentialPlayer{

    public final int widthScreen = 1024;
    public final int heightScreen = 576;

	public UserPlayer(double x, double y, int id, Spawn[] spawn, TeamEnum team,
			CollisionsHandler collisionsHandler, Image image) {
		super(x, y, id, spawn, team, collisionsHandler, image);
	}

	public void tick()
	{
		// handle the collisions with walls and props before moving the position
		// of the player so to understand if he can move or not in a specific direction
		collisionsHandler.handlePropWallCollision(this);
		if(!eliminated)
		{
			lastX = getLayoutX();
			lastY = getLayoutY();
			lastAngle = angle;
			updatePosition();
			updateShooting();
			updateAngle();
		}
		else
		{
			checkSpawn();
		}

		updatePlayerBounds();
		updateBullets();

		if(!invincible)
		{
			collisionsHandler.handleBulletCollision(this);
		}
		else
		{
			checkInvincibility();
		}
	}

	protected void updatePosition()
	{
		//System.out.println("collup: " + collUp + " collDown:" + collDown + " collLeft:" + collLeft + " collRight: " + collRight );
		if(up && !collUp){
			setLayoutY(getLayoutY() - movementSpeed);
		}else if(!up && collUp){
			setLayoutY(getLayoutY() + movementSpeed);
		}
		if(down && !collDown){
			setLayoutY(getLayoutY() + movementSpeed);
		}else if(!down && collDown){
			setLayoutY(getLayoutY() - movementSpeed);
		}
		if(left && !collLeft) {
			setLayoutX(getLayoutX() - movementSpeed);
		} else if(!left && collLeft){
			setLayoutX(getLayoutX() + movementSpeed);
		}
		if(right && !collRight){
			setLayoutX(getLayoutX() + movementSpeed);
		}else if (!right && collRight){
			setLayoutX(getLayoutX() - movementSpeed);
		}
	}


	//Calculates the angle the player is facing with respect to the mouse
	@Override
	protected void updateAngle()
	{
		double deltax = mouseX  - widthScreen/2;
		double deltay = heightScreen/2 + playerHeadY/6 -  mouseY ;
		angle = Math.atan2(deltax, deltay);
		double degrees = Math.toDegrees(angle);
		rotation.setAngle(degrees);


	}

	protected void updateShooting(){
		if(shoot && shootTimer < System.currentTimeMillis() - shootDelay){
			shoot();
			shootTimer = System.currentTimeMillis();
		}
	}


	@Override
	public void updateScore() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTeamPlayers(ArrayList<EssentialPlayer> team) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEnemies(ArrayList<EssentialPlayer> enemies) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setMyTeam(Team team) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setOppTeam(Team team) {
		// TODO Auto-generated method stub

	}


}

