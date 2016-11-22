package make448greatagain.studybuddy.Messaging;


/**
 * Created by Michael on 11/21/2016.
 *
 */
public class GroupMessage{

    public String board_to;
    public String user_from;
    public String msg;
    long msg_id;


    public GroupMessage(String subject, final String from, String msg)
    {
        this.user_from = from;
        this.board_to = subject;
        this.msg = msg;
        this.msg_id = hash();
    }
    private long hash()
    {
        final int prime = 31;
        long result = 1;

        result = result * prime + board_to.hashCode();
        result = result * prime + msg.hashCode();
        result = result * prime + user_from.hashCode();
        result = result * prime + System.nanoTime()/1000;

        return result;
    }


}
