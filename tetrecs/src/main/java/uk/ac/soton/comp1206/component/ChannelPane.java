package uk.ac.soton.comp1206.component;

import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
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
    private ScrollPane msgArea;
    private Multimedia soundPlayer = new Multimedia();

    public ChannelPane() {
        this.getStyleClass().add("chan-gridpane");
        this.channelName.getStyleClass().add("chanpaneTitle");
        this.playerView.getStyleClass().add("playerBox");
        this.myName.getStyleClass().addAll("playerBox", "myname");
        this.add(channelName, 1, 0);
        this.add(myName, 1,1);
        this.add(playerView, 1, 2, 1, 2);

        // Use a scrollpane to display messages.
        msgArea = new ScrollPane();
        msgArea.getStyleClass().add("scroller");
        this.add(msgArea, 1, 4, 1, 2);

        var msgField = new TextField();
        var msgBox = new HBox();
        var sendButton = new Button("Send");
        msgBox.getChildren().addAll(msgField, sendButton);
        HBox.setHgrow(msgField, Priority.ALWAYS);
        this.add(msgBox, 1, 5);

        // Send message interaction
        sendButton.setOnAction((e) -> {
            this.listener.msgToSend(msgField.getText());
        });

        msgField.setOnKeyPressed((e) -> {
            if (e.getCode() == KeyCode.ENTER) {
                this.listener.msgToSend(msgField.getText());
            }
        });
    }

    public void updateName(String name) {
        channelName.setText(name);
    }

    public void updatePlayer(String players) {
        logger.info("Updating player list");
        players = players.split(" ")[1];
        String[] player = players.replaceAll("\n", ",").split(",");
        this.myName.setText(player[0]);
        String playerNameString = new String();

        for (int i = 1; i < player.length - 1; i ++) {
            playerNameString += player[i];
        }
        this.playerView.setText(playerNameString);

    }

    public void addMsg(String msg) {
        Text newMsg = new Text(msg.split("MSG ")[1].replace(":", ": "));
        msgArea.setContent(newMsg);
        soundPlayer.playAudio("message.wav");
    }

    public void setChannelMsgListener(ChannelMsgListener listener) {
        this.listener = listener;
    }
}
