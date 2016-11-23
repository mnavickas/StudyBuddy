package make448greatagain.studybuddy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Allow User to input info about themselves.
 */
public class StudyInfo extends AppActionBarActivity {

    /**
     * Create the activity
     * @param savedInstanceState Android instance state
     * Auto Generated by Android Studio
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studyinfo);
    }

    /**
     * Jump to Map View on button click
     * @param view Current view object
     */
    public void goToMaps(View view)
    {
        UserManager.getUser().comments = ((EditText)findViewById(R.id.comments)).getText().toString();
        UserManager.getUser().courseID = ((EditText)findViewById(R.id.courseID)).getText().toString();
        UserManager.getUser().courseName = ((EditText)findViewById(R.id.courseName)).getText().toString();
        if(ConnectivityReceiver.isGpsconnected())
        {
            Intent myIntent = new Intent(StudyInfo.this, MapsActivity.class);
            startActivity(myIntent);
        }else{
            Toast myToast = Toast.makeText(this,R.string.NoGPS, Toast.LENGTH_SHORT);
            myToast.show();
        }

    }


}
