package rendering;

import enums.TeamEnum;
import gui.GUIManager;
import integrationClient.ClientInputSender;
import javafx.animation.AnimationTimer;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import logic.GameMode;
import networking.client.ClientReceiver;
import physics.*;
import players.ClientPlayer;
import players.EssentialPlayer;
import players.GhostPlayer;
import players.OfflinePlayer;
import serverLogic.CaptureTheFlagMode;
import serverLogic.Team;
import serverLogic.TeamMatchMode;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Random;

import static players.EssentialPlayer.PLAYER_HEAD_X;
import static players.EssentialPlayer.PLAYER_HEAD_Y;

/**
 * A scene of a game instance. All assets are drawn on a <i>view</i> pane. There are two instances of <code>SubScene</code> for the pause menu and the settings menu, and a <code>SubScene</code> for the in-game head up display.
 *
 * @author Artur Komoter
 */
public class Renderer extends Scene
{
	static Pane view = new Pane();

	ClientPlayer cPlayer;
	OfflinePlayer player;
	private PauseMenu pauseMenu;
	private PauseSettingsMenu settingsMenu;
	private HeadUpDisplay hud;
	private Map map;
	private int paintIndex;
	private AnimationTimer timer;
	private GUIManager guiManager;
	private boolean singlePlayer = false;

	/**
	 * Renders an offline game instance by loading the selected map, spawning the AI players and responding to changes in game logic.
	 *
	 * @param mapName    Name of the selected map
	 * @param guiManager GUI manager that creates this object
	 */
	public Renderer(String mapName, GUIManager guiManager)
	{
		super(view, guiManager.getStage().getWidth(), guiManager.getStage().getHeight());
		this.guiManager = guiManager;
		init(mapName);
		singlePlayer = true;

		CollisionsHandler collisionsHandler = new CollisionsHandler(map);
		InputHandler inputHandler = new InputHandler();
		KeyPressListener keyPressListener = new KeyPressListener(inputHandler);
		KeyReleaseListener keyReleaseListener = new KeyReleaseListener(inputHandler);
		MouseListener mouseListener = new MouseListener(inputHandler);
		setOnKeyPressed(keyPressListener);
		setOnKeyReleased(keyReleaseListener);
		setOnMouseDragged(mouseListener);
		setOnMouseMoved(mouseListener);
		setOnMousePressed(mouseListener);
		setOnMouseReleased(mouseListener);

		player = new OfflinePlayer(map.getSpawns()[0].x * 64, map.getSpawns()[0].y * 64, 0, map, guiManager, TeamEnum.RED, collisionsHandler, inputHandler, map.getGameMode());
		ArrayList<EssentialPlayer> players = new ArrayList<>();

		players.addAll(player.getMyTeam().getMembers());
		players.addAll(player.getOppTeam().getMembers());

		view.getChildren().addAll(players);
		collisionsHandler.setPlayers(players);

		hud = new HeadUpDisplay(guiManager, player.getTeam());
		view.getChildren().add(hud);
		hud.toFront();

		if(map.gameMode == enums.GameMode.CAPTURETHEFLAG)
		{
			map.flag = new Flag(map.flagLocations);
			view.getChildren().add(map.flag);
		}

		map.powerups = new Powerup[] { new Powerup(PowerupType.SHIELD, map.powerupLocations), new Powerup(PowerupType.SPEED, map.powerupLocations)
		};
		view.getChildren().addAll(map.powerups);

		GameMode gameLoop = initGame(player);
		gameLoop.start();
		
		if(map.gameMode == enums.GameMode.CAPTURETHEFLAG)
		{
				map.flag = new Flag(map.flagLocations);
				view.getChildren().add(map.flag);
		}
		map.powerups = new Powerup[] { new Powerup(PowerupType.SHIELD, map.powerupLocations), new Powerup(PowerupType.SPEED, map.powerupLocations)
			};
		view.getChildren().addAll(map.powerups);

		timer = new AnimationTimer()
		{
			@Override
			public void handle(long now)
			{
				for(EssentialPlayer player : players)
				{
					for(Bullet pellet : player.getBullets())
					{
						if(pellet.isActive())
						{
							if(!view.getChildren().contains(pellet))
								view.getChildren().add(view.getChildren().size() - 2, pellet);
						}
						else if(view.getChildren().contains(pellet))
						{
							if(pellet.getCollision() != null)
								generateSpray(pellet, player.getTeam());
							view.getChildren().remove(pellet);
						}
					}
					player.tick();
				}
				hud.tick(gameLoop.getRemainingTime());
				if(gameLoop.getRemainingTime() == 0)
					hud.setWinner(gameLoop.getRedTeam().getScore(), gameLoop.getBlueTeam().getScore());

				hud.setScore(TeamEnum.RED, gameLoop.getRedTeam().getScore());
				hud.setScore(TeamEnum.BLUE, gameLoop.getBlueTeam().getScore());
				updateView();
			}
		};
		timer.start();
	}


	/**
	 * Renders an online game instance by loading the selected map, receiving data from the client receiver and responding to changes in game logic.
	 *
	 * @param mapName    Name of the selected map
	 * @param receiver   Client receiver for communication with the game server
	 * @param guiManager GUI manager that creates this object
	 * @param flag       Flag object for Capture the Flag gamemode
	 */
	public Renderer(String mapName, ClientReceiver receiver, GUIManager guiManager, Flag flag)
	{
		super(view, guiManager.getStage().getWidth(), guiManager.getStage().getHeight());
		this.guiManager = guiManager;
		init(mapName);

		cPlayer = receiver.getClientPlayer();
		ArrayList<GhostPlayer> players = receiver.getAllPlayers();
		view.getChildren().add(cPlayer);
		view.getChildren().addAll(receiver.getMyTeam());
		receiver.getMyTeam().forEach(player -> view.getChildren().add(player.getNameTag()));
		view.getChildren().addAll(receiver.getEnemies());
		receiver.getEnemies().forEach(player -> view.getChildren().add(player.getNameTag()));

		InputHandler inputHandler = new InputHandler();
		KeyPressListener keyPressListener = new KeyPressListener(inputHandler);
		KeyReleaseListener keyReleaseListener = new KeyReleaseListener(inputHandler);
		MouseListener mouseListener = new MouseListener(inputHandler);
		setOnKeyPressed(keyPressListener);
		setOnKeyReleased(keyReleaseListener);
		setOnMouseDragged(mouseListener);
		setOnMouseMoved(mouseListener);
		setOnMousePressed(mouseListener);
		setOnMouseReleased(mouseListener);

		cPlayer.setInputHandler(inputHandler);

		if(flag != null)
			view.getChildren().add(flag);

		hud = new HeadUpDisplay(guiManager, cPlayer.getTeam());
		view.getChildren().add(hud);
		hud.toFront();

		ClientInputSender inputSender = new ClientInputSender(receiver.getUdpClient(), inputHandler, cPlayer);
		inputSender.startSending();
		Group displayBullets = new Group();
		view.getChildren().add(displayBullets);

		timer = new AnimationTimer()
		{
			@Override
			public void handle(long now)
			{
				cPlayer.tick();
				for(GhostPlayer player : players)
				{
					for(GhostBullet pellet : player.getFiredBullets())
					{
						if(!displayBullets.getChildren().contains(pellet))
							displayBullets.getChildren().add(pellet);
					}
				}
				updateView();
			}
		};
		timer.start();
	}

	private GameMode initGame(OfflinePlayer player)
	{
		Team red = player.getMyTeam(), blue = player.getOppTeam();
		switch(map.getGameMode())
		{
			case ELIMINATION:
				return new TeamMatchMode(red, blue);
			case CAPTURETHEFLAG:
				return new CaptureTheFlagMode(red, blue);
			default:
				throw new NoSuchElementException("Gamemode doesn't exist");
		}
	}

	/**
	 * Toggles the pause menu whilst in-game
	 */
	public void togglePauseMenu()
	{
		if(!pauseMenu.opened)
			view.getChildren().add(pauseMenu);
		else
			view.getChildren().remove(pauseMenu);
		pauseMenu.opened = !pauseMenu.opened;
	}

	/**
	 * Toggles the settings scene from the pause menu whilst in-game
	 */
	public void toggleSettingsMenu()
	{
		if(!settingsMenu.opened)
		{
			view.getChildren().remove(pauseMenu);
			view.getChildren().add(settingsMenu);
		}
		else
		{
			view.getChildren().remove(settingsMenu);
			view.getChildren().add(pauseMenu);
		}
		settingsMenu.opened = !settingsMenu.opened;
	}

	/**
	 * Get the current state of the pause menu
	 *
	 * @return <code>true</code> if the pause menu is active, <code>false</code> otherwise
	 */
	public boolean getPauseMenuState()
	{
		return pauseMenu.opened;
	}

	/**
	 * Get the current state of the settings scene in the pause menu
	 *
	 * @return <code>true</code> if the settings scene is active, <code>false</code> otherwise
	 */
	public boolean getSettingsMenuState()
	{
		return settingsMenu.opened;
	}

	public Map getMap()
	{
		return map;
	}
	
	public HeadUpDisplay getHud()
	{
		return hud;
	}

	public void destroy()
	{
		timer.stop();
		view = new Pane();
		PauseMenu.p = new GridPane();
		PauseSettingsMenu.p = new GridPane();
		HeadUpDisplay.view = new BorderPane();
	}

	private void generateSpray(Bullet pellet, TeamEnum team)
	{
		WritableImage paint = new WritableImage(64, 64);
		PixelWriter pixelWriter = paint.getPixelWriter();
		Random random = new Random();
		double probability = 0.1;
		for(int i = 1; i < 63; i++)
		{
			for(int j = 1; j < 63; j++)
				if(random.nextDouble() < probability)
					pixelWriter.setArgb(i, j, (team == TeamEnum.RED ? java.awt.Color.RED : java.awt.Color.BLUE).getRGB());
		}
		ImageView imageView = new ImageView(paint);
		imageView.relocate(pellet.getCollision().getX(), pellet.getCollision().getY());
		imageView.setCache(true);
		view.getChildren().add(paintIndex, imageView);
	}

	private void init(String mapName)
	{
		setFill(Color.BLACK);
		setCursor(Cursor.CROSSHAIR);
		view.setStyle("-fx-background-color: black;");
		view.getStylesheets().add("styles/menu.css");
		String[] resolution = GUIManager.getUserSettings().getResolution().split("x");
		view.setScaleX(Double.parseDouble(resolution[0]) / 1024);
		view.setScaleY(Double.parseDouble(resolution[1]) / 576);
		pauseMenu = new PauseMenu(guiManager);
		settingsMenu = new PauseSettingsMenu(guiManager);
		map = Map.load(mapName);
		paintIndex = view.getChildren().size();
	}

	private void updateView()
	{
		double playerLayoutX = (singlePlayer ? player : cPlayer).getLayoutX(), playerLayoutY = (singlePlayer ? player : cPlayer).getLayoutY();

		view.setLayoutX(((getWidth() / 2) - PLAYER_HEAD_X - playerLayoutX) * view.getScaleX());
		view.setLayoutY(((getHeight() / 2) - PLAYER_HEAD_Y - playerLayoutY) * view.getScaleY());
		hud.relocate((playerLayoutX + PLAYER_HEAD_X - getWidth() / 2) * hud.getScaleX(), (playerLayoutY + PLAYER_HEAD_Y - getHeight() / 2) * hud.getScaleY());

		if(view.getChildren().contains(pauseMenu))
			pauseMenu.relocate(playerLayoutX + PLAYER_HEAD_X - getWidth() / 2, playerLayoutY + PLAYER_HEAD_Y - getHeight() / 2);
		if(view.getChildren().contains(settingsMenu))
			settingsMenu.relocate(playerLayoutX + PLAYER_HEAD_X - getWidth() / 2, playerLayoutY + PLAYER_HEAD_Y - getHeight() / 2);
	}
}