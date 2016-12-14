package make448greatagain.studybuddy.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import make448greatagain.studybuddy.UserManager;

/**
 * Created by Michael on 11/24/2016.
 * App activity before user exists
 */
public class AppPrefaceActivity extends AppCompatActivity {
    /**
     * On creation
     * @param bundle saved bundle instance
     */
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        UserManager.logout();
    }
}
