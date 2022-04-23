package uk.ac.soton.comp1206.game;


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


    /**
     * Create a new instance of MultiplayerGame
     *
     * @param cols number of columns
     * @param rows number of rows
     */
    public MultiplayerGame(int cols, int rows) {
        super(cols, rows);
        this.isMultiplayer = true;
    }

    /**
     * Add a new GamePiece to
     *
     * @param pieceValue value of incoming piece
     */
    public void enqueuePiece(int pieceValue) {
        logger.info("Adding new piece {} to the game", pieceValue);
        pieceQueue.add(GamePiece.createPiece(pieceValue));
    }
    @Override
    public GamePiece spawnPiece() {
        this.listener.msgToSend("PIECE");
        return pieceQueue.poll();
    }

    /**
     * Set the listener handling send message events
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
                loseLife();
                listener.msgToSend("LIVES " + lives.getValue().toString());

                // When no life remains, pass -1 as parameter to gameLoopListener to stop the
                // challenge.
                if (lives.get() == -1) {
                    endGame();
                    listener.msgToSend("DIE");
                    gameLoopListener.gameLoops(-1);
                    return;
                }

                multiplier.set(1);
                afterPiece();
                resetTimer();
            }
        };

        this.gameLoopListener.gameLoops(delay);

        // Start the timer
        this.countdownTimer.schedule(countdownTask, delay);
    }

    @Override
    public void loseLife() {
        logger.info("A life is lost");
        this.lives.set(this.lives.get() - 1);
        this.listener.msgToSend("LIVES " + this.lives.get());
        playSound("lifelose.wav");
    }

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

    public void saveScore(ListProperty<Pair<String, Integer>> scores) {
        this.scoreList = scores;
    }

    public ListProperty<Pair<String, Integer>> getScoreList() {
        return scoreList;
    }
}
