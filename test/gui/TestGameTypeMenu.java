package gui;

import enums.GameLocation;
import enums.Menu;
import helpers.GUIManagerTestHelper;
import helpers.JavaFXTestHelper;
import javafx.scene.Scene;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Tests for the Game Type menu
 *
 * @author Jack Hughes
 */
public class TestGameTypeMenu {

    /**
     * Setup the JavaFX thread
     * @throws Exception test failed
     */
    @Before
    public void setUp() throws Exception {
        JavaFXTestHelper.setupApplication();
        JavaFXTestHelper.waitForPlatform();
    }

    /**
     * Test for selecting Team Match in singleplayer mode
     * @throws Exception test failed
     */
    @Test
    public void selectEliminationSingle() throws Exception {
        GUIManagerTestHelper m = new GUIManagerTestHelper();
        Scene s = GameTypeMenu.getScene(m, GameLocation.SINGLEPLAYER);
        assertTrue(m.currentMenu == Menu.MAIN_MENU);
        GUIManagerTestHelper.findButtonByTextInParent("Team Match", s.getRoot()).fire();
        Thread.sleep(1000);
        assertTrue(m.currentMenu == Menu.TEAM_MATCH_SINGLEPLAYER);
    }

    /**
     * Test for selecting Team Match in multiplayer mode
     * @throws Exception test failed
     */
    @Test
    public void selectEliminationMulti() throws Exception {
        GUIManagerTestHelper m = new GUIManagerTestHelper();
        Scene s = GameTypeMenu.getScene(m, GameLocation.MULTIPLAYER);
        assertTrue(m.currentMenu == Menu.MAIN_MENU);
        GUIManagerTestHelper.findButtonByTextInParent("Team Match", s.getRoot()).fire();
        assertTrue(m.currentMenu == Menu.LOBBY);
    }

    /**
     * Test for selecting Capture the Flag in singleplayer mode
     * @throws Exception test failed
     */
    @Test
    public void selectCTFSingle() throws Exception {
        GUIManagerTestHelper m = new GUIManagerTestHelper();
        Scene s = GameTypeMenu.getScene(m, GameLocation.SINGLEPLAYER);
        assertTrue(m.currentMenu == Menu.MAIN_MENU);
        GUIManagerTestHelper.findButtonByTextInParent("Capture The Flag", s.getRoot()).fire();
        Thread.sleep(4000);
        assertTrue(m.currentMenu == Menu.CAPTURE_THE_FLAG_SINGLEPLAYER);
    }

    /**
     * Test for selecting Capture the Flag in multiplayer mode
     * @throws Exception test failed
     */
    @Test
    public void selectCTFMulti() throws Exception {
        GUIManagerTestHelper m = new GUIManagerTestHelper();
        Scene s = GameTypeMenu.getScene(m, GameLocation.MULTIPLAYER);
        assertTrue(m.currentMenu == Menu.MAIN_MENU);
        GUIManagerTestHelper.findButtonByTextInParent("Capture The Flag", s.getRoot()).fire();
        assertTrue(m.currentMenu == Menu.LOBBY);
    }

    /**
     * Test for selecting the back button
     * @throws Exception test failed
     */
    @Test
    public void selectBack() throws Exception {
        GUIManagerTestHelper m = new GUIManagerTestHelper();
        Scene s = GameTypeMenu.getScene(m, GameLocation.MULTIPLAYER);
        m.currentMenu = Menu.SINGLEPLAYER_GAME_TYPE;
        GUIManagerTestHelper.findButtonByTextInParent("Back", s.getRoot()).fire();
        assertTrue(m.currentMenu == Menu.MAIN_MENU);
    }

}