package make448greatagain.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import make448greatagain.studybuddy.Activities.AppPrefaceActivity;
import make448greatagain.studybuddy.Activities.AppTestingActivity;


public class MainActivity extends AppTestingActivity {

    /**
     * Create the activity
     * @param savedInstanceState Android instance state
     * Auto Generated by Android Studio
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    /**
     * Jump to Login View on button click
     * @param view Current view object
     */
    public void goToLogin(View view)
    {
        Intent j = new Intent(view.getContext(), LoginActivity.class);
        startActivityForResult(j, 0);
    }

}
