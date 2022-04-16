package uk.ac.soton.comp1206.component;

import javafx.animation.FadeTransition;
import javafx.collections.ObservableList;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScoreList extends BorderPane {

    private static final Logger logger = LogManager.getLogger(ScoreList.class);

    private ObservableList<Pair<String, Integer>> scoresList;

    /**
     * Create a new instance of ScoreList.
     * 
     * @param scoresList The list of scores that will be displayed.
     */
    public ScoreList(ObservableList<Pair<String, Integer>> scoresList) {
        this.scoresList = scoresList;

        build();
    }

    /**
     * Add animation to ScoreList
     */
    public void reveal() {
        // Add fade animation
        FadeTransition fadeScore = new FadeTransition(Duration.millis(500), this);
        fadeScore.play();
    }

    public void build() {
        // Build layout
        var scoreText = new Text("Scores:");
        this.setTop(scoreText);

        var localScores = new VBox();

        for (int i = 0; i < this.scoresList.size(); i++) {
            var newPair = scoresList.get(i);
            var thisScore = new HBox();

            logger.info("Adding new score to scorelist. name:{}, key:{}", newPair.getKey(), newPair.getValue());
            var playerName = new Text(newPair.getKey());

            var playerScore = new Text(newPair.getValue().toString());
            thisScore.getChildren().addAll(playerName, playerScore);
            localScores.getChildren().add(thisScore);
        }

        this.setCenter(localScores);

        reveal();
    }

}
