package uk.ac.soton.comp1206.scene;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.layout.BorderPane;
import javafx.util.Pair;
import uk.ac.soton.comp1206.component.ScoreList;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is the scene for displaying score screen.
 * Holds the UI for displaying score.
 */
public class ScoreScene extends BaseScene {
    private static final Logger logger = LogManager.getLogger(ScoreScene.class);

    private Game game;
    private ObservableList<Pair<String, Integer>> localScores;
    private ScoreList scoreList;

    /**
     * Create a new instance of ScoreScene
     * 
     * @param gameWindow The GameWindow it belongs to
     */
    public ScoreScene(GameWindow gameWindow, Game game) {
        super(gameWindow);
        this.game = game;

        var scorePairs = new ArrayList<Pair<String, Integer>>();
        localScores = new SimpleListProperty<Pair<String, Integer>>(FXCollections.observableArrayList(scorePairs));
        loadScore();
    }

    /**
     * Build the Score scene layout
     */
    @Override
    public void build() {
        logger.info("Building {}", this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());
        var mainPane = new BorderPane();
        mainPane.setMaxHeight(gameWindow.getHeight());
        mainPane.setMaxWidth(gameWindow.getWidth());
        root.getChildren().add(mainPane);
        mainPane.getStyleClass().add("menu-background");

        scoreList = new ScoreList(localScores);
        scoreList.getStyleClass().add("scorelist");

        mainPane.setLeft(scoreList);
    }

    /**
     * Initialise the score screen.
     */
    @Override
    public void initialise() {
        logger.info("Initialising..");
        scoreList = new ScoreList(localScores);
        localScores.addListener(new ListChangeListener<Pair<String, Integer>>() {
            @Override
            public void onChanged(Change<? extends Pair<String, Integer>> c) {
                scoreList.build();

            };
        });

    }

    /**
     * Load score from file to an ordered list
     */
    public void loadScore() {
        try {
            logger.info("Loading score from file.");
            // Read scores
            FileReader scoreFile = new FileReader("score.txt");
            BufferedReader scoreFileReader = new BufferedReader(scoreFile);

            String nextLine;
            while ((nextLine = scoreFileReader.readLine()) != null) {
                String[] thisScore = nextLine.split(":");
                localScores.add(new Pair<String, Integer>(thisScore[0], Integer.parseInt(thisScore[1])));
                logger.info("New score record added to localScores. Key:{}, value:{}",thisScore[0],Integer.parseInt(thisScore[1]));
            }

            scoreFile.close();

        } catch (FileNotFoundException e) {
            logger.error("File not found. Creating a new one");
            // Create a new file with default value
            writeDummyScore();
            loadScore();
        } catch (IOException e) {
            logger.error(e.getStackTrace());
        }
    }

    /**
     * Create a dummy score file if it doesn't exist.
     */
    public void writeDummyScore() {
        var devScorePairs = new ArrayList<Pair<String, Integer>>();
        devScorePairs.add(new Pair<String, Integer>("Dev", 1024));
        var devScores = new SimpleListProperty<Pair<String, Integer>>(FXCollections.observableArrayList(devScorePairs));

        writeScore(devScores);
    }

    /**
     * Write specific score data to the score file.
     * 
     * @param scores Score data to be wrote.
     */
    public void writeScore(ObservableList<Pair<String, Integer>> scores) {
        try {
            FileWriter nFileWriter = new FileWriter("score.txt");

            // Clear the content of score file.
            nFileWriter.write("");

            var scoreIerable = scores.iterator();
            while (scoreIerable.hasNext()) {
                var thisScore = scoreIerable.next();
                nFileWriter.append(thisScore.getKey() + ":" + thisScore.getValue() + "\n");
            }

            nFileWriter.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

    }

}
