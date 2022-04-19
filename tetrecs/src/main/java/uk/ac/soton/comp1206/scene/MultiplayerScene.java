package uk.ac.soton.comp1206.scene;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import uk.ac.soton.comp1206.component.ChannelPane;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * This class contains UI, interaction and logic of a multiplayer lobby.
 */
public class MultiplayerScene extends BaseScene {
    private final Communicator communicator;
    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    private final Timer timer = new Timer();

    private final ScrollPane channelScrollPane = new ScrollPane();

    private final ChannelPane chanPane = new ChannelPane();

    private final VBox channelBox = new VBox();

    private final VBox erroMsgView = new VBox();

    private Timer pollPlaTimer = new Timer();

    /**
     * Initialise the scene
     *
     * @param gameWindow Game window
     */
    public MultiplayerScene(GameWindow gameWindow) {
        super(gameWindow);
        this.communicator = gameWindow.getCommunicator();
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

        this.chanPane.setChannelMsgListener(this.communicator::send);

        this.chanPane.visibleProperty().addListener((listener, oldValue, newValue) -> {
            // When chanPane is visible, polling player data
            if (newValue) {
                Platform.runLater(() -> {
                    TimerTask pollPlayer = new TimerTask() {

                        @Override
                        public void run() {
                            communicator.send("USERS");
                        }

                    };
                    pollPlaTimer = new Timer();
                    pollPlaTimer.schedule(pollPlayer, 10000, 15000);

                });
            } else if (!newValue) {
                pollPlaTimer.cancel();
            }
        });

        // Listen for ESC key press.
        gameWindow.getScene().setOnKeyPressed((event) -> quit());
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
        var createChanButton = new Button("Host game");
        var chanNameField = new TextField();
        // this.erroMsgView.getStyleClass().add("error-listview");

        joinPane.add(currGameText, 1, 0);
        joinPane.add(channelScrollPane, 1, 2);
        joinPane.add(createChanButton, 2, 3);
        joinPane.add(chanNameField, 1, 3);
        channelScrollPane.setContent(channelBox);
        channelScrollPane.getStyleClass().add("scroller");
        chanPane.setVisible(false);

        // Handles host new game interaction
        createChanButton.setOnMouseClicked((e) -> {
            this.communicator.send("CREATE " + chanNameField.getText());
        });

        mainPane.add(erroMsgView, 0, 1);
        mainPane.add(joinPane, 1, 1);
        mainPane.add(chanPane, 3, 1);

        // Setting up grid
        ColumnConstraints border = new ColumnConstraints();
        border.setPercentWidth(15);
        ColumnConstraints joinPaneConstraints = new ColumnConstraints();
        joinPaneConstraints.setPercentWidth(35);
        ColumnConstraints gap = new ColumnConstraints();
        gap.setPrefWidth(30);
        ColumnConstraints chanPaneConstraints = new ColumnConstraints();
        chanPaneConstraints.setPercentWidth(40);
        mainPane.getColumnConstraints().addAll(border, joinPaneConstraints, gap, chanPaneConstraints);

        GridPane.setVgrow(joinPane, Priority.ALWAYS);
        GridPane.setVgrow(chanPane, Priority.ALWAYS);
    }

    /**
     * Handler to deal this incoming messages
     *
     * @param msg message received
     */
    public void msgHandler(String msg) {
        switch (msg.substring(0, 4)) {
            default -> logger.info("Unhandled msg. Msg starts with {}", msg.substring(0, 4));
            case "CHAN" -> Platform.runLater(() -> {
                msgChannelHandler(msg);
            });
            case "JOIN" -> Platform.runLater(() -> {
                msgJoinHandler(msg);
            });
            case "USER" -> Platform.runLater(() -> {
                chanPane.updatePlayer(msg);
            });
            case "ERRO" -> Platform.runLater(() -> {
                msgErroHandler(msg);
            });
            case "MSG " -> Platform.runLater(() -> {
                chanMsgHandler(msg);
            });
            case "PART" -> Platform.runLater(() -> {
                this.chanPane.setVisible(false);
            });
            case "HOST" -> Platform.runLater(this.chanPane::setHost);
            case "NICK" -> Platform.runLater(() -> {
                this.chanPane.msgNickHandler(msg);
            });
            case "STAR" -> Platform.runLater(() -> {
                gameWindow.startMultiplayerGame();
            });
        }
    }

    /**
     * Handles JOIN message
     *
     * @param msg message
     */
    private void msgJoinHandler(String msg) {
        logger.info("Joining new channel");
        this.chanPane.updateName(msg.split(" ")[1]);
        this.chanPane.setVisible(true);
    }

    /**
     * Handle CHANNELS message. Update channels
     *
     * @param msg message
     */
    public void msgChannelHandler(String msg) {
        logger.info("Handling CHANNELS message.");
        channelBox.getChildren().clear();

        if (msg.split(" ").length < 2) {
            return;
        }

        // Refresh channel box.
        String[] channels = msg.split(" ")[1].split("\n");
        for (int i = 0; i < channels.length; i++) {
            var buttonBox = new HBox();
            var button = new Button(channels[i]);
            buttonBox.getChildren().add(button);
            buttonBox.setAlignment(Pos.CENTER);
            channelBox.getChildren().add(buttonBox);

            button.getStyleClass().add("channelItem");

            button.setOnMouseClicked((e) -> {
                joinChannel(((Button) e.getSource()).getText());
            });
        }
    }

    /**
     * Attempting to jon a channel
     *
     * @param name name of that channel
     */
    public void joinChannel(String name) {
        logger.info("Requesting joinning channel {}", name);
        this.communicator.send("JOIN " + name);
    }

    /**
     * Handles ERROR message
     *
     * @param msg message
     */
    public void msgErroHandler(String msg) {
        logger.info("Handeling error message.");

        // Get error content
        Text errorMsg = new Text(msg.split("ERROR ")[1]);
        Text error = new Text("Error!");
        error.getStyleClass().add("joinpaneTitle");
        errorMsg.getStyleClass().add("errorMsg");
        var errorBox = new VBox();
        errorBox.getChildren().addAll(error, errorMsg);
        errorBox.getStyleClass().add("errorMsgBox");
        erroMsgView.getChildren().add(errorBox);

        // Fade in the message then fade out and delete it.
        FadeTransition fadeError = new FadeTransition(Duration.millis(3000), errorBox);
        fadeError.setFromValue(0);
        fadeError.setToValue(1);
        fadeError.setAutoReverse(true);
        fadeError.setCycleCount(2);
        fadeError.play();

        fadeError.setOnFinished((e) -> {
            erroMsgView.getChildren().remove(errorBox);
        });
    }

    /**
     * Handles MSG messages. Pass it to channel pane to deal with.
     *
     * @param msg message.
     */
    public void chanMsgHandler(String msg) {
        this.chanPane.addMsg(msg);
    }

    public void quit() {
        this.communicator.send("QUIT");
        gameWindow.cleanup();
        gameWindow.startMenu();
        this.timer.cancel();
        this.pollPlaTimer.cancel();
    }

}