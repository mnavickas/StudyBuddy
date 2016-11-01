package make448greatagain.studybuddy;

/**
 * Created by Michael on 10/25/2016.
 *
 */
public class User {
    public long userId;
    public String username;
    public String password;
    public String courseName;
    public String courseID;
    public String comments;

    public User(long userId, String username, String password){
        this.userId=userId;
        this.username=username;
        this.password=password;
        this.courseName=courseName;
        this.courseID=courseID;
        this.comments=comments;
    }

}