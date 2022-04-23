package uk.ac.soton.comp1206.game;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.scene.MenuScene;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Settings handles saving, loading and applying pre-defined configs to the game.
 */
public class Settings {
    private static final Logger logger = LogManager.getLogger(Settings.class);


    public static void applyConfig(GameWindow gameWindow) {
        Properties conf = new Properties();
        try {
            // Read config file
            File confFile = new File("conf.config");
            var confFileReader = new FileInputStream(confFile);
            conf.load(confFileReader);
            confFileReader.close();

            // Call appropriate methods
            var resolution = conf.getProperty("resolution");
            var resolutionArray = resolution.split("x");
            gameWindow.updateResolution(Integer.parseInt(resolutionArray[0]), Integer.parseInt(resolutionArray[1]));

            var bgmVol = conf.getProperty("bgmVol");
            Multimedia.setBGMVol(Double.parseDouble(bgmVol));
            var menuVol = conf.getProperty("menuVol");
            Multimedia.setMenuVol(Double.parseDouble(menuVol));


        } catch (IOException e) {
            logger.error(e.getStackTrace());
        }

    }

    public static String checkResolution() {
        Properties conf = new Properties();
        try {
            // Read config file
            File confFile = new File("conf.config");
            var confFileReader = new FileInputStream(confFile);
            conf.load(confFileReader);
            confFileReader.close();

            // Return result
            return conf.getProperty("resolution");
        } catch (IOException e) {
            logger.error(e.getStackTrace());
        }

        return "null";

    }

}
