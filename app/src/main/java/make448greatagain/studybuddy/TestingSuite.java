package make448greatagain.studybuddy;

import android.os.AsyncTask;

/**
 * Created by Michael on 12/13/2016.
 */

public class TestingSuite extends AsyncTask<Void,Void,Void> {

    public static final String TEST_USER_NAME ="TestingDummy";
    public static final String TEST_PASSWORD = "TestingPassword";
    @Override
    protected Void doInBackground(Void... params) {
        return null;
    }

    private boolean testLogin(){
        return true;
    }
    private boolean testSendReceiveMessage(){
        return true;
    }
    private boolean updateLocation(){
        return true;
    }
}
