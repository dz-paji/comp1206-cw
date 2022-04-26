package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.component.GameBlock;

/**
 * This is the listener handles when right clicking the GameBoard or GamePiece.
 */
public interface RightClickedListener {

    /**
     * Handles the event when user right-clicks a gameblock
     * 
     * @param gameBlock the gameblock that was right-clicked.
     */
    public void rightClicked(GameBlock gameBlock);
}