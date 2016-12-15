package make448greatagain.studybuddy.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import make448greatagain.studybuddy.FriendsListActivity;
import make448greatagain.studybuddy.MainActivity;
import make448greatagain.studybuddy.R;
import make448greatagain.studybuddy.TestingSuite;
import make448greatagain.studybuddy.UserManager;

/**
 * Created by Michael on 12/14/2016.
 */

public class AppTestingActivity extends AppPrefaceActivity {

    private boolean run = true;
    Context context;
    /**
     * On creation
     * @param menu Menu to inflate
     * @return Success Status
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.testing_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        context = this;
    }

    /**
     * Handle item clicked
     * @param item Menu Item that was pressed
     * @return success status
     */
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {

            case R.id.test:
                if(run){
                    run = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new TestingSuite().execute(context);
                        }
                    });
                }


                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
