package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
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
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.component.ScoreList;
import uk.ac.soton.comp1206.game.Multimedia;
import uk.ac.soton.comp1206.game.MultiplayerGame;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.ArrayList;

/**
 * Hold the UI, interaction and logic of a Multiplayer Game.
 */
public class MultiplayerGameScene extends ChallengeScene {

    private static final Logger logger = LogManager.getLogger(MultiplayerGameScene.class);

    private final Communicator communicator;

     private MultiplayerGame multiplayerGame;

    private final TextField msgField = new TextField();

    private final ScrollPane msgPane = new ScrollPane(new Text("Press \"T\" to start typing and ENTER to send."));

    private final ListProperty<Pair<String, Integer>> scoreList = new SimpleListProperty<>(FXCollections.observableList(new ArrayList<>()));

    private final ScoreList versusScore = new ScoreList(scoreList);

    private final PieceBoard playerBoard = new PieceBoard(5, 5, 60, 60);

    private final Label playerID = new Label();

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
        board.setOnRightClick((e) -> multiplayerGame.rotateCurrentPiece());

        // Show stats
        var level = new Text();
        var level_text = new Text(
                "Current Level:");
        level.textProperty().bind(multiplayerGame.getLevel().asString());
        level.getStyleClass().add("level");
        level_text.getStyleClass().add("heading");

        var lives = new Text();
        var lives_text = new Text(
                "Lives remain:");
        lives.textProperty().bind(multiplayerGame.getLives().asString());
        lives.getStyleClass().add("lives");
        lives_text.getStyleClass().add("heading");

        var score = new Text();
        var score_text = new Text(
                "Current score:");
        score.textProperty().bind(multiplayerGame.getScore().asString());
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
        versusBox.getChildren().addAll(versusText, versusScore);
        versusBox.getStyleClass().add("playerBox");
        mainPane.setLeft(versusBox);

        // Show other player's board in versus box
        var playerBox = new VBox();
        playerBox.getChildren().addAll(playerBoard, playerID);
        playerBox.setAlignment(Pos.CENTER);
        versusBox.getChildren().add(playerBox);

        // Rotate currentPiece when clicking PieceBoard
        pieceBoard.setOnBlockClick((e) -> multiplayerGame.rotateCurrentPiece());

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
        multiplayerGame = new MultiplayerGame(5, 5);
        game = multiplayerGame;

        this.communicator.addListener(this::msgHandler);

        // feed 2 piece to game
        this.communicator.send("PIECE");
        this.communicator.send("PIECE");
        this.communicator.send("SCORES");


        // Bind NextPieceListener.
        multiplayerGame.setNextPieceListener((message, e) -> {
            pieceBoard.setPiece(multiplayerGame.getPiece());
            pieceBoard.toggleIndicator();
            followingPieceBoard.setPiece(multiplayerGame.getFollowingPiece());
        });

        // Handle fadeOut animation
        multiplayerGame.setOnLineCleared(this::lineCleared);

        // Register timer
        multiplayerGame.setOnGameLoop(this::animateTimerBar);

        // Register communicator
        multiplayerGame.setCommuListener(communicator::send);
    }

    /**
     * Initialise the game
     */
    @Override
    public void initialise() {
        logger.info("Initialise game");
        Multimedia.playBGM();

        this.communicator.send("SCORES");
        // Show current Piece
        multiplayerGame.start();
        pieceBoard.setPiece(multiplayerGame.getPiece());
        pieceBoard.toggleIndicator();
        followingPieceBoard.setPiece(multiplayerGame.getFollowingPiece());


        // Key press support
        gameWindow.getScene().setOnKeyPressed((e) -> {
            logger.info("Key {} pressed", e.getCharacter());
            switch (e.getCode()) {
                case UP, W -> aimUp();
                case DOWN, S -> aimDown();
                case LEFT, A -> aimLeft();
                case RIGHT, D -> aimRight();
                case X -> blockClicked(board.getBlock(aimWare[0].get(), aimWare[1].get()));
                case Z, C, CLOSE_BRACKET -> multiplayerGame.rotateCurrentPiece();
                case SPACE, R -> multiplayerGame.swapPiece();
                case OPEN_BRACKET, Q, E -> rotateAnticlockwise();
                case ESCAPE -> endGame();
                case T -> this.msgField.setVisible(true);
            }
        });

        aimWare[0].addListener(this::aimXUpdate);
        aimWare[1].addListener(this::aimYUpdate);

        // send by enter
        msgField.setOnKeyPressed((key) -> {
            if (key.getCode() == KeyCode.ENTER) {
                this.communicator.send("MSG " + msgField.getText());
                msgField.clear();
                msgField.setVisible(false);
            }
        });

    }

    /**
     * Handles game logic when gameboard block got clicked
     *
     * @param block the block was clicked
     */
    private void blockClicked(GameBlock block) {
        String boardMsg = multiplayerGame.blockClicked(block);
        this.communicator.send(boardMsg);
    }

    /**
     * Rotate current piece when pieceboard got clicked
     *
     * @param block The block was clicked
     */
    private void pieceBoardClicked(GameBlock block) {
        multiplayerGame.swapPiece();
    }

    /**
     * Handles SCORES message
     *
     * @param msg message received
     */
    private void scoreMsgHandler(String msg) {
        if (msg.split(" ")[1].matches("\\w+:\\d+")) {
            // Update player's score
            logger.info("Processing a score update message");
            String[] thisScore = msg.split(" ")[1].split(":");

            for (int i = 0; i < this.scoreList.size(); i++) {
                var thisPair = scoreList.get(i);
                if (thisPair.getKey().equals(thisScore[0])) {
                    var newPair = new Pair<String, Integer>(thisScore[0], Integer.parseInt(thisScore[1]));
                    scoreList.set(i, newPair);
                    scoreList.remove(thisPair);
                }
            }
        } else {
            logger.info("Processing a score list message");
            scoreList.clear();

            for (String thisScore : msg.split(" ")[1].split("\n")) {
                logger.info("this score object contains: {}", thisScore);
                String[] scoreInfo = thisScore.split(":");
                Pair<String, Integer> newPair;

                // Check whether the player died
                if (scoreInfo[2].equals("DEAD")) {
                    newPair = new Pair<String, Integer>(scoreInfo[0], -1);
                } else {
                    newPair = new Pair<String, Integer>(scoreInfo[0], Integer.parseInt(scoreInfo[1]));
                }

                scoreList.add(newPair);
            }
        }
        versusScore.update(scoreList);
    }

    /**
     * Handles chat message
     *
     * @param msg received message
     */
    private void chatMsgHandler(String msg) {
        Text playerMsg = new Text(msg.split(" ")[1].replace(":", ": "));
        msgPane.setContent(playerMsg);
    }

    /**
     * Registers message handlers.
     *
     * @param msg received message
     */
    private void msgHandler(String msg) {
        switch (msg.substring(0, 4)) {
            case "SCOR" -> Platform.runLater(() -> scoreMsgHandler(msg));
            case "PIEC" -> Platform.runLater(() -> multiplayerGame.enqueuePiece(Integer.parseInt(msg.split(" ")[1])));
            case "MSG " -> Platform.runLater(() -> chatMsgHandler(msg));
            case "BOAR" -> Platform.runLater(() -> boardMsgHandler(msg));
            default -> logger.info("Unhandled msg.");
        }
    }

    /**
     * Handles BOARD message
     *
     * @param msg received message
     */
    private void boardMsgHandler(String msg) {
        // format validator
        if (!msg.contains(":")) {
            return;
        }

        var blockInfo = msg.split(":")[1].split(" ");
        var playerName = msg.split(":")[0].split(" ")[1];
        playerID.setText(playerName);
        playerBoard.resetBoard();
        var blockIndex = 0;

        // Set block value to board
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                playerBoard.updateValue(i, j, Integer.parseInt(blockInfo[blockIndex]));
                blockIndex++;
            }
        }
    }

    /**
     * Rotate this piece anti-clockwise
     */
    @Override
    public void rotateAnticlockwise() {
        multiplayerGame.rotateCurrentPieceAnticlockwise();
    }

    private void endGame() {
        this.communicator.send("DIE");
        logger.info("Cleanning up the game...");
        multiplayerGame.playSound("explode.wav");
        Multimedia.stopBGM();
        multiplayerGame.saveScore(scoreList);
        multiplayerGame.endGame();
        Platform.runLater(() -> gameWindow.startScore(game));
    }

}
