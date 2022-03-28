package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * PieceSpawnedListener is used when a piece is spwaned. It passes the piece spawned in the message.
 */
public interface PieceSpawnedListener {

    /**
     * Handle a piece spawned event
     * @param currentPiece the piece that spawned.
     */
    public void pieceSpawned(GamePiece currentPiece);
}
