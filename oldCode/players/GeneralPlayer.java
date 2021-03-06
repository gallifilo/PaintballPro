package oldCode.players;
//package players;
//
//import audio.AudioManager;
//import enums.Team;
//import javafx.geometry.Insets;
//import javafx.scene.control.Label;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.shape.Polygon;
//import javafx.scene.shape.Rectangle;
//import javafx.scene.transform.Rotate;
//import logic.GameObject;
//import physics.Bullet;
//import physics.CollisionsHandler;
//import rendering.Map;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
///**
// *  The player, represented by an ImageView
// */
//public abstract class GeneralPlayer extends ImageView implements GameObject{
//
//	public static final double PLAYER_HEAD_X = 12.5, PLAYER_HEAD_Y = 47.5;
//	protected static long SHOOT_DELAY = 450;
//	protected static long SPAWN_DELAY = 2000;
//	protected final double MOVEMENT_SPEED = 2;
//	protected boolean up, down, left, right, shoot, eliminated, invincible;
//	protected boolean collUp, collDown, collLeft, collRight;
//	protected double angle, lastAngle;
//	protected ArrayList<Bullet> firedBullets = new ArrayList<Bullet>();
//	protected Rotate rotation, boundRotation;
//	protected Map map;
//	protected int id;
//	protected long shootTimer, spawnTimer;
//	protected double lastX, lastY;
//	protected Team team;
//	protected ArrayList<GeneralPlayer> enemies;
//	protected ArrayList<GeneralPlayer> teamPlayers;
//	protected Polygon bounds = new Polygon();
//	protected ArrayList<Rectangle> propsWalls;
//	protected boolean scoreChanged = false;
//	protected AudioManager audio;
//	protected CollisionsHandler collisionsHandler;
//	protected Random rand;
//	protected Label nameTag;
//
//	/**
//	 * Create a new player at the set location, and adds the rotation property to the player,
//	 * this a General class for the Client Side which needs to store the Image
//	 * @param x The x-coordinate of the player with respect to the map
//	 * @param y The y-coordinate of the player with respect to the map
//	 * @param id The id of the player
//	 * @param map The map in which the player is playing
//	 *
//	 */
//	public GeneralPlayer(double x, double y, int id, Map map, Team team, Image image, AudioManager audio, CollisionsHandler collisionsHandler){
//		super(image);
//		setLayoutX(x);
//		setLayoutY(y);
//		this.lastX = x;
//		this.lastY = y;
//		this.lastAngle = angle;
//		this.team = team;
//		this.id = id;
//		this.rand = new Random();
//		rotation = new Rotate(Math.toDegrees(angle), 0, 0, 0, Rotate.Z_AXIS);
//	    getTransforms().add(rotation);
//		rotation.setPivotX(PLAYER_HEAD_X);
//		rotation.setPivotY(PLAYER_HEAD_Y);
//		this.map = map;
//		propsWalls = map.getRecProps();
//	    propsWalls.addAll(map.getRecWalls());
//		eliminated = false;
//		invincible = false;
//		this.audio = audio;
//		this.collisionsHandler = collisionsHandler;
//		createPlayerBounds();
//		boundRotation = new Rotate(Math.toDegrees(angle), 0, 0, 0, Rotate.Z_AXIS);
//		bounds.getTransforms().add(boundRotation);
//		boundRotation.setPivotX(PLAYER_HEAD_X);
//		boundRotation.setPivotY(PLAYER_HEAD_Y);
//		updatePlayerBounds();
//
//		nameTag = new Label("Player");
//		nameTag.setStyle("-fx-background-color: rgba(64, 64, 64, 0.75);" +
//				"-fx-font-size: 10pt; -fx-text-fill: white");
//		nameTag.setPadding(new Insets(5));
//		nameTag.relocate(x - 15, y - 32);
//	}
//
//	/**
//	 * Constructor needed for the game logic.
//	 * @param x The x coordinate of the player.
//	 * @param y the y coordinate of the player.
//	 *
//	 * @ atp575
//	 */
//	public GeneralPlayer(double x, double y, int id, Image image) {
//		super(image);
//		setLayoutX(x);
//		setLayoutY(y);
//		this.id = id;
//	}
//
//	public GeneralPlayer(Image image) {
//		super(image);
//	}
//
//	protected abstract void updatePosition();
//
//	void updateShooting(){
//		if(shoot && shootTimer < System.currentTimeMillis() - SHOOT_DELAY){
//			shoot();
//			shootTimer = System.currentTimeMillis();
//		}
//	}
//
//	//Updates the location of the bullets
//	void updateBullets(){
//		for(Bullet firedBullet : firedBullets)
//			firedBullet.moveInDirection();
//	}
//
//	void cleanBullets(){
//		if(firedBullets.size() > 0) {
//			if (!firedBullets.get(0).isActive()) {
//				firedBullets.remove(0);
//			}
//		}
//	}
//
//	//Calculates the angle the player is facing with respect to the mouse
//	protected abstract void updateAngle();
//
//	/**
//	 * Method to update the team score once a player has been eliminated.
//	 * Will be implemented differently depending on the player type:
//	 * 		- The client player will have to send the updated information to the server
//	 * 		- The offline player will have to update the internal score of the team
//	 * 		- The AI player will have to adjust its behaviour depending on the the game type (single player or online)
//	 *
//	 */
//	public abstract void updateScore();
//
//	protected void checkSpawn() {
//		if(spawnTimer + SPAWN_DELAY <= System.currentTimeMillis()){
//			int i = 0;
//			if(team == Team.BLUE) i = 4;
//			setLayoutX(map.getSpawns()[i].x * 64);
//			setLayoutY(map.getSpawns()[i].y * 64);
//			eliminated = false;
//			invincible = true;
//			spawnTimer = System.currentTimeMillis();
//			updatePosition();
//			setVisible(true);
//		}
//	}
//
//	protected void checkInvincibility() {
//		//Invincible animation
//		if(spawnTimer + SPAWN_DELAY > System.currentTimeMillis()){
//			if(System.currentTimeMillis() >= spawnTimer + SPAWN_DELAY/8 && System.currentTimeMillis() < spawnTimer + 2 * SPAWN_DELAY/8)
//				setVisible(false);
//			if(System.currentTimeMillis() >= spawnTimer + 2* SPAWN_DELAY/8 && System.currentTimeMillis() < spawnTimer + 3* SPAWN_DELAY/8)
//				setVisible(true);
//			if(System.currentTimeMillis() >= spawnTimer + 3* SPAWN_DELAY/8 && System.currentTimeMillis() < spawnTimer + 4* SPAWN_DELAY/8)
//				setVisible(false);
//			if(System.currentTimeMillis() >= spawnTimer + 4* SPAWN_DELAY/8 && System.currentTimeMillis() < spawnTimer + 5* SPAWN_DELAY/8)
//				setVisible(true);
//			if(System.currentTimeMillis() >= spawnTimer + 5* SPAWN_DELAY/8 && System.currentTimeMillis() < spawnTimer + 6* SPAWN_DELAY/8)
//				setVisible(false);
//			if(System.currentTimeMillis() >= spawnTimer + 6* SPAWN_DELAY/8 && System.currentTimeMillis() < spawnTimer + 7* SPAWN_DELAY/8)
//				setVisible(true);
//			if(System.currentTimeMillis() >= spawnTimer + 7* SPAWN_DELAY/8 && System.currentTimeMillis() < spawnTimer + 8* SPAWN_DELAY/8)
//				setVisible(false);
//
//		} else {
//			invincible = false;
//			setVisible(true);
//			nameTag.setVisible(true);
//		}
//	}
//
//	//Consists of 5 points around player
//	public void createPlayerBounds(){
//		//Point1
//		double x1 = (83 * getImage().getWidth()/120) - PLAYER_HEAD_X;
//		double y1 = (5 * getImage().getHeight()/255) - PLAYER_HEAD_Y;
//		double x2 = x1 * Math.cos(angle) - y1 * Math.sin(angle);
//		double y2 = x1 * Math.sin(angle) + y1 * Math.cos(angle);
//		double boundx1 = x2 + PLAYER_HEAD_X;
//		double boundy1 = y2 + PLAYER_HEAD_Y;
//		//Point2
//		x1 = (getImage().getWidth()) - PLAYER_HEAD_X;
//		y1 = (233 * getImage().getHeight()/255) - PLAYER_HEAD_Y;
//		x2 = x1 * Math.cos(angle) - y1 * Math.sin(angle);
//		y2 = x1 * Math.sin(angle) + y1 * Math.cos(angle);
//		double boundx2 = x2 + PLAYER_HEAD_X;
//		double boundy2 = y2 + PLAYER_HEAD_Y;
//		//Point3
//		x1 = (57 * getImage().getWidth()/120) - PLAYER_HEAD_X;
//		y1 = (getImage().getHeight()) - PLAYER_HEAD_Y;
//		x2 = x1 * Math.cos(angle) - y1 * Math.sin(angle);
//		y2 = x1 * Math.sin(angle) + y1 * Math.cos(angle);
//		double boundx3 = x2 + PLAYER_HEAD_X;
//		double boundy3 = y2 + PLAYER_HEAD_Y;
//		//Point4
//		x1 = (1 * getImage().getWidth()/120) - PLAYER_HEAD_X;
//		y1 = (183 * getImage().getHeight()/255) - PLAYER_HEAD_Y;
//		x2 = x1 * Math.cos(angle) - y1 * Math.sin(angle);
//		y2 = x1 * Math.sin(angle) + y1 * Math.cos(angle);
//		double boundx4 = x2 + PLAYER_HEAD_X;
//		double boundy4 = y2 + PLAYER_HEAD_Y;
//		//Point5
//		x1 = (1 * getImage().getWidth()/120) - PLAYER_HEAD_X;
//		y1 = (128 * getImage().getHeight()/255) - PLAYER_HEAD_Y;
//		x2 = x1 * Math.cos(angle) - y1 * Math.sin(angle);
//		y2 = x1 * Math.sin(angle) + y1 * Math.cos(angle);
//		double boundx5 = x2 + PLAYER_HEAD_X;
//		double boundy5 = y2 + PLAYER_HEAD_Y;
//		bounds.getPoints().clear();
//		bounds.getPoints().addAll(boundx1, boundy1,
//				boundx2, boundy2,
//				boundx3, boundy3,
//				boundx4, boundy4,
//				boundx5, boundy5);
//	}
//
//	public void updatePlayerBounds(){
//		bounds.setLayoutX(getLayoutX());
//		bounds.setLayoutY(getLayoutY());
//		boundRotation.setAngle(Math.toDegrees(angle));
//	}
//
//
//
//	/**
//	 * Creates a bullet at the player's location that travels in the direction the player is facing.
//	 * The bullet is added to the arraylist "firedBullets"
//	 * It is called every time the player presses the left mouse button
//	 */
//	public void shoot(){
//
//		double x1 = (83 * getImage().getWidth()/120) - PLAYER_HEAD_X;
//		double y1 = (12 * getImage().getHeight()/255) - PLAYER_HEAD_Y;
//
//		double x2 = x1 * Math.cos(angle) - y1 * Math.sin(angle);
//		double y2 = x1 * Math.sin(angle) + y1 * Math.cos(angle);
//
//		double bulletX = getLayoutX() + x2 + PLAYER_HEAD_X;
//		double bulletY = getLayoutY() + y2 + PLAYER_HEAD_Y;
//
//		double bulletAngle = angle;
//		boolean sign= rand.nextBoolean();
//		double deviation = (double)rand.nextInt(100)/1000;
//		if(sign){
//			bulletAngle += deviation;
//		} else {
//            bulletAngle -= deviation;
//		}
//		Bullet bullet = new Bullet(bulletX, bulletY, bulletAngle, team);
//
//		firedBullets.add(bullet);
//	}
//
//	public void beenShot() {
//		audio.playSFX(audio.sfx.splat, (float) 1.0);
//		spawnTimer = System.currentTimeMillis();
//		eliminated = true;
//		setVisible(false);
//		nameTag.setVisible(false);
//		updateScore();
//	}
//
//
//	  public void addTeamPlayer(GeneralPlayer p){
//		  teamPlayers.add(p);
//	  }
//
//	  public void addEnemy(GeneralPlayer p){
//		  enemies.add(p);
//	  }
//
//	//Getters and setters below this point
//	//-----------------------------------------------------------------------------
//
//	public List<Bullet> getBullets(){
//		return this.firedBullets;
//	}
//
//	public double getAngle(){
//		return this.angle;
//	}
//
//	public void setAngle(double angle){
//		this.angle = angle;
//	}
//
//	public void setUp(boolean up){
//		this.up = up;
//	}
//
//	public void setDown(boolean down){
//		this.down = down;
//	}
//
//	public void setLeft(boolean left){
//		this.left = left;
//	}
//
//	public void setRight(boolean right){
//		this.right = right;
//	}
//
//	public void setShoot(boolean shoot){
//		this.shoot = shoot;
//	}
//
//	public Team getTeam() {
//		return team;
//	}
//
//	public ArrayList<GeneralPlayer> getEnemies(){
//		return this.enemies;
//	}
//
//	public void setEnemies(ArrayList<GeneralPlayer> enemies) {
//		this.enemies = enemies;
//	}
//
//	public int getPlayerId(){
//		return id;
//	}
//
//	public ArrayList<GeneralPlayer> getTeamPlayers(){
//		return teamPlayers;
//	}
//
//	public void setTeamPlayers(ArrayList<GeneralPlayer> teamPlayers) {
//		this.teamPlayers = teamPlayers;
//	}
//
//	public void setMX(double newX) {
//	}
//	public void setMY(double newY){
//	}
//
//	public Label getNameTag()
//	{
//		return nameTag;
//	}
//
//	public boolean isEliminated(){
//		return eliminated;
//	}
//
//	public boolean isInvincible() {return invincible;}
//	public Polygon getPolygonBounds() {
//		return bounds;
//	}
//
//	public void setCollUp(boolean collUp) {
//		this.collUp = collUp;
//	}
//	public void setCollDown(boolean collDown) {
//		this.collDown = collDown;
//	}
//	public void setCollLeft(boolean collLeft) {
//		this.collLeft = collLeft;
//	}
//	public void setCollRight(boolean collRight) {
//		this.collRight = collRight;
//	}
//
//
//}
//
