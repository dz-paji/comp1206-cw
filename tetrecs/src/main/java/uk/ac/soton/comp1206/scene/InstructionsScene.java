package uk.ac.soton.comp1206.scene;

import org.apache.logging.log4j.Logger;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import org.apache.logging.log4j.LogManager;

public class InstructionsScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * Create a new Instruction scene
     * 
     * @param gameWindow the game windows this will be displayed in
     */
    public InstructionsScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating instruction scene");
    }

    /**
     * Initialise this scene
     */
    @Override
    public void initialise() {
    }

    /**
     * Construct the layout of the scene
     */
    public void build() {
        logger.info("Building " + this.getClass().getName());
        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());
        var instructPane = new StackPane();
        instructPane.setMaxHeight(gameWindow.getHeight());
        instructPane.setMaxWidth(gameWindow.getWidth());
        instructPane.getStyleClass().add("menu-background");
        root.getChildren().add(instructPane);

        Image instructImage = new Image(
                InstructionsScene.class.getResource("/images/instructions.png").toExternalForm(), gameWindow.getWidth(),
                gameWindow.getHeight(), false, false);
        ImageView instructView = new ImageView(instructImage);
        instructView.fitHeightProperty().bind(gameWindow.getHeIntegerProperty());
        instructView.fitWidthProperty().bind(gameWindow.getWiIntegerProperty());
        instructView.autosize();

        instructPane.getChildren().add(instructView);

    }
}
