package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.Game;

/**
 * Handles Challenge ends event
 */
public interface ChallengeEndListener {
    /**
     * Pass through the game object when game ends.
     * 
     * @param game The Game object.
     */
    public void endsChallenge(Game game);
}
