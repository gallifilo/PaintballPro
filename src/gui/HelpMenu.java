package gui;

import enums.Menu;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebView;

/**
 * Class to create a scene for the help screen
 *
 * @author Jack Hughes
 */
public class HelpMenu {

	/**
	 * Create and return a help menu scene for a given GUI manager
	 *
	 * @param guiManager GUI manager to use
	 * @return scene for the help menu
	 */
	public static Scene getScene(GUIManager guiManager) {

		// Create the main grid (to contain the options grid, and the apply/cancel buttons)
		GridPane mainGrid = new GridPane();
		mainGrid.setAlignment(Pos.CENTER);
		mainGrid.setHgap(10);
		mainGrid.setVgap(10);
		mainGrid.setPadding(MenuControls.scaleByResolution(25));

		Platform.runLater(() -> {
			WebView webView = new WebView();
			webView.getEngine().load(HelpMenu.class.getResource("/help.html").toExternalForm());
			mainGrid.add(webView, 0, 0);
		});


		// Create a array of options for the cancel and apply buttons
		MenuOption[] set = {new MenuOption("Back", true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// Transition back to the main menu
				guiManager.transitionTo(Menu.MAIN_MENU);
			}
		})};
		// Turn the array into a grid pane
		GridPane buttonGrid = MenuOptionSet.optionSetToGridPane(set);

		// Add the options grid and the button grid to the main grid
		mainGrid.add(buttonGrid, 0, 1);

		// Create a new scene using the main grid
		return guiManager.createScene(mainGrid);
	}
}
