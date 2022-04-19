package uk.ac.soton.comp1206.game;


import uk.ac.soton.comp1206.event.ChannelMsgListener;
import uk.ac.soton.comp1206.event.CommunicationsListener;

import java.util.ArrayDeque;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Contains multiplayer game logic.
 */
public class MultiplayerGame extends Game {
    private final ArrayDeque<GamePiece> pieceQueue = new ArrayDeque<>();

    private ChannelMsgListener listener;

    /**
     * Create a new instance of MultiplayerGame
     *
     * @param cols number of columns
     * @param rows number of rows
     */
    public MultiplayerGame(int cols, int rows) {
        super(cols, rows);
    }

    /**
     * Add a new GamePiece to
     *
     * @param pieceValue value of incoming piece
     */
    public void enqueuePiece(int pieceValue) {
        pieceQueue.add(GamePiece.createPiece(pieceValue));
    }

    /**
     * Get the next GamePiece in queue
     *
     * @return
     */
    @Override
    public GamePiece getPiece() {
        return pieceQueue.getFirst();
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

}
