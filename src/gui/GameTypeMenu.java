package gui;

import enums.GameLocation;
import enums.Menu;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

/**
 * Class for the game type picker screen
 */
public class GameTypeMenu {
	
	/**
	 * Return a game type menu scene for a given GUI manager
	 * @param guiManager GUI manager to use
	 * @return game type menu scene
	 */
	public static Scene getScene(GUIManager guiManager, GameLocation loc) {

		MenuOption[] empty = {};
		GridPane mainGrid = MenuOptionSet.optionSetToGridPane(empty);

		LoadingPane loadingPane = new LoadingPane(mainGrid);

		Label titleLabel = new Label("Choose a Game Type");
		titleLabel.setStyle("-fx-font-size: 26px;");

		// Create a set of button options, with each button's title and event handler
		MenuOption[] set = {new MenuOption("Team Match", true, event -> {
			if (loc == GameLocation.MultiplayerServer) {
				guiManager.transitionTo(Menu.Lobby, "Elimination");
				} else {
				loadingPane.startLoading();
				new Thread(() -> guiManager.transitionTo(Menu.EliminationSingle)).start();
				}
			}), new MenuOption("Capture The Flag", true, event -> {
			if (loc == GameLocation.MultiplayerServer) {
				guiManager.transitionTo(Menu.Lobby, "CTF");
			} else {
				loadingPane.startLoading();
				new Thread(() -> guiManager.transitionTo(Menu.CTFSingle)).start();
			}
		}), new MenuOption("Back", false, event -> {
			if (loc == GameLocation.MultiplayerServer)
				guiManager.exitClient();
			guiManager.transitionTo(Menu.MainMenu);
		})};
		
		// Turn the collection of button options into a GridPane to be displayed
		GridPane grid = MenuOptionSet.optionSetToGridPane(set);
		mainGrid.add(MenuControls.centreInPane(titleLabel), 0, 0);
		mainGrid.add(grid, 0, 1);

		// Create the scene and return it
		return guiManager.createScene(loadingPane);
	}
}
