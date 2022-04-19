package uk.ac.soton.comp1206.component;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import uk.ac.soton.comp1206.event.ChannelMsgListener;
import uk.ac.soton.comp1206.game.Multimedia;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChannelPane extends GridPane {
    private static final Logger logger = LogManager.getLogger(ChannelPane.class);

    private final Text channelName = new Text();
    private final Text playerView = new Text();
    private final Text myName = new Text();
    private ChannelMsgListener listener;
    private TextFlow msgArea = new TextFlow(new Text("Send /nick <Name> to change your nick name \n"));
    private Multimedia soundPlayer = new Multimedia();
    private TextField msgField;
    private Button startGame;
    private Boolean isNameSetup = false;

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

    public void sendMsgHandler(Event e) {
        String msgToSend = msgField.getText();
        if (msgToSend.startsWith("/nick")) {
            this.listener.msgToSend("NICK " + msgToSend.split(" ")[1]);
            ;
        }

        this.listener.msgToSend("MSG " + msgField.getText());
        msgField.clear();

    }

    public void sendMsgKeyHandler(KeyEvent e) {
        if (e.getCode() != KeyCode.ENTER) {
            return;
        }

        String msgToSend = msgField.getText();
        if (msgToSend.startsWith("/nick")) {
            this.listener.msgToSend("NICK " + msgToSend.split(" ")[1]);
            ;
        }

        this.listener.msgToSend("MSG " + msgField.getText());
        msgField.clear();

    }

    public void updateName(String name) {
        channelName.setText(name);
    }

    public void updatePlayer(String players) {
        logger.info("Updating player list");
        var playerArray = players.split(" ")[1].split("\n");
        if (this.isNameSetup == false) {
            this.myName.setText(playerArray[playerArray.length - 1]);
            this.isNameSetup = true;
        }
        String playerNameString = new String();

        for (int i = 0; i < playerArray.length; i++) {
            
            if (playerArray[i].equals(this.myName.getText())) {
                continue;
            } else {
                playerNameString += playerArray[i] + " ";
            }
        }
        this.playerView.setText(playerNameString);
    }

    public void addMsg(String msg) {
        Text newMsg = new Text(msg.split("MSG ")[1].replace(":", ": ") + "\n");
        msgArea.getChildren().add(newMsg);
        soundPlayer.playAudio("message.wav");
    }

    public void setChannelMsgListener(ChannelMsgListener listener) {
        this.listener = listener;
    }

    public void startGameHandler(ActionEvent e) {
        this.listener.msgToSend("START");
    }

    public void leaveChannelHandler(ActionEvent e) {
        this.listener.msgToSend("PART");
        this.isNameSetup = false;
    }

    public void setHost() {
        this.startGame.setVisible(true);
    }

}
