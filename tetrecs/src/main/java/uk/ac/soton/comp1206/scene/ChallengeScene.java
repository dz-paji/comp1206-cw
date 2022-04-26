package uk.ac.soton.comp1206.scene;

import javafx.animation.FillTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
    protected final PieceBoard pieceBoard;

    /**
     * The PieceBoard displays the following piece.
     */
    protected final PieceBoard followingPieceBoard;

    /**
     * A pair of integer keeping track of current aim
     */
    protected IntegerProperty[] aimWare = { new SimpleIntegerProperty(0), new SimpleIntegerProperty(0) };

    /**
     * GameBoard of the game.
     */
    protected GameBoard board;

    /**
     * Timeline to animate the timer
     */
    protected final Timeline timerLine = new Timeline();

    /**
     * Actual representation of timer
     */
    protected Rectangle timerBar;

    /**
     * A list of keycodes which stores series of keys pressed.
     */
    private ListProperty<KeyCode> konamiList;

    /**
     * Create a new Single Player challenge scene
     * 
     * @param gameWindow the Game Window displays this scene
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

        var highScore = new Text();
        var highScore_text = new Text(
                "Highest score:");
        highScore.textProperty().bind(game.getHighScore().asString());
        highScore.getStyleClass().add("hiscore");
        highScore_text.getStyleClass().add("heading");

        var statsBox = new VBox();
        statsBox.getChildren().addAll(level_text, level, lives_text, lives, multiplier_text, multiplier, score_text,
                score, highScore_text, highScore);


        // Rotate currentPiece when clicking PieceBoard
        pieceBoard.setOnBlockClick((e) -> {
            game.rotateCurrentPiece();
        });

        // Swap GamePieces when clicking followingPieceBoard
        followingPieceBoard.setOnBlockClick(this::pieceBoardClicked);

        // Implementing Timer Bar
        timerBar = new Rectangle(gameWindow.getWidth(), 10, Color.BLUE);

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

        pieceBoard.setPiece(game.getPiece());
        pieceBoard.toggleIndicator();
        followingPieceBoard.setPiece(game.getFollowingPiece());

        getHighestScore();

        // Initialise konami cheat code list
        konamiList = new SimpleListProperty<KeyCode>(FXCollections.observableArrayList(new ArrayList<KeyCode>()));

        // Key board support
        gameWindow.getScene().setOnKeyPressed((e) -> {
            logger.info("Key {} pressed", e.getCharacter());
            switch (e.getCode()) {
                case UP, W -> {
                    addKeyPress(e.getCode());
                    aimUp();
                }
                case DOWN, S -> {
                    addKeyPress(e.getCode());
                    aimDown();
                }
                case LEFT, A -> {
                    addKeyPress(e.getCode());
                    aimLeft();
                }
                case RIGHT, D -> {
                    addKeyPress(e.getCode());
                    aimRight();
                }
                case ENTER -> {
                    blockClicked(board.getBlock(aimWare[0].get(), aimWare[1].get()));
                    konamiCheck();
                }
                case X -> {
                    addKeyPress(e.getCode());
                    blockClicked(board.getBlock(aimWare[0].get(), aimWare[1].get()));
                }
                case Z, C, CLOSE_BRACKET -> {
                    addKeyPress(e.getCode());
                    game.rotateCurrentPiece();
                }
                case SPACE, R -> {
                    addKeyPress(e.getCode());
                    game.swapPiece();
                }
                case OPEN_BRACKET, Q, E -> {
                    addKeyPress(e.getCode());
                    rotateAnticlockwise();
                }
                case ESCAPE -> exitGame();
                default -> addKeyPress(e.getCode());
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
        game.playSound("explode.wav");
        Multimedia.stopBGM();
        game.endGame();
        Platform.runLater(() -> {
            gameWindow.startScore(game);
        });
    }

    /**
     * Exit game on ESC press.
     */
    private void exitGame() {
        logger.info("Exit the game");
        Multimedia.stopBGM();
        game.endGame();
        Platform.runLater(() -> {
            gameWindow.startMenu();
        });

    }

    /**
     * Handles when key is pressed.
     * 
     * @param e The key pressed event
     */
    public void keyHandler(KeyEvent e) {
        logger.info("Key being pressed is: {}", e.getCharacter());
    }

    /**
     * Aim at the block higher than above
     */
    protected void aimUp() {
        if (aimWare[1].get() > 0) {
            aimWare[1].set(aimWare[1].get() - 1);
        }
    }

    /**
     * Aim at the block in the below
     */
    protected void aimDown() {
        if (aimWare[1].get() < game.getRows() - 1) {
            aimWare[1].set(aimWare[1].get() + 1);
        }
    }

    /**
     * Aim at the block in the left
     */
    protected void aimLeft() {
        if (aimWare[0].get() > 0) {
            aimWare[0].set(aimWare[0].get() - 1);
        }
    }

    /**
     * Aim at the block to the right
     */
    protected void aimRight() {
        if (aimWare[0].get() < game.getCols() - 1) {
            aimWare[0].set(aimWare[0].get() + 1);
        }
    }

    /**
     * Draw the aim indication at game board when x changed.
     * @param observable The observable value
     * @param oldNumber old x coordinate
     * @param newNumber new x coordinate
     */
    protected void aimXUpdate(ObservableValue<? extends Number> observable, Number oldNumber, Number newNumber) {
        board.rePaintAll();
        board.getBlock(aimWare[0].get(), aimWare[1].get()).highlight();
        board.getBlock(oldNumber.intValue(), aimWare[1].get()).paint();
    }

    /**
     * Draw the aim indication at game board when y changed
     * @param observable the observable value
     * @param oldNumber old y coordinate
     * @param newNumber new y coordinate
     */
    protected void aimYUpdate(ObservableValue<? extends Number> observable, Number oldNumber, Number newNumber) {
        board.rePaintAll();
        board.getBlock(aimWare[0].get(), aimWare[1].get()).highlight();
        board.getBlock(aimWare[0].get(), oldNumber.intValue()).paint();
    }

    /**
     * Do the fade out animation when a line is cleared
     * @param coordinates Set of GameBlockCoordinate which got cleared.
     */
    protected void lineCleared(Set<GameBlockCoordinate> coordinates) {
        board.fadeOut(coordinates);
    }

    /**
     * Bring animation to timer
     * @param delay duration of animation
     */
    protected void animateTimerBar(int delay) {
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
        FillTransition turningYellow = new FillTransition(Duration.millis(delay), this.timerBar, Color.GREEN,
                Color.RED);

        this.timerLine.play();
        turningYellow.play();

    }

    private void getHighestScore() {
        try {
            BufferedReader scoreReader = new BufferedReader(new FileReader("score.txt"));
            var scoreRecord = scoreReader.readLine();

            if (scoreRecord == null) {
                game.setHighestScore(0);
            } else {
                game.setHighestScore(Integer.parseInt(scoreRecord.split(":")[1]));
            }
            scoreReader.close();
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void konamiCheck() {
        logger.info("checking konami code");
        if (this.konamiList.size() < 10) {
            this.konamiList.clear();
        } else {
            KeyCode[] ruleList = {KeyCode.UP, KeyCode.UP, KeyCode.DOWN, KeyCode.DOWN, KeyCode.LEFT, KeyCode.RIGHT, KeyCode.LEFT, KeyCode.RIGHT, KeyCode.B, KeyCode.A};
            for (int i = 0; i < 10; i ++) {
                if (ruleList[i] != konamiList.get(i)) {
                    konamiList.clear();
                    return;
                }
            }

            logger.info("konami code triggered.");
            game.setLives(999);
            board.resetBoard();
        }
    }

    private void addKeyPress(KeyCode key) {
        this.konamiList.add(key);
    }

    /**
     * Rotate current piece in anti-clockwise direction
     */
    public void rotateAnticlockwise() {
        game.rotateCurrentPieceAnticlockwise();
    }
}