package uk.ac.soton.comp1206.event;

/**
 * The Channel Message Listener handles the process of sending a channel message.
 */
public interface ChannelMsgListener {

    /**
     * Handles user click send button or press ENTER key
     * @param msg
     */
    public void msgToSend(String msg);
    
}
