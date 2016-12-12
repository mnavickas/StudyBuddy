package make448greatagain.studybuddy.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import make448greatagain.studybuddy.FriendsListActivity;
import make448greatagain.studybuddy.MainActivity;
import make448greatagain.studybuddy.Messaging.PopupMessageCreator;
import make448greatagain.studybuddy.R;
import make448greatagain.studybuddy.UserManager;

/**
 * Created by Michael on 12/5/2016.
 */

public class AppViewMessageActivity extends AppCompatActivity {
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_msg_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {

            case R.id.sendMessage:
                Intent myIntent = getIntent();
                String otherUser = myIntent.getStringExtra("otherUser");
                PopupMessageCreator.create(this,otherUser);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
