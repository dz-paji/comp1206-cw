package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.component.GameBlock;

/**
 * The MouseHoverListener is used to highlight the block currently hovering
 * over.
 */
public interface MouseHoverListener {

    /**
     * Handle a block houver event.
     * 
     * @param gameBlock the block that hovers.
     */
    public void blockHover(GameBlock gameBlock);
}
