package make448greatagain.studybuddy.Messaging;


/**
 * Created by Michael on 11/21/2016.
 *
 */
public class PrivateMessage {

    public String user_to, user_from;
    public String msg;
    long msg_id;


    public PrivateMessage(final String to, final String from, String msg)
    {
        this.user_from = from;
        this.user_to = to;
        this.msg = msg;
        this.msg_id = hash();
    }
    public PrivateMessage(final String to, final String from, String msg, long hash)
    {
        this.user_from = from;
        this.user_to = to;
        this.msg = msg;
        this.msg_id = hash;
    }
    private long hash()
    {
        final int prime = 31;
        long result = 1;

        result = result * prime + user_to.hashCode();
        result = result * prime + msg.hashCode();
        result = result * prime + user_from.hashCode();
        result = result * prime + System.nanoTime()/1000;

        return result;
    }


}
