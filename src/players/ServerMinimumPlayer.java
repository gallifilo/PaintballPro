package players;
import java.util.ArrayList;
import java.util.List;

import audio.AudioManager;
import enums.TeamEnum;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;
import logic.GameObject;
import physics.Bullet;
import physics.CollisionsHandler;
import physics.CollisionsHandlerGeneralPlayer;
import rendering.Map;
import serverLogic.Team;
/**
 *  The player, represented by an ImageView
 */
public abstract class ServerMinimumPlayer implements GameObject{

	protected double x, y;
	public static final double playerHeadX = 12.5, playerHeadY = 47.5;
	protected final double movementSpeed = 2;
	protected static long shootDelay = 450;
	protected static long spawnDelay = 2000;
	protected double width, height;
	protected boolean up, down, left, right, shoot, eliminated, invincible, visible;
	protected boolean collUp, collDown, collLeft, collRight;
	protected double angle, lastAngle;
	protected double mouseX, mouseY;
	protected ArrayList<Bullet> firedBullets = new ArrayList<Bullet>();
	protected Rotate rotation;
	protected Map map;
	protected int id;
	protected long shootTimer, spawnTimer;
	protected double lastX, lastY;
	protected TeamEnum team;
	protected Polygon bounds = new Polygon();
	protected CollisionsHandler collisionsHandler;

	/**
	 * Create a new player at the set location, and adds the rotation property to the player,
	 * this a General class for the Client Side which needs to store the Image
	 * @param x The x-coordinate of the player with respect to the map
	 * @param y The y-coordinate of the player with respect to the map
	 * @param id The id of the player
	 * @param map The map in which the player is playing
	 * @param Team The team of the player
	 *
	 */
	public ServerMinimumPlayer(double x, double y, int id, double width, double height,  Map map, TeamEnum team, CollisionsHandler collisionsHandler){
		this.x = x;
		this.y = y;
		this.lastX = x;
		this.lastY = y;
		this.lastAngle = angle;
		this.team = team;
		this.id = id;
		rotation = new Rotate(Math.toDegrees(angle), 0, 0, 0, Rotate.Z_AXIS);
		rotation.setPivotX(playerHeadX);
		rotation.setPivotY(playerHeadY);
		this.map = map;
		eliminated = false;
		invincible = false;
		visible = true;
		this.collisionsHandler = collisionsHandler;
		updatePlayerBounds();
		bounds.getTransforms().add(rotation);

	}

	protected abstract void updatePosition();

	protected void updateShooting(){
		if(shoot && shootTimer < System.currentTimeMillis() - shootDelay){
			shoot();
			shootTimer = System.currentTimeMillis();
		}
	}

	//Updates the location of the bullets
	protected void updateBullets(){
		for(int i = 0; i < firedBullets.size(); i++){
			firedBullets.get(i).moveInDirection();
		}
	}

	//Calculates the angle the player is facing with respect to the mouse
	protected abstract void updateAngle();

	/**
	 * Method to update the team score once a player has been eliminated.
	 * Will be implemented differently depending on the player type:
	 * 		- The client player will have to send the updated information to the server
	 * 		- The offline player will have to update the internal score of the team
	 * 		- The AI player will have to adjust its behaviour depending on the the game type (single player or online)
	 *
	 */
	public abstract void updateScore();

	protected void checkSpawn() {
		if(spawnTimer + spawnDelay <= System.currentTimeMillis()){
			int i = 0;
			if(team == TeamEnum.BLUE) i = 4;
			x = map.getSpawns()[i].x * 64 ;
			y = map.getSpawns()[i].y * 64 ;
			eliminated = false;
			invincible = true;
			spawnTimer = System.currentTimeMillis();
			updatePosition();
			visible = true;
		}
	}

	protected void checkInvincibility() {
		//Invincible animation
		if(spawnTimer + spawnDelay > System.currentTimeMillis()){
			if(System.currentTimeMillis() >= spawnTimer + spawnDelay/8 && System.currentTimeMillis() < spawnTimer + 2 * spawnDelay/8)
				visible = false;
			if(System.currentTimeMillis() >= spawnTimer + 2* spawnDelay/8 && System.currentTimeMillis() < spawnTimer + 3* spawnDelay/8)
				visible = true;
			if(System.currentTimeMillis() >= spawnTimer + 3* spawnDelay/8 && System.currentTimeMillis() < spawnTimer + 4* spawnDelay/8)
				visible = false;
			if(System.currentTimeMillis() >= spawnTimer + 4* spawnDelay/8 && System.currentTimeMillis() < spawnTimer + 5* spawnDelay/8)
				visible = true;
			if(System.currentTimeMillis() >= spawnTimer + 5* spawnDelay/8 && System.currentTimeMillis() < spawnTimer + 6* spawnDelay/8)
				visible = false;
			if(System.currentTimeMillis() >= spawnTimer + 6* spawnDelay/8 && System.currentTimeMillis() < spawnTimer + 7* spawnDelay/8)
				visible = true;
			if(System.currentTimeMillis() >= spawnTimer + 7* spawnDelay/8 && System.currentTimeMillis() < spawnTimer + 8* spawnDelay/8)
				visible = false;

		} else {
			invincible = false;
			visible = true;

		}
	}

	//Consists of 5 points around player
	public void updatePlayerBounds(){
		//Point1
		double x1 = (83 * width/120) - playerHeadX;
		double y1 = (5 * height/255) - playerHeadY;
		double x2 = x1 * Math.cos(angle) - y1 * Math.sin(angle);
		double y2 = x1 * Math.sin(angle) + y1 * Math.cos(angle);
		double boundx1 = x + x2 + playerHeadX;
		double boundy1 = y + y2 + playerHeadY;
		//Point2
		x1 = (width) - playerHeadX;
		y1 = (233 * height/255) - playerHeadY;
		x2 = x1 * Math.cos(angle) - y1 * Math.sin(angle);
		y2 = x1 * Math.sin(angle) + y1 * Math.cos(angle);
		double boundx2 = x + x2 + playerHeadX;
		double boundy2 = y + y2 + playerHeadY;
		//Point3
		x1 = (57 * width/120) - playerHeadX;
		y1 = (height) - playerHeadY;
		x2 = x1 * Math.cos(angle) - y1 * Math.sin(angle);
		y2 = x1 * Math.sin(angle) + y1 * Math.cos(angle);
		double boundx3 = x + x2 + playerHeadX;
		double boundy3 = x + y2 + playerHeadY;
		//Point4
		x1 = (1 * width/120) - playerHeadX;
		y1 = (183 * height/255) - playerHeadY;
		x2 = x1 * Math.cos(angle) - y1 * Math.sin(angle);
		y2 = x1 * Math.sin(angle) + y1 * Math.cos(angle);
		double boundx4 = x + x2 + playerHeadX;
		double boundy4 = y + y2 + playerHeadY;
		//Point5
		x1 = (1 * width/120) - playerHeadX;
		y1 = (128 * height/255) - playerHeadY;
		x2 = x1 * Math.cos(angle) - y1 * Math.sin(angle);
		y2 = x1 * Math.sin(angle) + y1 * Math.cos(angle);
		double boundx5 = x + x2 + playerHeadX;
		double boundy5 = y + y2 + playerHeadY;
		bounds.getPoints().clear();
		bounds.getPoints().addAll(boundx1, boundy1,
				boundx2, boundy2,
				boundx3, boundy3,
				boundx4, boundy4,
				boundx5, boundy5);

	}


	/**
	 * Creates a bullet at the player's location that travels in the direction the player is facing.
	 * The bullet is added to the arraylist "firedBullets"
	 * It is called every time the player presses the left mouse button
	 */
	public void shoot(){

		double x1 = (83 * width/120) - playerHeadX;
		double y1 = (12 * height/255) - playerHeadY;

		double x2 = x1 * Math.cos(angle) - y1 * Math.sin(angle);
		double y2 = x1 * Math.sin(angle) + y1 * Math.cos(angle);

		double bulletX = x + x2 + playerHeadX;
		double bulletY = y + y2 + playerHeadY;

		Bullet bullet = new Bullet(bulletX, bulletY, angle, team);

		firedBullets.add(bullet);
	}

	public void beenShot() {
		spawnTimer = System.currentTimeMillis();
		eliminated = true;
		visible = false;
		updateScore();
	}


	//Getters and setters below this point
	//-----------------------------------------------------------------------------

	public double getX(){
		return this.x;
	}

	public double getY(){
		return this.y;
	}

	public void setX(double x){
		this.x = x;
	}

	public void setY(double y){
		this.y = y;
	}

	public double getWidth(){
		return this.width;
	}

	public double getHeight(){
		return this.height;
	}

	public List<Bullet> getBullets(){
		return this.firedBullets;
	}

	public double getAngle(){
		return this.angle;
	}

	public void setAngle(double angle){
		this.angle = angle;
	}

	public synchronized void setUp(boolean up){
		this.up = up;
	}

	public synchronized void setDown(boolean down){
		this.down = down;
	}

	public synchronized void setLeft(boolean left){
		this.left = left;
	}

	public synchronized void setRight(boolean right){
		this.right = right;
	}

	public synchronized void setShoot(boolean shoot){
		this.shoot = shoot;
	}

	public TeamEnum getTeam() {
		return team;
	}

	public int getPlayerId(){
		return id;
	}

	public synchronized void setMouseX(double newX) {
		mouseX = newX;
	}
	public synchronized void setMouseY(double newY){
		mouseY = newY;
	}

	public boolean isEliminated(){
		return eliminated;
	}

	public Polygon getPolygonBounds() {
		return bounds;
	}

	public void setCollUp(boolean collUp) {
		this.collUp = collUp;
	}
	public void setCollDown(boolean collDown) {
		this.collDown = collDown;
	}
	public void setCollLeft(boolean collLeft) {
		this.collLeft = collLeft;
	}
	public void setCollRight(boolean collRight) {
		this.collRight = collRight;
	}
	
	public TeamEnum getColour(){
		return team;
	}


}
