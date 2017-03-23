package physics;

import integrationServer.CollisionsHandlerListener;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import rendering.GameObject;
import rendering.ImageFactory;

import java.util.ArrayList;
import java.util.Random;

public class Powerup extends ImageView
{
	private static final int duration = 15000; //Respawn 15 seconds after being taken
	private PowerupType type;
	private ArrayList<Powerup> alternatePowerups = new ArrayList<>();
	private GameObject[] locations;
	private CollisionsHandlerListener listener;

	public Powerup(PowerupType type, GameObject[] locations)
	{
		super(ImageFactory.getPowerupImage(type));
		setEffect(new DropShadow(8, type == PowerupType.SHIELD ? Color.GREEN : Color.YELLOW));
		this.type = type;
		this.locations = locations;
	}

	public PowerupType getType()
	{
		return type;
	}

	public void setListener(CollisionsHandlerListener listener)
	{
		this.listener = listener;
	}

	private void resetPosition()
	{
		int indexLocation = (new Random()).nextInt(locations.length);
		double x = locations[indexLocation].getX() * 64 + 16, y =  locations[indexLocation].getY() * 64 + 16;

		for(Powerup alternatePowerup : alternatePowerups)
			if(x == alternatePowerup.getLayoutX() && y == alternatePowerup.getLayoutY())
			{
				resetPosition();
				return;
			}

		relocate(x, y);
		if(listener != null)
			listener.onPowerupRespawn(type, indexLocation);
	}

	public void resetPosition(int index)
	{
		relocate(locations[index].getX() * 64 + 16, locations[index].getY() * 64 + 16);
	}

	public void addAlternatePowerup(Powerup alternatePowerup)
	{
		this.alternatePowerups.add(alternatePowerup);
		resetPosition();
	}

	void take() {
		setVisible(false);
		resetPosition();
		new java.util.Timer().schedule(
		        new java.util.TimerTask() {
		            @Override
		            public void run() {
		            	setVisible(true);
		            }
		        },
		        duration
		);

	}
}
