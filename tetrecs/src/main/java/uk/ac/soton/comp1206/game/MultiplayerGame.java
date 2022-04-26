package uk.ac.soton.comp1206.game;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.ChannelMsgListener;

import java.util.ArrayDeque;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Contains multiplayer game logic.
 */
public class MultiplayerGame extends Game {
    private static final Logger logger = LogManager.getLogger(MultiplayerGame.class);

    private final ArrayDeque<GamePiece> pieceQueue = new ArrayDeque<>();

    private ChannelMsgListener listener;

    private ListProperty<Pair<String, Integer>> scoreList;

    private Boolean isDead = false;

    /**
     * Create a new instance of MultiplayerGame
     * Automatically creates a Grid
     *
     * @param cols number of columns
     * @param rows number of rows
     */
    public MultiplayerGame(int cols, int rows) {
        super(cols, rows);
        this.isMultiplayer = true;
    }

    /**
     * Add a new GamePiece to to piece queue
     *
     * @param pieceValue value of incoming piece
     */
    public void enqueuePiece(int pieceValue) {
        logger.info("Adding new piece {} to the game", pieceValue);
        pieceQueue.add(GamePiece.createPiece(pieceValue));
    }

    /**
     * Dequeue a piece from piecequeue then
     * Send a PIECE message to server requesting a new GamePiece
     * 
     * @return First GamePiece in piece queue
     */
    @Override
    public GamePiece spawnPiece() {
        this.listener.msgToSend("PIECE");
        return pieceQueue.poll();
    }

    /**
     * Set the listener handling send message events
     * 
     * @param listener the listener
     */
    public void setCommuListener(ChannelMsgListener listener) {
        this.listener = listener;
    }

    private void gameLoop() {

        int delay = getTimerDelay();
        this.countdownTimer = new Timer();
        TimerTask countdownTask = new TimerTask() {
            @Override
            public void run() {

                // When no life remains, pass -1 as parameter to gameLoopListener to stop the
                // challenge.
                if (lives.get() <= 0) {
                    // cancel timer, set player to dead
                    endGame();
                    gameLoopListener.gameLoops(-1);

                    Platform.runLater(() -> {
                        listener.msgToSend("DIE");
                    });
                    return;
                }

                loseLife();

                // Update lives to server and reset timer
                Platform.runLater(() -> {
                    listener.msgToSend("LIVES " + lives.getValue().toString());
                    multiplier.set(1);
                    afterPiece();
                    resetTimer();
                });
            }
        };

        this.gameLoopListener.gameLoops(delay);

        // Start the timer
        this.countdownTimer.schedule(countdownTask, delay);
    }

    /**
     * Deduct the remaining life by 1 and notify server of life change.
     */
    @Override
    public void loseLife() {
        logger.info("A life is lost");
        this.lives.set(this.lives.get() - 1);
        this.listener.msgToSend("LIVES " + this.lives.get());
        playSound("lifelose.wav");
    }

    /**
     * Initialise a new game and set everything up at the begining of the game.
     */
    @Override
    public void initialiseGame() {
        logger.info("Initialising game");

        this.score.addListener((event) -> {
            logger.info("Score got changed, new score: {}", score.get());
            int changedScore = this.score.get() / 1000;
            if (changedScore - this.scoreTracker > 0) {
                this.scoreTracker += changedScore;
                setLevel(getLevel().get() + changedScore);
                playSound("level.wav");
            }
            this.listener.msgToSend("SCORE " + this.score.get());
        });

        gameLoop();
    }

    /**
     * Save the score of this multiplayer game for scorescene
     * 
     * @param scores List of scores
     */
    public void saveScore(ListProperty<Pair<String, Integer>> scores) {
        this.scoreList = scores;
    }

    /**
     * Get the scores of this multiplayer game
     * 
     * @return list of scores of this game
     */
    public ListProperty<Pair<String, Integer>> getScoreList() {
        return scoreList;
    }

    /**
     * Handles everything after game over.
     * Set player death status to true and cancel game timer.
     */
    @Override
    public void endGame() {
        this.isDead = true;
        this.countdownTimer.cancel();
    }

    /**
     * Return status of this game. Dead or Alive
     * 
     * @return true if player dead
     */
    public Boolean isDead() {
        return isDead;
    }
}
