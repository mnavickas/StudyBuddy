package make448greatagain.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import make448greatagain.studybuddy.Activities.AppActionBarActivity;

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
     * Logout the current user if we go back.
     */
    public void onBackPressed(){
        UserManager.logout();
        super.onBackPressed();
    }

    /**
     * Jump to Map View on button click
     * @param view Current view object
     */
    public void goToMaps(View view)
    {
        UserManager.getUser().comments = ((EditText)findViewById(R.id.comments)).getText().toString().replace("&","").replace(",","");
        UserManager.getUser().courseID = ((EditText)findViewById(R.id.courseID)).getText().toString().replace("&","").replace(",","");
        UserManager.getUser().courseName = ((EditText)findViewById(R.id.courseName)).getText().toString().replace("&","").replace(",","");
        UserManager.updateInfo();
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
