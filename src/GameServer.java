import gui.GUIManager;
import gui.ServerGUI;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import networking.discovery.DiscoveryServerAnnouncer;
import networking.server.Server;

public class GameServer extends Application {

	ServerGUI gui = new ServerGUI();

	@Override
	public void start(Stage stage) throws Exception {
		Font.loadFont(getClass().getResourceAsStream("styles/fonts/roboto-slab/RobotoSlab-Regular.ttf"), 16);
		stage.getIcons().addAll(new Image("assets/icon_dock.png"), new Image("assets/icon_32.png"), new Image("assets/icon_16.png"));
		stage.setScene(gui);
		stage.setTitle("Paintball Pro Server");
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				System.exit(0);
			}
		});
		stage.show();
		int portNo = 25566;
		String[] serverArgs = {portNo + "", "127.0.0.1"};
		Thread discovery = new Thread(new DiscoveryServerAnnouncer(portNo));
		discovery.start();
		(new Thread(new Runnable() {
			@Override
			public void run() {
				Server.main(serverArgs, gui);
			}
		})).start();

	}

	public static void main(String[] args) {
		launch(args);
	}
}
