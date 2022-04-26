package uk.ac.soton.comp1206.event;

/**
 * This event handles the gameloop timer information exchange.
 */
public interface GameLoopListener {
    
    /**
     * Handle a game loop completes event.
     * 
     * @param delay new delay for game loop timer
     */
    public void gameLoops(int delay);
}
