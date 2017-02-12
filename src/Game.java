import gui.GUIManager;
import javafx.application.Application;
import javafx.stage.Stage;
import rendering.Renderer;

// Ensure res is part of Build Path folder and gson-2.8.0.jar is part of References Libraries before running.

public class Game extends Application
{
	@Override
	public void start(Stage stage) throws Exception
	{
		GUIManager g = new GUIManager();
		stage.setScene(new Renderer("elimination", g.getAudioManager()));
		stage.setTitle("Paintball Pro");
		stage.show();
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
