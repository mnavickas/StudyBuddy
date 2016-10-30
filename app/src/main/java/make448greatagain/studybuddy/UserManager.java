package make448greatagain.studybuddy;

/**
 * @author Michael
 * Keep track of active user
 */
public class UserManager {

    private static boolean isSet = false;
    private static User sUser;
    /**
    * Add the current user
    * @param user current User
    */
    public static void addUser(User user)
    {
        sUser=user;
        isSet = true;
    }
    
    /**
    * Remove the current user
    */
    public static void removeUser()
    {
        sUser = null;
        isSet = false;
    }
    
    /**
    * Get the current user
    * @return active user
    */
    public static User getUser()
    {
        if(isSet)
        {
            return sUser;
        }
        else
        {
            //TODO actually handle this.
            //for now, force a crash.
            Object o = null;
            o.toString();
            return null;
        }
    }
}
