package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
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
import uk.ac.soton.comp1206.game.Multimedia;
import uk.ac.soton.comp1206.game.MultiplayerGame;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * Hold the UI, interaction and logic of a Multiplayer Game.
 */
public class MultiplayerGameScene extends ChallengeScene {

    private final VBox versusScoreBox = new VBox();
    private static final Logger logger = LogManager.getLogger(MultiplayerGameScene.class);

    private final Communicator communicator;

    private MultiplayerGame game;

    private final TextField msgField = new TextField();

    private final ScrollPane msgPane = new ScrollPane(new Text("Press \"T\" to start typing and ENTER to send."));


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
        board.setOnRightClick((e) -> game.rotateCurrentPiece());

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

        var statsBox = new VBox();
        statsBox.getChildren().addAll(level_text, level, lives_text, lives, score_text,
                score);
        statsBox.getChildren().addAll(pieceBoard, followingPieceBoard);
        statsBox.setAlignment(Pos.CENTER);
        mainPane.setRight(statsBox);


        // Show versus scores
        var versusText = new Text(
                "Versus players:");
        versusText.getStyleClass().add("heading");
        var versusBox = new VBox();
        versusBox.getChildren().addAll(versusText, versusScoreBox);
        versusBox.getStyleClass().add("playerBox");
        mainPane.setLeft(versusBox);

        // Rotate currentPiece when clicking PieceBoard
        pieceBoard.setOnBlockClick((e) -> game.rotateCurrentPiece());

        // Swap GamePieces when clicking followingPieceBoard
        followingPieceBoard.setOnBlockClick(this::pieceBoardClicked);

        // Implementing Timer Bar
        timerBar = new Rectangle(gameWindow.getWidth(), 10, Color.BLUE);

        mainPane.setTop(timerBar);
        BorderPane.setAlignment(timerBar, Pos.TOP_CENTER);

        // Bottom chat area
        var chatBox = new VBox();
        chatBox.setAlignment(Pos.BASELINE_LEFT);
        chatBox.getChildren().addAll(msgPane, msgField);
        msgPane.getStyleClass().add("scroller");
        msgField.setVisible(false);
        chatBox.getStyleClass().add("messages");
        mainPane.setBottom(chatBox);

    }

    /**
     * Setup Multiplayer game and add listeners.
     */
    @Override
    public void setupGame() {
        logger.info("Setting up game");
        game = new MultiplayerGame(5, 5);

        this.communicator.addListener(this::msgHandler);

        // feed 2 piece to game
        this.communicator.send("PIECE");
        this.communicator.send("PIECE");
        this.communicator.send("SCORES");


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

        game.setCommuListener(communicator::send);
    }

    /**
     * Initialise the game
     */
    @Override
    public void initialise() {
        logger.info("Initialise game");
        Multimedia.playBGM();

        game.start();
        this.communicator.send("SCORES");
        // Show current Piece
        if (game.getPiece() == null) {
            try {
                gameWindow.wait(5000);
            } catch (InterruptedException e){
                logger.error(e.getStackTrace());
            }

        }
        pieceBoard.setPiece(game.getPiece());
        pieceBoard.toggleIndicator();
        followingPieceBoard.setPiece(game.getFollowingPiece());


        // Key press support
        gameWindow.getScene().setOnKeyPressed((e) -> {
            logger.info("Key {} pressed", e.getCharacter());
            switch (e.getCode()) {
                case UP, W -> aimUp();
                case DOWN, S -> aimDown();
                case LEFT, A -> aimLeft();
                case RIGHT, D -> aimRight();
                case X -> blockClicked(board.getBlock(aimWare[0].get(), aimWare[1].get()));
                case Z, C, CLOSE_BRACKET -> game.rotateCurrentPiece();
                case SPACE, R -> game.swapPiece();
                case OPEN_BRACKET, Q, E -> rotateAnticlockwise();
                case ESCAPE -> endGame();
                case T -> this.msgField.setVisible(true);
            }
        });

        aimWare[0].addListener(this::aimXUpdate);
        aimWare[1].addListener(this::aimYUpdate);

        msgField.setOnKeyPressed((key) -> {
            if (key.getCode() == KeyCode.ENTER) {
                this.communicator.send("MSG " + msgField.getText());
                msgField.clear();
                msgField.setVisible(false);
            }
        });

    }

    private void blockClicked(GameBlock block) {
        game.blockClicked(block);
    }

    private void pieceBoardClicked(GameBlock block) {
        game.swapPiece();
    }

    private void scoreMsgHandler(String msg) {
        if (msg.split(" ")[1].matches("\\w+:\\d+")) {
            // Update player's score
            logger.info("Processing a score update message");
            String[] thisScore = msg.split(" ")[1].split(":");

            for (Node thisNode : versusScoreBox.getChildren()) {
                Text thisLabel = (Text) thisNode;
                if (thisLabel.getId().equals(thisScore[0])) {
                    thisLabel.setText(thisScore[0] + ": " + thisScore[1]);
                }
            }
        } else {
            logger.info("Processing a score list message");
            versusScoreBox.getChildren().clear();

            for (String thisScore : msg.split(" ")[1].split("\n")) {
                logger.info("this score object contains: {}", thisScore);
                String[] scoreInfo = thisScore.split(":");
                var playerLabel = new Text();
                playerLabel.setId(scoreInfo[0]);

                // Check whether the player died
                if (scoreInfo[2].equals("DEAD")) {
                    playerLabel.setText(scoreInfo[0] + ": " + scoreInfo[2]);
                } else {
                    playerLabel.setText(scoreInfo[0] + ": " + scoreInfo[1]);
                }

                versusScoreBox.getChildren().add(playerLabel);
            }
        }
    }

    private void chatMsgHandler(String msg) {
        Text playerMsg = new Text(msg.split(" ")[1].replace(":", ": "));
        msgPane.setContent(playerMsg);
    }

    private void msgHandler(String msg) {
        switch (msg.substring(0, 4)) {
            case "SCOR" -> Platform.runLater(() -> scoreMsgHandler(msg));
            case "PIEC" -> Platform.runLater(() -> game.enqueuePiece(Integer.parseInt(msg.split(" ")[1])));
            case "MSG " -> Platform.runLater(() -> chatMsgHandler(msg));
            default -> logger.info("Unhandled msg.");
        }
    }

    @Override
    public void rotateAnticlockwise() {
        game.rotateCurrentPieceAnticlockwise();
    }

    private void endGame() {
        this.communicator.send("DIE");
        logger.info("Cleanning up the game...");
        game.playSound("explode.wav");
        Multimedia.stopBGM();
        game.endGame();
        Platform.runLater(() -> {
            gameWindow.startScore(game);
        });
    }

}
