package uk.ac.soton.comp1206.scene;

import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The Single Player challenge scene. Holds the UI for the single player
 * challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    protected Game game;

    /**
     * Create a new Single Player challenge scene
     * 
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Challenge Scene");
    }

    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        setupGame();

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        var mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);

        var board = new GameBoard(game.getGrid(), gameWindow.getWidth() / 2, gameWindow.getWidth() / 2);
        mainPane.setCenter(board);

        // Handle block on gameboard grid being clicked
        board.setOnBlockClick(this::blockClicked);

        // Show stats
        var level = new Text();
        var level_text = new Text("Current Level:");
        level.textProperty().bind(game.getLevel().asString());
        level.getStyleClass().add("level");
        level_text.getStyleClass().add("heading");

        var lives = new Text();
        var lives_text = new Text("Lives remain:");
        lives.textProperty().bind(game.getLives().asString());
        lives.getStyleClass().add("lives");
        lives_text.getStyleClass().add("heading");

        var multiplier = new Text();
        var multiplier_text = new Text("Current multiplier:");
        multiplier.textProperty().bind(game.getMultiplier().asString());
        multiplier.getStyleClass().add("multiplier");
        multiplier_text.getStyleClass().add("heading");

        var score = new Text();
        var score_text = new Text("Current score:");
        score.textProperty().bind(game.getLevel().asString());
        score.getStyleClass().add("score");
        score_text.getStyleClass().add("heading");

        var statsBox = new VBox();
        statsBox.getChildren().addAll(level_text, level, lives_text, lives, multiplier_text, multiplier, score_text,
                score);

        // Show current Piece
        PieceBoard pieceBoard = new PieceBoard(gameWindow.getWidth() / 5, gameWindow.getHeight() / 4);
        pieceBoard.setPiece(game.getPiece());
        var piecePane = new BorderPane();
        piecePane.getChildren().add(pieceBoard);

        statsBox.getChildren().add(piecePane);
        statsBox.setAlignment(Pos.CENTER);
        mainPane.setRight(statsBox);

    }

    /**
     * Handle when a block is clicked
     * 
     * @param gameBlock the Game Block that was clocked
     */
    private void blockClicked(GameBlock gameBlock) {
        game.blockClicked(gameBlock);
    }

    /**
     * Setup the game object and model
     */
    public void setupGame() {
        logger.info("Starting a new challenge");

        // Start new game
        game = new Game(5, 5);
    }

    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising Challenge");
        game.start();
        Multimedia.playBGM();
    }

    public void endGame() {
        logger.info("Cleanning up the game...");
        
    }

}
