package uk.ac.soton.comp1206.scene;

import javafx.animation.RotateTransition;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.ac.soton.comp1206.game.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * Create a new menu scene
     * 
     * @param gameWindow the Game Window this will be displayed in
     */
    public MenuScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");
        root.getChildren().add(menuPane);

        var mainPane = new GridPane();
        menuPane.getChildren().add(mainPane);

        // Better title
        try {
            FileInputStream logo = new FileInputStream(MenuScene.class.getResource("/images/TetrECS.png").getPath());
            Image logoImage = new Image(logo);
            ImageView logoView = new ImageView(logoImage);
            logoView.setFitHeight(40);
            logoView.setFitWidth(200);
            RotateTransition logoRT = new RotateTransition(Duration.millis(3000), logoView);
            this.logoRT(logoRT);

            var button = new Button("Single Player");

            // Bind the button action to the startGame method in the menu
            button.setOnAction(this::startGame);
            button.getStyleClass().add("menuItem");

            mainPane.add(logoView, 2, 0);
            mainPane.add(button, 2, 2);

            ColumnConstraints colum1 = new ColumnConstraints();
            colum1.setPercentWidth(37);
            mainPane.getColumnConstraints().add(colum1);

            RowConstraints row1 = new RowConstraints();
            row1.setPercentHeight(40);
            RowConstraints row2 = new RowConstraints();
            row2.setPercentHeight(20);
            mainPane.getRowConstraints().addAll(row2, row1, row2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {
        Multimedia.playMenu();
    }

    /**
     * Handle when the Start Game button is pressed
     * 
     * @param event event
     */
    private void startGame(ActionEvent event) {
        Multimedia.stopMenu();
        gameWindow.startChallenge();
    }

    /**
     * Handle when the start instruction button is pressed
     * 
     * @param event event
     */
    private void startInstruction(ActionEvent event) {
        gameWindow.startInstruction();
    }

    private void logoRT(RotateTransition logoRT) {
        logoRT.setFromAngle(10);
        logoRT.setByAngle(-20);
        logoRT.setCycleCount(2);

        logoRT.setAutoReverse(true);
        logoRT.play();
        logoRT.setOnFinished((event) -> this.logoRT(logoRT));
    }

}
