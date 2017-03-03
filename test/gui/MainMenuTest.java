package gui;

import enums.MenuEnum;
import helpers.GUIManagerTestHelper;
import helpers.JavaFXTestHelper;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by jack on 01/03/2017.
 */
public class MainMenuTest {

    @Before
    public void setUp() throws Exception {
        // Setup JavaFX
        JavaFXTestHelper.setupApplication();
    }

    @Test
    public void singleButton() throws Exception {
        GUIManagerTestHelper m = new GUIManagerTestHelper();
        Scene mainMenu = MainMenu.getScene(m);

        (GUIManagerTestHelper.findButtonByTextInParent("Single player", mainMenu.getRoot())).fire();

        assertTrue(m.currentMenu == MenuEnum.SingleplayerGameType);
    }

    @Test
    public void multiButton() throws Exception {
        GUIManagerTestHelper m = new GUIManagerTestHelper();
        Scene mainMenu = MainMenu.getScene(m);

        (GUIManagerTestHelper.findButtonByTextInParent("Multiplayer", mainMenu.getRoot())).fire();

        assertTrue(m.currentMenu == MenuEnum.NicknameServerConnection);
    }

    @Test
    public void settingsButton() throws Exception {
        GUIManagerTestHelper m = new GUIManagerTestHelper();
        Scene mainMenu = MainMenu.getScene(m);

        (GUIManagerTestHelper.findButtonByTextInParent("Settings", mainMenu.getRoot())).fire();

        assertTrue(m.currentMenu == MenuEnum.Settings);
    }

}