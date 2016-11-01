package make448greatagain.studybuddy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //this is going to leak
        NearbyClients.getInstance().start();


    }

    public void goToLogin(View view)
    {
        Intent j = new Intent(view.getContext(), LoginActivity.class);
        startActivityForResult(j, 0);
    }

}
