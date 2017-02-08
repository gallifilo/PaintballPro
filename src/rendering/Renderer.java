package rendering;

import java.util.ArrayList;

import ai.AIPlayer;
import enums.Teams;

import javafx.animation.AnimationTimer;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import physics.*;

/**
 * A scene of a game instance. All assets are drawn on a <i>view</i> pane.
 *
 * @author Artur Komoter
 */
public class Renderer extends Scene
{
	static Pane view = new Pane();
	private Map map;
	private double scale = 1;
	private ArrayList<AIPlayer> bots = new ArrayList<AIPlayer>();

	/**
	 * Renders a game instance by loading the selected map, spawning the players and responding to changes in game logic.
	 *
	 * @param mapName Name of the selected map
	 */
	public Renderer(String mapName)
	{
		super(view, 1024, 576);
		super.setFill(Color.BLACK);
		setCursor(Cursor.CROSSHAIR);

		//16:9 aspect ratio
		widthProperty().addListener(observable ->
		{
			scale = getWidth() / 1024;
			view.setScaleX(scale);
			view.setScaleY((getWidth() * 0.5625) / 576);
		});

		map = Map.load("res/maps/" + mapName + ".json");

		Image playerImage = new Image("assets/player.png", 30, 64, true, true);
		Player player = new Player(map.spawns[0].x * 64, map.spawns[0].y * 64, "Me", false, this, Teams.RED, playerImage);
		view.getChildren().add(player);

		AIPlayer ai = new AIPlayer(map.spawns[1].x * 64, map.spawns[1].y * 64, "Bot1", this, Teams.BLUE, playerImage);
		view.getChildren().add(ai);
		bots.add(ai);

		AIPlayer ai2 = new AIPlayer(map.spawns[2].x * 64, map.spawns[2].y * 64, "Bot2", this, Teams.BLUE, playerImage);
		view.getChildren().add(ai2);
		bots.add(ai2);

		KeyPressListener keyPressListener = new KeyPressListener(player);
		KeyReleaseListener keyReleaseListener = new KeyReleaseListener(player);
		MouseListener mouseListener = new MouseListener(player);

		setOnKeyPressed(keyPressListener);
		setOnKeyReleased(keyReleaseListener);
		setOnMouseDragged(mouseListener);
		setOnMouseMoved(mouseListener);
		setOnMousePressed(mouseListener);
		setOnMouseReleased(mouseListener);

		new AnimationTimer()
		{
			@Override
			public void handle(long now)
			{
				player.tick();
				view.setLayoutX(((getWidth() / 2) - player.getImage().getWidth() - player.getLayoutX()) * scale);
				view.setLayoutY(((getHeight() / 2) - player.getImage().getHeight() - player.getLayoutY()) * scale);
				for(Bullet pellet : player.getBullets())
				{
					if(pellet.getActive())
					{
						if(!view.getChildren().contains(pellet))
							view.getChildren().add(pellet);
					}
					else if(view.getChildren().contains(pellet))
						view.getChildren().remove((pellet));
				}

				for(AIPlayer bot : bots)
				{
					bot.tick();
					for(Bullet pellet : bot.getBullets())
					{
						if(pellet.getActive())
						{
							if(!view.getChildren().contains(pellet))
								view.getChildren().add(pellet);
						}
						else if(view.getChildren().contains(pellet))
							view.getChildren().remove((pellet));
					}
				}
			}
		}.start();
	}

	public Map getMap()
	{
		return map;
	}
}
