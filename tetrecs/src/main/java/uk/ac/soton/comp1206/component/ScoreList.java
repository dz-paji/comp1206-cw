package uk.ac.soton.comp1206.component;

import javafx.animation.FadeTransition;
import javafx.beans.property.ListProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Contains UI component and logic of a Score list. Used to display scores.
 */
public class ScoreList extends BorderPane {

    private static final Logger logger = LogManager.getLogger(ScoreList.class);

    private ListProperty<Pair<String, Integer>> scoresList;

    private VBox localScoreBox;

    /**
     * Create a new instance of ScoreList.
     * 
     * @param scoresList The list of scores that will be displayed.
     */
    public ScoreList(ListProperty<Pair<String, Integer>> scoresList) {
        this.scoresList = scoresList;

        build();
    }

    /**
     * Add animation to ScoreList
     */
    public void reveal() {
        // Add fade animation
        FadeTransition fadeScore = new FadeTransition(Duration.millis(2000), localScoreBox);
        fadeScore.setFromValue(0);
        fadeScore.setToValue(1);
        fadeScore.play();
    }

    /**
     * Build the layout of the scorelist.
     */
    public void build() {
        // Build layout
        localScoreBox = new VBox();
        localScoreBox.setAlignment(Pos.TOP_CENTER);

        for (int i = 0; i < this.scoresList.size(); i++) {
            var newPair = scoresList.get(i);
            var thisScore = new HBox();

            var playerName = new Text(newPair.getKey() + ": ");
            var playerScore = new Text(newPair.getValue().toString());
            if (newPair.getValue() == -1) {
                playerScore.setText("Dead");
            }
            playerName.getStyleClass().add("scorelist");
            playerScore.getStyleClass().add("scorelist");
            thisScore.getChildren().addAll(playerName, playerScore);
            localScoreBox.getChildren().add(thisScore);
            thisScore.setAlignment(Pos.CENTER);
        }
        this.setCenter(localScoreBox);

        this.getStyleClass().add("-fx-padding: 10");
    }

    /**
     * Updating this ScoreList with provided information
     * 
     * @param scoresList new score list
     */
    public void update(ListProperty<Pair<String, Integer>> scoresList) {
        logger.info("Updating ScoreList object");
        this.scoresList = scoresList;
        this.getChildren().clear();
        build();
    }

}
