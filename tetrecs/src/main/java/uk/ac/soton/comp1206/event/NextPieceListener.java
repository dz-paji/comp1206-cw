package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * NextPieceListener is used when a piece is spwaned. It passes the next
 * piece in the message.
 */
public interface NextPieceListener {

    /**
     * Handle a piece spawned event
     * 
     * @param next the piece that spawned.
     */
    public void nextPiece(GamePiece next, GamePiece followingPiece);
}
