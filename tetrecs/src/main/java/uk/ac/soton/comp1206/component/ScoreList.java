package uk.ac.soton.comp1206.component;

import javafx.animation.FadeTransition;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
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

    private VBox localScoreBox;

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
        FadeTransition fadeScore = new FadeTransition(Duration.millis(1000), localScoreBox);
        fadeScore.setFromValue(0);
        fadeScore.setToValue(1);
        fadeScore.play();
    }

    public void build() {
        // Build layout
        localScoreBox = new VBox();
        var scoreTitle = new Text("High scores");
        scoreTitle.getStyleClass().add("title");
        localScoreBox.getChildren().add(scoreTitle);
        localScoreBox.setAlignment(Pos.TOP_CENTER);

        for (int i = 0; i < this.scoresList.size(); i++) {
            var newPair = scoresList.get(i);
            var thisScore = new HBox();

            logger.info("Adding new score to scorelist. name:{}, key:{}", newPair.getKey(), newPair.getValue());
            var playerName = new Text(newPair.getKey() + ": ");
            var playerScore = new Text(newPair.getValue().toString());
            playerName.getStyleClass().add("scorelist");
            playerScore.getStyleClass().add("scorelist");
            thisScore.getChildren().addAll(playerName, playerScore);
            localScoreBox.getChildren().add(thisScore);
            thisScore.setAlignment(Pos.CENTER);
        }
        this.setCenter(localScoreBox);
    }

    /**
     * Allow update this score list.
     * 
     * @param scoresList new score list
     */
    public void update(ObservableList<Pair<String, Integer>> scoresList) {
        this.scoresList = scoresList;
        build();
    }

}
