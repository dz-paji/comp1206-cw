package uk.ac.soton.comp1206.event;

import java.util.Set;

import uk.ac.soton.comp1206.component.GameBlockCoordinate;

/**
 * This is the listener handles fading out animation when being cleared.
 */
public interface LineClearedListener {
    
    /**
     * Handles the event when a line is cleared
     * 
     * @param coordinates Set of coordinates of gameblocks that forms the line being cleared.
     */
    public void clearedLine(Set<GameBlockCoordinate> coordinates);
}
