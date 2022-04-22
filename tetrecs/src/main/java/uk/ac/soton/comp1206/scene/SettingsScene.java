package uk.ac.soton.comp1206.scene;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * SettingScene contains UI components and relevant logic to Settings scene which allows user to change game settings.
 */
public class SettingsScene extends BaseScene {
    public SettingsScene(GameWindow gameWindow) {
        super(gameWindow);
    }

    @Override
    public void initialise() {

    }

    /**
     * Build UI components
     */
    @Override
    public void build() {
        // Set up UI
        var settingPane = new GridPane();
        root.getChildren().add(settingPane);

        var settingText = new Text("Settings");
        settingText.getStyleClass().add("title");
        settingPane.add(settingText, 1,0);

        var settingScroller = new ScrollPane();
        settingPane.getStyleClass().add("");
    }
}
