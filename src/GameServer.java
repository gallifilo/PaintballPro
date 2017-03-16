import gui.GUIManager;
import gui.ServerGUI;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import networking.discoveryNew.DiscoveryServerAnnouncer;
import networking.discovery.IPAddress;
import networking.server.Server;

public class GameServer extends Application {

	ServerGUI gui = new ServerGUI();
	Server server;
	Thread discovery;

	@Override
	public void start(Stage stage) throws Exception {
		Font.loadFont(getClass().getResourceAsStream("styles/fonts/roboto-slab/RobotoSlab-Regular.ttf"), 16);
		stage.getIcons().addAll(new Image("assets/icon_dock.png"), new Image("assets/icon_32.png"), new Image("assets/icon_16.png"));
		stage.setScene(gui);
		stage.setTitle("Paintball Pro Server");
		stage.setOnCloseRequest((event) -> {
			if (server != null)
				server.getExitListener().stopServer();
			if (discovery != null)
				discovery.interrupt();
			System.exit(0);
		});
		stage.show();
		(new Thread(() -> {
			int portNo = 25566;
			DiscoveryServerAnnouncer discovery = new DiscoveryServerAnnouncer(portNo);
			discovery.start();
			server = new Server(portNo, IPAddress.getLAN(), gui, 0);
			//server = new Server(portNo, "10.20.202.182", gui, 0);
			server.start();
			gui.setServer(server, discovery);
		})).start();

	}

	public static void main(String[] args) {
		launch(args);
	}
}
