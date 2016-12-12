package make448greatagain.studybuddy.Messaging;

/**
 * Created by Michael on 12/7/2016.
 */

public class MessageData{
    String time;
    String msg;
    String to;
    String from;

    public MessageData(String time, String msg, String to, String from) {
        this.time = time;
        this.msg = msg;
        this.to = to;
        this.from = from;
    }
}