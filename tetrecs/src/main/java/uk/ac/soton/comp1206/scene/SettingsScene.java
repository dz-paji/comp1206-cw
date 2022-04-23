package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.Settings;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.io.*;
import java.util.Properties;

/**
 * SettingScene contains UI components and relevant logic to Settings scene which allows user to change game settings.
 */
public class SettingsScene extends BaseScene {
    private static final Logger logger = LogManager.getLogger(SettingsScene.class);

    private ComboBox<String> resolutionOption;

    private Slider bgmVolSlider;

    private Slider menuVolSlider;

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
        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        // Set up UI
        var settingPane = new GridPane();
        root.getChildren().add(settingPane);

        var settingText = new Text("Settings");
        settingText.getStyleClass().add("title");
        settingPane.add(settingText, 0, 1);
        settingPane.getStyleClass().add("menu-background");

        // Screen resolution options
        var resolutionLabel = new Label("Screen resolution");
        resolutionLabel.getStyleClass().add("settingLabel");
        resolutionOption = new ComboBox<String>();
        resolutionOption.getItems().addAll("800x600(default)", "960x720", "1024x768", "1280x960", "1400x1050", "1440x1080");
        resolutionOption.getSelectionModel().select(0);

        // volume changer
        var bgmVolLabel = new Label("BGM Volume");
        bgmVolSlider = new Slider();
        bgmVolLabel.getStyleClass().add("settingLabel");

        var menuVolLabel = new Label("Menu BGM Volume");
        menuVolSlider = new Slider();
        menuVolLabel.getStyleClass().add("settingLabel");

        var saveButton = new Button("Save");
        saveButton.getStyleClass().add("menuItem");
        saveButton.setOnAction(this::save);

        // Layout menu
        settingPane.add(resolutionLabel, 1, 2);
        settingPane.add(resolutionOption, 2, 2);
        settingPane.add(bgmVolLabel, 1, 3);
        settingPane.add(bgmVolSlider, 2, 3);
        settingPane.add(menuVolLabel, 1, 4);
        settingPane.add(menuVolSlider, 2, 4);
        settingPane.add(saveButton, 3, 5);

    }

    private void save(ActionEvent actionEvent) {
        Properties conf = new Properties();
        try {
            // Read config file, create if it doesn't exist.
            File confFile = new File("conf.config");
            confFile.createNewFile();
            var confFileReader = new FileInputStream(confFile);
            confFileReader.close();
            conf.load(confFileReader);
            confFileReader.close();

            var resolution = this.resolutionOption.getValue();
            var bgmVol = this.bgmVolSlider.getValue();
            var menuVol = this.menuVolSlider.getValue();
            conf.put("menuVol", String.valueOf(menuVol));
            conf.put("resolution", resolution);
            conf.put("bgmVol", String.valueOf(bgmVol));


            // Store configs
            var confFileWriter = new FileOutputStream(confFile);
            conf.store(confFileWriter, "save config");
            confFileWriter.close();

            Platform.runLater(() -> Settings.applyConfig(gameWindow));

        } catch (IOException e) {
            logger.error(e.getStackTrace());
            logger.error(e.getMessage());
        }
    }


}
