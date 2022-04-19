package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.game.MultiplayerGame;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * Hold the UI, interaction and logic of a Multiplayer Game.
 */
public class MultiplayerGameScene extends ChallengeScene {

    private final VBox versusScoreBox = new VBox();
    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    private final Communicator communicator;

    private MultiplayerGame game;


    /**
     * Create a new instance of Multiplayer Game.
     *
     * @param gameWindow the game window
     */
    public MultiplayerGameScene(GameWindow gameWindow) {
        super(gameWindow);
        this.communicator = gameWindow.getCommunicator();
    }

    /**
     * Build the UI layout of this scene.
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

        var score = new Text();
        var score_text = new Text(
                "Current score:");
        score.textProperty().bind(game.getScore().asString());
        score.getStyleClass().add("score");
        score_text.getStyleClass().add("heading");

        var versusText = new Text(
                "Versus players:");
        versusText.getStyleClass().add("heading");

        var statsBox = new VBox();
        statsBox.getChildren().addAll(level_text, level, lives_text, lives, score_text,
                score, versusText, versusScoreBox);

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
        timerBar = new Rectangle(gameWindow.getWidth(), 10, Color.BLUE);

        statsBox.getChildren().addAll(pieceBoard, followingPieceBoard);
        statsBox.setAlignment(Pos.CENTER);
        mainPane.setRight(statsBox);
        mainPane.setTop(timerBar);
        BorderPane.setAlignment(timerBar, Pos.TOP_CENTER);

    }

    /**
     * Setup Multiplayer game and add listeners.
     */
    @Override
    public void setupGame() {
        game = new MultiplayerGame(5,5);

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

        game.setCommuListener((msg) -> {
            communicator.send(msg);
        });

    }

    /**
     * Initialise the game
     */
    @Override
    public void initialise() {
        this.communicator.addListener((msg) -> {
            switch (msg.substring(0,4)) {
                case "SCOR" -> {

                }
                case "PIEC" -> {
                    Platform.runLater(() -> {
                        game.enqueuePiece(Integer.parseInt(msg.split(" ")[1]));
                    });
                }
                case "MSG " -> {

                }
            }
        });
    }

    private void blockClicked(GameBlock block) {
        game.blockClicked(block);
    }

    private void pieceBoardClicked(GameBlock block) {
        game.swapPiece();
    }


}
