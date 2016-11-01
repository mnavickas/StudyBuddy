package make448greatagain.studybuddy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class StudyInfo extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studyInfo);
    }
    public void goToMaps(View view)
    {
        UserManager.getUser().comments = ((EditText)findViewById(R.id.comments)).getText().toString();
        UserManager.getUser().courseID = ((EditText)findViewById(R.id.courseID)).getText().toString();
        UserManager.getUser().courseName = ((EditText)findViewById(R.id.courseName)).getText().toString();
        Intent myIntent = new Intent(StudyInfo.this, MapsActivity.class);
        startActivity(myIntent);
    }


}
