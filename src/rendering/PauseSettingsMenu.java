package rendering;

import enums.MenuEnum;
import gui.GUIManager;
import gui.MenuOption;
import gui.MenuOptionSet;
import gui.UserSettings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

class PauseSettingsMenu extends SubScene
{
    private static Pane view = new Pane();
    boolean opened = false;

    PauseSettingsMenu(GUIManager m)
    {
        super(view, Renderer.view.getWidth(), Renderer.view.getHeight());
        view.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9);");

        GridPane p = new GridPane();

        p.setAlignment(Pos.CENTER);
        p.setHgap(10);
        p.setVgap(10);
        p.setPadding(new Insets(25, 25, 25, 25));
        p.setPrefWidth(Renderer.view.getWidth());
        p.setPrefHeight(Renderer.view.getHeight());


        // Obtain the user's settings
        UserSettings s = GUIManager.getUserSettings();

        // Create the option grid (grid to contain all possible options)
        GridPane optGrid = new GridPane();
        optGrid.setAlignment(Pos.CENTER);
        optGrid.setHgap(10);
        optGrid.setVgap(10);
        optGrid.setPadding(new Insets(25, 25, 25, 25));

        // Create the music label and slider
        Label musicLabel = new Label("Music Volume");

        Slider musicSlider = new Slider();
        musicSlider.setMin(0);
        musicSlider.setMax(100);
        musicSlider.setValue(s.getMusicVolume());
        musicSlider.setShowTickLabels(true);
        musicSlider.setShowTickMarks(true);
        musicSlider.setMajorTickUnit(50);
        musicSlider.setMinorTickCount(5);
        musicSlider.setBlockIncrement(10);
        musicSlider.addEventHandler(InputEvent.ANY, new EventHandler<InputEvent>() {
            @Override
            public void handle(InputEvent event) {
                s.setMusicVolume((int) musicSlider.getValue());
                m.notifySettingsObservers();
            }
        });

        // Create the sound FX label and slider
        Label sfxLabel = new Label("SFX Volume");

        Slider sfxSlider = new Slider();
        sfxSlider.setMin(0);
        sfxSlider.setMax(100);
        sfxSlider.setValue(s.getSfxVolume());
        sfxSlider.setShowTickLabels(true);
        sfxSlider.setShowTickMarks(true);
        sfxSlider.setMajorTickUnit(50);
        sfxSlider.setMinorTickCount(5);
        sfxSlider.setBlockIncrement(10);
        sfxSlider.addEventHandler(InputEvent.ANY, new EventHandler<InputEvent>() {
            @Override
            public void handle(InputEvent event) {
                s.setSfxVolume((int) sfxSlider.getValue());
                m.notifySettingsObservers();
            }
        });

        // Create the shading option label and checkbox
        Label shadingLabel = new Label("Use shading (default on)");

        CheckBox shadingCheckbox = new CheckBox();
        shadingCheckbox.setSelected(s.getShading());
        shadingCheckbox.addEventHandler(InputEvent.ANY, new EventHandler<InputEvent>() {
            @Override
            public void handle(InputEvent event) {
                s.setShading(shadingCheckbox.isSelected());
                m.notifySettingsObservers();
            }
        });

        // Add all of the options to the options grid
        optGrid.add(musicLabel, 0, 0);
        optGrid.add(musicSlider, 1, 0);
        optGrid.add(sfxLabel, 0, 1);
        optGrid.add(sfxSlider, 1, 1);
        optGrid.add(shadingLabel, 0, 2);
        optGrid.add(shadingCheckbox, 1, 2);

        // Create a array of options for the cancel and apply buttons
        MenuOption[] set = {new MenuOption("Back", true, new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                // Transition back to the pause menu
                Renderer.toggleSettingsMenu();
            }
        })};
        // Turn the array into a grid pane
        GridPane buttonGrid = MenuOptionSet.optionSetToGridPane(set);

        // Add the options grid and the button grid to the main grid
        p.add(optGrid, 0, 0);
        p.add(buttonGrid, 0, 1);

        m.addButtonHoverSounds(p);
        view.getStylesheets().add("styles/menu.css");
        view.getChildren().addAll(p);

    }
}
