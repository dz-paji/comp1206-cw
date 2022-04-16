package uk.ac.soton.comp1206.event;

/**
 * This event handles the gameloop timer information exchange.
 */
public interface GameLoopListener {
    public void gameLoops(int delay);
}
