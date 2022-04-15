package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.component.GameBlockCoordinate;

/**
 * This is the listener handles fading out animation when being cleared.
 */
public interface LineClearedListener {
    public void clearedLine(GameBlockCoordinate coordinates);
}
