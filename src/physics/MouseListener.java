package physics;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import rendering.Renderer;

public class MouseListener implements EventHandler<MouseEvent>{

	private InputHandler inputHandler;

	public MouseListener(InputHandler inputHandler){
		this.inputHandler = inputHandler;
	}

	@Override
    public void handle(MouseEvent event) {

		if(!Renderer.getPauseMenuState()&& !Renderer.getSettingsMenuState())
		{
				int newX = (int) event.getX();
				int newY = (int) event.getY();
				inputHandler.setMouseX(newX);
				inputHandler.setMouseY(newY);
				inputHandler.setShoot(event.isPrimaryButtonDown());
		}
    }
}
