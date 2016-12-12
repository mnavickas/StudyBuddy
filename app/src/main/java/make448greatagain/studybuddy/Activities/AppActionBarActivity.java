package make448greatagain.studybuddy.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import make448greatagain.studybuddy.FriendsListActivity;
import make448greatagain.studybuddy.MainActivity;
import make448greatagain.studybuddy.R;
import make448greatagain.studybuddy.UserManager;

/**
 * Created by Michael on 11/23/2016.
 *
 */
public class AppActionBarActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {

            case R.id.messaging:
                Intent myIntent = new Intent(this, FriendsListActivity.class);
                startActivityForResult(myIntent, 0);
                return true;

            case R.id.logout:
                UserManager.logout();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
