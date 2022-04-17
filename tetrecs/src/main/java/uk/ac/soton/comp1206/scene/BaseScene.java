package uk.ac.soton.comp1206.scene;

// import org.apache.logging.log4j.LogManager;
// import org.apache.logging.log4j.Logger;

import javafx.scene.Scene;
import javafx.scene.paint.Color;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * A Base Scene used in the game. Handles common functionality between all scenes.
 */
public abstract class BaseScene {

    /**
     * The one and only one GameWindow
     */
    protected final GameWindow gameWindow;

    /**
     * Root pane that contains everything
     */
    protected GamePane root;

    /**
     * Current Scene
     */
    protected Scene scene;

    /**
     * Previous scene
     */
    protected Scene prevScene;
    // private final Logger logger = LogManager.getLogger(BaseScene.class);

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     * @param gameWindow the game window
     */
    public BaseScene(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
    }

    /**
     * Initialise this scene. Called after creation
     */
    public abstract void initialise();

    /**
     * Build the layout of the scene
     */
    public abstract void build();

    /**
     * Create a new JavaFX scene using the root contained within this scene
     * @return JavaFX scene
     */
    public Scene setScene() {
        this.prevScene = gameWindow.getScene();
        Scene newScene = new Scene(root, prevScene.getWidth(), prevScene.getHeight(), Color.BLACK);
        newScene.getStylesheets().add(getClass().getResource("/style/game.css").toExternalForm());
        this.scene = newScene;
        return scene;
    }

    /**
     * Get the JavaFX scene contained inside
     * @return JavaFX scene
     */
    public Scene getScene() {
        return this.scene;
    }

}
