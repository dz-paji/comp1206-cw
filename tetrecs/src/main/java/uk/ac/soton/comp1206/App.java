package uk.ac.soton.comp1206;

import javafx.application.Application;
import javafx.stage.Stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.ac.soton.comp1206.game.Settings;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.Objects;

/**
 * JavaFX Application class
 */
public class App extends Application {

    /**
     * Base resolution width
     */
    private int width = 800;

    /**
     * Base resolution height
     */
    private int height = 600;

    private static App instance;
    private static final Logger logger = LogManager.getLogger(App.class);
    private Stage stage;

    /**
     * Start the game
     * @param args commandline arguments
     */
    public static void main(String[] args) {
        logger.info("Starting client");
        launch();
    }

    /**
     * Called by JavaFX with the primary stage as a parameter. Begins the game by opening the Game Window
     * @param stage the default stage, main window
     */
    @Override
    public void start(Stage stage) {

        var resolution = Settings.checkResolution();
        if (resolution != null) {
            this.height = Integer.parseInt(resolution.split("x")[0]);
            this.width = Integer.parseInt(resolution.split("x")[1]);
        }

        instance = this;
        this.stage = stage;

        //Open game window
        openGame();
    }

    /**
     * Create the GameWindow with the specified width and height
     */
    public void openGame() {
        logger.info("Opening game window");

        //Change the width and height in this class to change the base rendering resolution for all game parts
        var gameWindow = new GameWindow(stage,width,height);

        //Display the GameWindow
        stage.show();
    }

    /**
     * Shutdown the game
     */
    public void shutdown() {
        logger.info("Shutting down");
        System.exit(0);
    }

    /**
     * Get the singleton App instance
     * @return the app
     */
    public static App getInstance() {
        return instance;
    }

}