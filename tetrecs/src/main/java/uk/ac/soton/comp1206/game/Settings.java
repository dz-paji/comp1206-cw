package uk.ac.soton.comp1206.game;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Settings handles saving, loading and applying pre-defined configs to the game.
 * The methods it contains are all static hence no need initialising it.
 */
public class Settings {
    private static final Logger logger = LogManager.getLogger(Settings.class);

    /**
     * Initialise a new object of Settings class. 
     * Which does nothing.
     */
    public Settings() {

    }
    /**
     * Read the pre-saved config file and apply the settings to game.
     * 
     * @param gameWindow the GameWindow this scene will be displayed in.
     */
    public static void applyConfig(GameWindow gameWindow) {
        Properties conf = new Properties();
        try {
            // Read config file
            File confFile = new File("conf.config");

            // break when no config saved
            if (confFile.length() <= 1) {
                return;
            }
            var confFileReader = new FileInputStream(confFile);
            conf.load(confFileReader);
            confFileReader.close();

            // Call appropriate methods
            var resolution = conf.getProperty("resolution");
            var resolutionArray = resolution.split("x");
            gameWindow.updateResolution(Integer.parseInt(resolutionArray[1]), Integer.parseInt(resolutionArray[0]));

            var bgmVol = conf.getProperty("bgmVol");
            Multimedia.setBGMVol(Double.parseDouble(bgmVol));
            var menuVol = conf.getProperty("menuVol");
            Multimedia.setMenuVol(Double.parseDouble(menuVol));
            var fxVol = conf.getProperty("fxVol");
            Multimedia.setFxVol(Double.parseDouble(fxVol));


        } catch (IOException e) {
            logger.error(e.getStackTrace());
        }

    }

    /**
     * Read the config file and get the resolution from it.
     * @return Resolution set in config or null if not present
     */
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
