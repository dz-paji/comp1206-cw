package uk.ac.soton.comp1206.ui;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.ac.soton.comp1206.App;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.MultiplayerGame;
import uk.ac.soton.comp1206.game.Settings;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.scene.*;

/**
 * The GameWindow is the single window for the game where everything takes
 * place. To move between screens in the game,
 * we simply change the scene.
 * <p>
 * The GameWindow has methods to launch each of the different parts of the game
 * by switching scenes. You can add more
 * methods here to add more screens to the game.
 */
public class GameWindow {

    private static final Logger logger = LogManager.getLogger(GameWindow.class);

    private int width;
    private int height;

    private final Stage stage;

    private BaseScene currentScene;
    private BaseScene prevScene;
    private Scene scene;

    final Communicator communicator;

    /**
     * Create a new GameWindow attached to the given stage with the specified width
     * and height
     *
     * @param stage  stage
     * @param width  width
     * @param height height
     */
    public GameWindow(Stage stage, int width, int height) {
        this.width = width;
        this.height = height;

        this.stage = stage;

        // Setup window
        setupStage();

        // Setup resources
        setupResources();

        Settings.applyConfig(this);

        // Setup default scene
        setupDefaultScene();

        // Setup communicator
        communicator = new Communicator("ws://ofb-labs.soton.ac.uk:9700");

        // Go to menu
        startMenu();
    }

    /**
     * Setup the font and any other resources we need
     */
    private void setupResources() {
        logger.info("Loading resources");

        // We need to load fonts here due to the Font loader bug with spaces in URLs in
        // the CSS files
        Font.loadFont(getClass().getResourceAsStream("/style/Orbitron-Regular.ttf"), 32);
        Font.loadFont(getClass().getResourceAsStream("/style/Orbitron-Bold.ttf"), 32);
        Font.loadFont(getClass().getResourceAsStream("/style/Orbitron-ExtraBold.ttf"), 32);
    }

    /**
     * Display the main menu
     */
    public void startMenu() {
        loadScene(new MenuScene(this));
    }

    /**
     * Display the single player challenge
     */
    public void startChallenge() {
        loadScene(new ChallengeScene(this));
    }

    /**
     * Display the instruction scene
     */
    public void startInstruction() {
        loadScene(new InstructionsScene(this));
    }

    /**
     * Start a multiplayer game
     */
    public void startMultiplayerGame() {
        loadScene(new MultiplayerGameScene(this));
    }

    /**
     * Display the multiplayer scene
     */
    public void startMultiplayer() {
        loadScene(new MultiplayerScene(this));
    }

    /**
     * Display settings scene
     */
    public void startSettings() {
        loadScene(new SettingsScene(this));
    }

    /**
     * Display the scores.
     *
     * @param game Game object
     */
    public void startScore(Game game) {
        logger.info("Loading score for local game");

        loadScene(new ScoreScene(this, game));
    }

    /**
     * Display the score for multiplayer object
     *
     * @param game Multiplayer game object
     */
    public void startScore(MultiplayerGame game) {
        logger.info("Loading score for multiplayer game");
        loadScene(new ScoreScene(this, game));
    }

    /**
     * Setup the default settings for the stage itself (the window), such as the
     * title and minimum width and height.
     */
    public void setupStage() {
        stage.setTitle("TetrECS");
        stage.setMinWidth(width);
        stage.setMinHeight(height + 20);
        stage.setOnCloseRequest(ev -> {
            this.communicator.send("QUIT");
            App.getInstance().shutdown();
        });
    }

    /**
     * Load a given scene which extends BaseScene and switch over.
     *
     * @param newScene new scene to load
     */
    public void loadScene(BaseScene newScene) {
        // Cleanup remains of the previous scene
        cleanup();

        // Create the new scene and set it up
        newScene.build();
        prevScene = currentScene;
        currentScene = newScene;
        scene = newScene.setScene();
        stage.setScene(scene);

        // Initialise the scene when ready
        Platform.runLater(() -> currentScene.initialise());
    }

    /**
     * Setup the default scene (an empty black scene) when no scene is loaded
     */
    public void setupDefaultScene() {
        this.scene = new Scene(new Pane(), width, height, Color.BLACK);
        stage.setScene(this.scene);
    }

    /**
     * When switching scenes, perform any cleanup needed, such as removing previous
     * listeners
     */
    public void cleanup() {
        logger.info("Clearing up previous scene");
        communicator.clearListeners();
    }

    /**
     * Get the current scene being displayed
     *
     * @return scene
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Get the width of the Game Window
     *
     * @return width
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Get the height of the Game Window
     *
     * @return height
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Get the height property of the Game Window
     *
     * @return height property.
     */
    public IntegerProperty getHalfHeIntegerProperty() {
        return new SimpleIntegerProperty(height / 2);
    }

    /**
     * Get the width property of the Game Window
     *
     * @return width property.
     */
    public IntegerProperty getWiIntegerProperty() {
        return new SimpleIntegerProperty(width);
    }

    /**
     * Get the communicator
     *
     * @return communicator
     */
    public Communicator getCommunicator() {
        return communicator;
    }

    /**
     * Depending on whether previous scene exists, exit the game or load the
     * previous scene with clean up.
     */
    public void endGame() {
        cleanup();
        this.communicator.disconnect();
        stage.close();

    }

    /**
     * Update the width and height of GameWindow
     *
     * @param height new height
     * @param width  new width
     */
    public void updateResolution(int height, int width) {
        this.height = height;
        this.width = width;

        // Resize the window
        this.stage.setMinWidth(width);
        this.stage.setMinHeight(height * 1.034);
        this.stage.setMaxWidth(width);
        this.stage.setMaxHeight(height * 1.034);

    }
}
