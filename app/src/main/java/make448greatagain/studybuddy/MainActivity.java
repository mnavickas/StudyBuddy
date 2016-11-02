package make448greatagain.studybuddy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    /**
     * Create the activity
     * @param savedInstanceState Android instance state
     * Auto Generated by Android Studio
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NearbyClients.getInstance().startThread();

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
