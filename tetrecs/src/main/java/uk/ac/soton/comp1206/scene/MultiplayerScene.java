package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import uk.ac.soton.comp1206.game.Grid;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class MultiplayerScene extends BaseScene {
    private final Communicator communicator;
    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    private final Timer timer = new Timer();

    private ScrollPane channelScrollPane = new ScrollPane();

    private ListProperty<Button> channelButtons;

    private final VBox channelBox = new VBox();

    public MultiplayerScene(GameWindow gameWindow) {
        super(gameWindow);
        this.communicator = gameWindow.getCommunicator();
        this.channelButtons = new SimpleListProperty<Button>(FXCollections.observableList(new ArrayList<Button>()));
    }

    /**
     * Setup scene essentials
     */
    @Override
    public void initialise() {
        // Add listener to handle incoming msgs.
        this.communicator.addListener(this::msgHandler);

        // Polling channels
        TimerTask reqChannels = new TimerTask() {
            @Override
            public void run() {
                communicator.send("LIST");
            }
        };
        timer.scheduleAtFixedRate(reqChannels, 500, 10000);

    }

    /**
     * Build the layout of the scene
     */
    @Override
    public void build() {
        logger.info("Building {}", this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());
        var mainPane = new GridPane();
        mainPane.setGridLinesVisible(true);
        mainPane.getStyleClass().add("multiplayer-background");
        root.getChildren().add(mainPane);

        // Basic UI components
        var joinPane = new GridPane();
        joinPane.getStyleClass().add("join-gridpane");
        var currGameText = new Text("Current Games");
        currGameText.getStyleClass().add("joinpaneTitle");
        joinPane.add(currGameText, 1, 0);
        joinPane.add(channelScrollPane, 1, 2);
        channelScrollPane.setContent(channelBox);
        channelScrollPane.getStyleClass().add("scroller");

        mainPane.add(joinPane, 0, 0,2,4);
    }

    /**
     * Handler to deal this incoming messages
     *
     * @param msg message received
     */
    public void msgHandler(String msg) {
        switch (msg.substring(0, 4)) {
            default:
                logger.info("Unhandled msg. Msg starts with {}", msg.substring(0, 4));
                break;
            case "CHAN":
                Platform.runLater(() -> {
                    msgChannelHandler(msg);
                });
                break;
            case "JOIN":
                Platform.runLater(() -> {
                    msgJoinHandler(msg);
                });
                break;
        }
    }

    private void msgJoinHandler(String msg) {
    }

    public void msgChannelHandler(String msg) {
        logger.info("Handling CHANNELS message.");
        channelBox.getChildren().clear();

        if (msg.split(" ").length < 2) {
            logger.info("No channel available.");
            return;
        }

        String[] channels = msg.split(" ")[1].split("\n");
        for (int i = 0; i < channels.length; i ++) {
            var buttonBox = new HBox();
            var button = new Button(channels[i]);
            button.setId(channels[i]);
            buttonBox.getChildren().add(button);
            buttonBox.setAlignment(Pos.CENTER);
            channelBox.getChildren().add(buttonBox);
            
            button.getStyleClass().add("channelItem");
            
            button.setOnMouseClicked((e) -> {
                joinChannel(((Button) e.getSource()).getText());
            });
        }
    }

    public void joinChannel(String name) {
        this.communicator.send("JOIN " + name);
    }

}