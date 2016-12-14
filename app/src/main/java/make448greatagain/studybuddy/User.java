package make448greatagain.studybuddy;

import android.location.Location;

/**
 * Datafield struct for information about the user
 * Created by Michael on 10/25/2016.
 */
public class User {
    /** DataField */
    private long userId;
    /** DataField */
    public String username;
    /** DataField */
    String password;
    /** DataField */
    String courseName;
    /** DataField */
    String courseID;
    /** DataField */
    String comments;
    /** DataField */
    Location location;

    User(long userId, String username, String password){
        this.userId=userId;
        this.username=username;
        this.password=password;
    }

}