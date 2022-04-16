package uk.ac.soton.comp1206.scene;

import javafx.animation.FillTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
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

    /**
     * The game instance.
     */
    protected Game game;

    /**
     * The PieceBoard displays the current piece.
     */
    private final PieceBoard pieceBoard;

    /**
     * The PieceBoard displays the following piece.
     */
    private final PieceBoard followingPieceBoard;

    private IntegerProperty[] aimWare = { new SimpleIntegerProperty(0), new SimpleIntegerProperty(0) };

    private GameBoard board;

    private final Timeline timerLine = new Timeline();

    private Rectangle timerBar;

    /**
     * Create a new Single Player challenge scene
     * 
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Challenge Scene");
        pieceBoard = new PieceBoard(gameWindow.getWidth() / 5, gameWindow.getWidth() / 5);
        followingPieceBoard = new PieceBoard(gameWindow.getWidth() / 8, gameWindow.getWidth() / 8);
    }

    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        logger.info("Building {}", this.getClass().getName());

        setupGame();

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        var mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);

        board = new GameBoard(game.getGrid(), gameWindow.getWidth() / 2, gameWindow.getWidth() / 2);
        mainPane.setCenter(board);

        // Handle block on gameboard grid being clicked
        board.setOnBlockClick(this::blockClicked);
        board.setOnRightClick((e) -> {
            game.rotateCurrentPiece();
        });

        // Show stats
        var level = new Text();
        var level_text = new Text(
                "Current Level:");
        level.textProperty().bind(game.getLevel().asString());
        level.getStyleClass().add("level");
        level_text.getStyleClass().add("heading");

        var lives = new Text();
        var lives_text = new Text(
                "Lives remain:");
        lives.textProperty().bind(game.getLives().asString());
        lives.getStyleClass().add("lives");
        lives_text.getStyleClass().add("heading");

        var multiplier = new Text();
        var multiplier_text = new Text(
                "Current multiplier:");
        multiplier.textProperty().bind(game.getMultiplier().asString());
        multiplier.getStyleClass().add("multiplier");
        multiplier_text.getStyleClass().add("heading");

        var score = new Text();
        var score_text = new Text(
                "Current score:");
        score.textProperty().bind(game.getScore().asString());
        score.getStyleClass().add("score");
        score_text.getStyleClass().add("heading");

        var statsBox = new VBox();
        statsBox.getChildren().addAll(level_text, level, lives_text, lives, multiplier_text, multiplier, score_text,
                score);

        // Show current Piece
        pieceBoard.setPiece(game.getPiece());
        pieceBoard.toggleIndicator();
        followingPieceBoard.setPiece(game.getFollowingPiece());

        // Rotate currentPiece when clicking PieceBoard
        pieceBoard.setOnBlockClick((e) -> {
            game.rotateCurrentPiece();
        });

        // Swap GamePieces when clicking followingPieceBoard
        followingPieceBoard.setOnBlockClick(this::pieceBoardClicked);

        // Implementing Timer Bar
        timerBar = new Rectangle(gameWindow.getWidth(), 5, Color.GREEN);

        statsBox.getChildren().addAll(pieceBoard, followingPieceBoard);
        statsBox.setAlignment(Pos.CENTER);
        mainPane.setRight(statsBox);
        mainPane.setTop(timerBar);
        BorderPane.setAlignment(timerBar, Pos.TOP_CENTER);
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
     * Handle when a PieceBoard block is clicked.
     */
    private void pieceBoardClicked(GameBlock gameBlock) {
        game.swapPiece();
    }

    /**
     * Setup the game object and model
     */
    public void setupGame() {
        logger.info("Starting a new challenge");

        // Start new game
        game = new Game(5, 5);

        // Bind NextPieceListener.
        game.setNextPieceListener((message, e) -> {
            pieceBoard.setPiece(game.getPiece());
            pieceBoard.toggleIndicator();
            followingPieceBoard.setPiece(game.getFollowingPiece());
        });

        // Handle fadeOut animation
        game.setOnLineCleared(this::lineCleared);

        // Register timer
        game.setOnGameLoop(this::animateTimerBar);
    }

    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising Challenge");
        game.start();
        Multimedia.playBGM();

        // Key board support
        gameWindow.getScene().setOnKeyPressed((e) -> {
            logger.info("Key {} pressed",e.getCharacter());
            switch (e.getCode()) {
                case UP:
                    aimUp();
                    break;

                case DOWN:
                    aimDown();
                    break;

                case LEFT:
                    aimLeft();
                    break;

                case RIGHT:
                    aimRight();
                    break;

                case W:
                    aimUp();
                    break;

                case A:
                    aimLeft();
                    break;

                case S:
                    aimDown();
                    break;

                case D:
                    aimRight();
                    break;

                case ENTER:
                    blockClicked(board.getBlock(aimWare[0].get(), aimWare[1].get()));
                    break;

                case X:
                    blockClicked(board.getBlock(aimWare[0].get(), aimWare[1].get()));
                    break;

                case OPEN_BRACKET:
                    game.rotateCurrentPiece();
                    break;

                case Q:
                    game.rotateCurrentPiece();
                    break;

                case Z:
                    game.rotateCurrentPiece();
                    break;

                case E:
                    game.rotateCurrentPiece();
                    break;

                case C:
                    game.rotateCurrentPiece();
                    break;

                case CLOSE_BRACKET:
                    game.rotateCurrentPiece();
                    break;

                case SPACE:
                    game.swapPiece();
                    break;

                case R:
                    game.swapPiece();
                    break;

                case ESCAPE:
                    endGame();
                    gameWindow.startMenu();
                    break;

                default:
                    break;
            }
        });

        aimWare[0].addListener(this::aimXUpdate);
        aimWare[1].addListener(this::aimYUpdate);

    }

    /**
     * End the challenge and display scores.
     */
    public void endGame() {
        logger.info("Cleanning up the game...");
        Multimedia.stopBGM();
        game.endGame();
        Platform.runLater(() -> gameWindow.startScore(game));
    }

    /**
     * Handles when key is pressed.
     * 
     * @param e The key pressed event
     */
    public void keyHandler(KeyEvent e) {
        logger.info("Key being pressed is: {}", e.getCharacter());
    }

    private void aimUp() {
        if (aimWare[1].get() > 0) {
            aimWare[1].set(aimWare[1].get() - 1);
        }
    }

    private void aimDown() {
        if (aimWare[1].get() < game.getRows() - 1) {
            aimWare[1].set(aimWare[1].get() + 1);
        }
    }

    private void aimLeft() {
        if (aimWare[0].get() > 0) {
            aimWare[0].set(aimWare[0].get() - 1);
        }
    }

    private void aimRight() {
        if (aimWare[0].get() < game.getCols() - 1) {
            aimWare[0].set(aimWare[0].get() + 1);
        }
    }

    private void aimXUpdate(ObservableValue<? extends Number> observable, Number oldNumber, Number newNumber) {
        board.rePaintAll();
        board.getBlock(aimWare[0].get(), aimWare[1].get()).highlight();
        board.getBlock(oldNumber.intValue(), aimWare[1].get()).paint();
    }

    private void aimYUpdate(ObservableValue<? extends Number> observable, Number oldNumber, Number newNumber) {
        board.rePaintAll();
        board.getBlock(aimWare[0].get(), aimWare[1].get()).highlight();
        board.getBlock(aimWare[0].get(), oldNumber.intValue()).paint();
    }

    private void lineCleared(Set<GameBlockCoordinate> coordinates) {
        board.fadeOut(coordinates);
    }

    private void animateTimerBar(int delay) {
        if (delay == -1) {
            endGame();
            this.timerLine.stop();
            return;
        }

        this.timerLine.stop();

        // Animate length
        this.timerLine.getKeyFrames().add(new KeyFrame(Duration.millis(0),
                new KeyValue(this.timerBar.widthProperty(), gameWindow.getWidth())));


        this.timerLine.getKeyFrames().add(new KeyFrame(Duration.millis(delay),
                new KeyValue(this.timerBar.widthProperty(), 0)));

        // Animate color
        FillTransition turningYellow = new FillTransition(Duration.millis(delay / 2), this.timerBar, Color.GREEN, Color.YELLOW);
        FillTransition turningRed = new FillTransition(Duration.millis(delay / 2), this.timerBar, Color.YELLOW, Color.RED);
        turningYellow.setOnFinished((e) -> {
            turningRed.play();
        });

        this.timerLine.play();
        turningYellow.play();

    }
}