package uk.ac.soton.comp1206.component;

import javafx.beans.property.ListProperty;
import javafx.util.Pair;

public class LeaderBoard extends ScoreList{
    /**
     * Create a new instance of ScoreList.
     *
     * @param scoresList The list of scores that will be displayed.
     */
    public LeaderBoard(ListProperty<Pair<String, Integer>> scoresList) {
        super(scoresList);
    }
}
