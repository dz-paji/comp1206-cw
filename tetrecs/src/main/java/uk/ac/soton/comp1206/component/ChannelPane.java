package uk.ac.soton.comp1206.component;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import uk.ac.soton.comp1206.event.ChannelMsgListener;
import uk.ac.soton.comp1206.game.Multimedia;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class contains UI and logic of a Channel's Pane.
 */
public class ChannelPane extends GridPane {
    private static final Logger logger = LogManager.getLogger(ChannelPane.class);

    private final Text channelName = new Text();
    private final Text playerView = new Text();
    private final Text myName = new Text();
    private ChannelMsgListener listener;
    private final TextFlow msgArea = new TextFlow(new Text("Send /nick <Name> to change your nick name \n"));
    private final Multimedia soundPlayer = new Multimedia();
    private final TextField msgField;
    private final Button startGame;
    private Boolean isNameSetup = false;

    /**
     * Construct the Pane.
     */
    public ChannelPane() {
        this.getStyleClass().add("chan-gridpane");
        this.channelName.getStyleClass().add("chanpaneTitle");
        this.playerView.getStyleClass().add("playerBox");
        this.myName.getStyleClass().addAll("playerBox", "myname");
        this.add(channelName, 1, 0);
        this.add(myName, 1, 1);
        this.add(playerView, 1, 2, 1, 2);

        // Use a scrollpane to display messages.
        var msgPane = new ScrollPane(msgArea);
        msgPane.getStyleClass().add("scroller");
        this.add(msgPane, 1, 4);
        GridPane.setVgrow(msgPane, Priority.ALWAYS);

        msgField = new TextField();
        var msgBox = new HBox();
        var sendButton = new Button("Send");
        msgBox.getChildren().addAll(msgField, sendButton);
        HBox.setHgrow(msgField, Priority.ALWAYS);
        this.add(msgBox, 1, 6);

        // Start and leave button
        startGame = new Button("Start");
        Button leaveChannel = new Button("Leave");
        startGame.setVisible(false);

        this.add(leaveChannel, 1, 7);
        this.add(startGame, 2, 7);

        // Button interactions
        sendButton.setOnAction(this::sendMsgHandler);
        msgField.setOnKeyPressed(this::sendMsgKeyHandler);
        startGame.setOnAction(this::startGameHandler);
        leaveChannel.setOnAction(this::leaveChannelHandler);
    }

    /**
     * Handles when send message button is triggered
     *
     * @param e Mouse Click event
     */
    public void sendMsgHandler(ActionEvent e) {
        String msgToSend = msgField.getText();
        if (msgToSend.startsWith("/nick")) {
            this.listener.msgToSend("NICK " + msgToSend.split(" ")[1]);
        }

        this.listener.msgToSend("MSG " + msgField.getText());
        msgField.clear();

    }

    /**
     * Send message when enter key is pressed on input text field.
     *
     * @param e keyevent
     */
    public void sendMsgKeyHandler(KeyEvent e) {
        if (e.getCode() != KeyCode.ENTER) {
            return;
        }

        String msgToSend = msgField.getText();
        if (msgToSend.startsWith("/nick")) {
            this.listener.msgToSend("NICK " + msgToSend.split(" ")[1]);
        }

        this.listener.msgToSend("MSG " + msgField.getText());
        msgField.clear();

    }

    /**
     * Update the name of this channel
     *
     * @param name channel name
     */
    public void updateName(String name) {
        channelName.setText(name);
        startGame.setVisible(false);
    }

    /**
     * Update player in this game
     *
     * @param players All players
     */
    public void updatePlayer(String players) {
        logger.info("Updating player list");
        var playerArray = players.split(" ")[1].split("\n");

        // Initialise my name when receive USERS message for the first time.
        if (!this.isNameSetup) {
            this.myName.setText(playerArray[playerArray.length - 1]);
            this.isNameSetup = true;
        }
        String playerNameString = "";

        for (String name : playerArray) {

            // Omnit when current nickname is mine.
            if (name.equals(this.myName.getText())) {
            } else {
                playerNameString += name + " ";
            }
        }
        this.playerView.setText(playerNameString);
    }

    /**
     * Add a new message to Message box
     *
     * @param msg the new message
     */
    public void addMsg(String msg) {
        Text newMsg = new Text(msg.split("MSG ")[1].replace(":", ": ") + "\n");
        msgArea.getChildren().add(newMsg);
        soundPlayer.playAudio("message.wav");
    }

    /**
     * Listener to be called whenever a message need passing to communicator
     *
     * @param listener the listener
     */
    public void setChannelMsgListener(ChannelMsgListener listener) {
        this.listener = listener;
    }

    /**
     * Tell communicator start the game when host presses start button.
     *
     * @param e Action event
     */
    public void startGameHandler(ActionEvent e) {
        this.listener.msgToSend("START");
    }

    /**
     * Tell communicator leaving current channel when leave button is pressed
     *
     * @param e action event
     */
    public void leaveChannelHandler(ActionEvent e) {
        this.listener.msgToSend("PART");
        this.isNameSetup = false;
    }

    /**
     * When becoming host of this channel, show start button
     */
    public void setHost() {
        this.startGame.setVisible(true);
    }

    /**
     * Update players' nickname
     *
     * @param msg nickname got changed
     */
    public void msgNickHandler(String msg) {
        // If contains :, name got changed isn't mine.
        if (!msg.contains(":")) {
            this.myName.setText(msg.split(" ")[1]);
        } else {
            //
            playerView.getText().replaceAll(msg.split(" ")[1].split(":")[0],msg.split(" ")[1].split(":")[1]);
        }
    }

}
