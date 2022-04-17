package uk.ac.soton.comp1206.scene;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
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
    private ListProperty<Pair<String, Integer>> localScores;
    private ScoreList scoreList;
    /**
     * Create a new instance of ScoreScene
     * 
     * @param gameWindow The GameWindow it belongs to
     */
    public ScoreScene(GameWindow gameWindow, Game game) {
        super(gameWindow);
        this.game = game;

        var localScorePairs = new ArrayList<Pair<String, Integer>>();
        localScores = new SimpleListProperty<Pair<String, Integer>>(FXCollections.observableArrayList(localScorePairs));
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

        int scoreListIndex = checkScore();
        if (scoreListIndex != -1) {
            // Scene texts
            var gameOver = new Text("Game Over");
            gameOver.getStyleClass().add("bigtitle");
            var highScorePromot = new Text("You beats a historic high score!");
            var savePromot = new Text("Save it to proceed.");
            savePromot.getStyleClass().add("title");
            highScorePromot.getStyleClass().add("title");

            var nameTextField = new TextField("Your unique identifier");
            var saveName = new Button("Save");
            saveName.setOnMouseClicked((e) -> {
                String name = nameTextField.getText();
                localScores.add(scoreListIndex, new Pair<String, Integer>(name, game.getScore().get()));
                writeScore();

                Platform.runLater(() -> {
                    displayScoreList();
                });
            });

            var titleBox = new VBox();
            titleBox.getChildren().addAll(gameOver, highScorePromot, savePromot, nameTextField, saveName);
            VBox.setVgrow(nameTextField, Priority.ALWAYS);
            mainPane.setTop(titleBox);
            titleBox.setAlignment(Pos.CENTER);
        } else {
            displayScoreList();
        }

    }

    /**
     * Initialise the score screen.
     */
    @Override
    public void initialise() {
        logger.info("Initialising..");
        scoreList = new ScoreList(localScores);

        // Update scoreList when localScores changed
        localScores.addListener(new ListChangeListener<Pair<String, Integer>>() {
            @Override
            public void onChanged(Change<? extends Pair<String, Integer>> c) {
                scoreList.update(localScores);
                scoreList.reveal();

            };
        });

        gameWindow.getScene().setOnKeyPressed((e) -> {
            switch (e.getCode()) {
                case ESCAPE:
                    gameWindow.startMenu();
                    break;
                default:
                    break;

            }
        });
    }

    public void displayScoreList() {
        root.getChildren().clear();
        logger.info("Rebuilding {} to display score list", this.getClass().toString());

        var mainPane = new BorderPane();
        mainPane.setMaxHeight(gameWindow.getHeight());
        mainPane.setMaxWidth(gameWindow.getWidth());
        root.getChildren().add(mainPane);
        mainPane.getStyleClass().add("menu-background");

        // Title
        var gameOver = new Text("Game Over");
        gameOver.getStyleClass().add("bigtitle");
        var titleBox = new VBox();
        titleBox.getChildren().add(gameOver);
        mainPane.setTop(titleBox);
        titleBox.setAlignment(Pos.CENTER);

        // Score list
        scoreList = new ScoreList(localScores);
        scoreList.getStyleClass().add("scorelist");
        var localScoreTxt = new Text("Local High Scores");
        localScoreTxt.getStyleClass().add("title");
        var localScoreBox = new VBox();
        localScoreBox.getChildren().addAll(localScoreTxt, scoreList);
        localScoreBox.setStyle("-fx-padding: 30;");

        var remoScoreTxt = new Text("Online High Scores");
        var notAvailable = new Text("Not available");
        notAvailable.getStyleClass().add("scorelist");
        remoScoreTxt.getStyleClass().add("title");
        var remoScoreBox = new VBox();
        remoScoreBox.getChildren().addAll(remoScoreTxt, notAvailable);
        remoScoreBox.setStyle("-fx-padding: 30;");

        var scorePane = new BorderPane();
        scorePane.setLeft(localScoreBox);
        scorePane.setRight(remoScoreBox);
        mainPane.setCenter(scorePane);

        // mainPane.setLeft(localScoreBox);
        // mainPane.setRight(remoScoreBox);
        scoreList.reveal();
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
                for (int i = 0; i < thisScore.length; i++) {
                }
                // Check for invalid player name
                if (thisScore[0] == "") {
                    thisScore[1] = thisScore[thisScore.length - 1];
                    thisScore[0] = "invalid player name";
                }

                localScores.add(new Pair<String, Integer>(thisScore[0], Integer.parseInt(thisScore[1])));
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
     * Write score data in property localScores to the score file.
     */
    private void writeScore() {
        try {
            FileWriter nFileWriter = new FileWriter("score.txt");

            // Clear the content of score file.
            nFileWriter.write("");

            var scoreIerable = this.localScores.iterator();
            while (scoreIerable.hasNext()) {
                var thisScore = scoreIerable.next();
                nFileWriter.append(thisScore.getKey() + ":" + thisScore.getValue() + "\n");
            }

            nFileWriter.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

    }

    /**
     * Write specific score data to the score file.
     * 
     * @param scores Score data to be wrote.
     */
    public void writeScore(ListProperty<Pair<String, Integer>> scores) {
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

    /**
     * Check if game score beats any saved scores.
     * 
     * @return index of the score beaten, or -1 if defeated.
     */
    public int checkScore() {
        logger.info("Checking score..");
        // Only store local score up to 5 entries.
        // When number of saved scores less than 5 score
        if (this.localScores.size() < 4) {
            for (int i = 0; i < this.localScores.size(); i++) {
                if (localScores.get(i).getValue() < game.getScore().get()) {
                    return i;
                }
            }
            return this.localScores.size();
        } else {
            // Check if game score beats any saved score.
            for (int i = 0; i < 5; i++) {
                if (localScores.get(i).getValue() < game.getScore().get()) {
                    return i;
                }
            }

            return -1;

        }

    }
}
