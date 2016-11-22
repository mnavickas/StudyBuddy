package make448greatagain.studybuddy;

import android.location.Location;

/**
 * Datafield struct for information about the user
 * Created by Michael on 10/25/2016.
 */
class User {
    private long userId;
    String username;
    String password;
    String courseName;
    String courseID;
    String comments;
    Location location;

    User(long userId, String username, String password){
        this.userId=userId;
        this.username=username;
        this.password=password;
    }

}