package make448greatagain.studybuddy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.LinkedList;

import make448greatagain.studybuddy.Messaging.MessagesListActivity;

/**
 * Activity to display the Contact list
 */
public class FriendsListActivity extends AppCompatActivity {
    /**
     * ListView that will contain the friends
     */
    ListView listView;
    /**
     * Size of the list
     */
    int listSize;
    /**
     * Array of friend names
     */
    String[] friends;
    /**
     * This cast to context
     */
    Context context;

    /**
     * On create, populate friends list
     * @param savedInstanceState Bundle representing saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        setContentView(R.layout.friendslist);
        final User user = UserManager.getUser();

        listView = (ListView) findViewById(R.id.list);
        LinkedList<String> friendList = UserManager.getFriends();
        listSize = friendList.size();
        friends = friendList.toArray(new String[listSize]);
        Log.e(this.getClass().getSimpleName(), friendList.size()+"");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, friends);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                for(int i = 0; i < listSize; i++){
                    if (position == i) {
                        //PopupMessageCreator.create(context,friends[i]);
                        Intent myIntent = new Intent(view.getContext(), MessagesListActivity.class);
                        myIntent.putExtra("otherUser",friends[i]);
                        myIntent.putExtra("user",user.username);
                        startActivityForResult(myIntent, 0);
                    }
                }
            }
        });
    }
}