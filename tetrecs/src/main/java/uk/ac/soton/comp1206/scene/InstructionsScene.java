package uk.ac.soton.comp1206.scene;

import org.apache.logging.log4j.Logger;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.ArrayList;

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

        // Getting new GamePane ready
        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());
        var mainPane = new StackPane();
        mainPane.setMaxHeight(gameWindow.getHeight());
        mainPane.setMaxWidth(gameWindow.getWidth());
        mainPane.getStyleClass().add("menu-background");
        root.getChildren().add(mainPane);

        var instructionPane = new BorderPane();

        // Getting instruction components ready
        Image instructImage = new Image(
                InstructionsScene.class.getResource("/images/instructions.png").toExternalForm(), gameWindow.getWidth(),
                gameWindow.getHeight() / 2, false, false);
        ImageView instructView = new ImageView(instructImage);
        var instruction = new Text("Instruction");
        instruction.getStyleClass().add("heading");

        var insctBox = new VBox();
        insctBox.getChildren().add(instruction);
        insctBox.getChildren().add(instructView);
        insctBox.setAlignment(Pos.CENTER);

        // Getting GamePiece ready
        var piecePane = new GridPane();
        var pieceText = new Text("Game Pieces");
        pieceText.getStyleClass().add("heading");
        ArrayList<GamePiece> pieceArray = new ArrayList<GamePiece>();
        ArrayList<PieceBoard> boardArray = new ArrayList<PieceBoard>();
        for (int i = 0; i < 15; i++) {
            pieceArray.add(GamePiece.createPiece(i));
            boardArray.add(new PieceBoard(50, 50));
            boardArray.get(i).setPiece(pieceArray.get(i));
            piecePane.add(boardArray.get(i), i % 8, i / 8);
        }

        logger.info(piecePane.getChildren().size());
        var gamePieceBox = new VBox();
        gamePieceBox.getChildren().addAll(pieceText, piecePane);
        gamePieceBox.setAlignment(Pos.CENTER);
        piecePane.setAlignment(Pos.CENTER);

        // Adding components to the Pane.
        mainPane.getChildren().add(instructionPane);
        instructionPane.setTop(insctBox);
        instructionPane.setCenter(gamePieceBox);
    }
}
