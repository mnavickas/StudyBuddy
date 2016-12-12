package make448greatagain.studybuddy.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import make448greatagain.studybuddy.UserManager;

/**
 * Created by Michael on 11/24/2016.
 */

public class AppPrefaceActivity extends AppCompatActivity {

    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        UserManager.logout();
    }
}
