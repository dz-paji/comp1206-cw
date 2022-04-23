package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.Multimedia;
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

    private Slider fxVolSlider;

    public SettingsScene(GameWindow gameWindow) {
        super(gameWindow);
    }

    @Override
    public void initialise() {

        // Register exit key bind
        gameWindow.getScene().setOnKeyPressed((e) -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                gameWindow.startMenu();
            }
        });

    }

    /**
     * Build UI components
     */
    @Override
    public void build() {
        logger.info("Building settings scene");

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        // Set up UI
        var settingPane = new GridPane();
        root.getChildren().add(settingPane);

        var settingText = new Text("Settings");
        settingText.getStyleClass().add("title");
        settingPane.add(settingText, 0, 1);
        settingPane.getStyleClass().add("setting");

        // Screen resolution options
        var resolutionLabel = new Label("Screen resolution");
        resolutionLabel.getStyleClass().add("settingLabel");
        resolutionOption = new ComboBox<String>();
        resolutionOption.getItems().addAll("800x600", "960x720", "1024x768", "1280x960", "1400x1050", "1440x1080");
        resolutionOption.getSelectionModel().select(0);

        // volume changer
        var bgmVolLabel = new Label("BGM Volume");
        bgmVolSlider = new Slider();
        bgmVolSlider.setValue(Multimedia.getBGMVol());
        bgmVolSlider.setMax(1);
        bgmVolLabel.getStyleClass().add("settingLabel");

        var menuVolLabel = new Label("Menu BGM Volume");
        menuVolSlider = new Slider();
        menuVolSlider.setValue(Multimedia.getMenuVol());
        menuVolSlider.setMax(1);
        menuVolLabel.getStyleClass().add("settingLabel");

        var fxVolLabel = new Label("Effect Volume");
        fxVolSlider = new Slider();
        fxVolSlider.setValue(Multimedia.getFxVol());
        fxVolSlider.setMax(1);
        fxVolLabel.getStyleClass().add("settingLabel");

        // save and exit
        var saveButton = new Button("Save");
        //saveButton.getStyleClass().add("settingButton");
        saveButton.setOnAction(this::save);
        var backButton = new Button("Back");
        backButton.setOnAction((e) -> gameWindow.startMenu());

        // Layout menu
        settingPane.add(resolutionLabel, 1, 2);
        settingPane.add(resolutionOption, 2, 2);
        settingPane.add(bgmVolLabel, 1, 3);
        settingPane.add(bgmVolSlider, 2, 3);
        settingPane.add(menuVolLabel, 1, 4);
        settingPane.add(menuVolSlider, 2, 4);
        settingPane.add(fxVolLabel, 1, 5);
        settingPane.add(fxVolSlider, 2, 5);
        settingPane.add(saveButton, 3, 6);
        settingPane.add(backButton,0,6);

        settingPane.setAlignment(Pos.CENTER);
    }

    private void save(ActionEvent actionEvent) {
        logger.info("Save button fired");
        Properties conf = new Properties();
        try {
            // Read config file, create if it doesn't exist.
            File confFile = new File("conf.config");
            confFile.createNewFile();
            var confFileReader = new FileInputStream(confFile);
            conf.load(confFileReader);
            confFileReader.close();

            conf.put("menuVol", String.valueOf(menuVolSlider.getValue()));
            conf.put("resolution", resolutionOption.getValue());
            conf.put("bgmVol", String.valueOf(bgmVolSlider.getValue()));
            conf.put("fxVol", String.valueOf(fxVolSlider.getValue()));


            // Store configs
            var confFileWriter = new FileOutputStream(confFile);
            conf.store(confFileWriter, "save config");
            confFileWriter.close();

            Platform.runLater(() -> {
                Settings.applyConfig(gameWindow);

                // Reload this scene to refresh ui component
                gameWindow.startSettings();
            });



        } catch (IOException e) {
            logger.error(e);
        }
    }


}
