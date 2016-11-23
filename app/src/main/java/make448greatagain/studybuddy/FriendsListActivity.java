package make448greatagain.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.LinkedList;

public class FriendsListActivity extends AppCompatActivity {
    ListView listView;
    int listSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_item_click_event);
        User user = UserManager.getUser();

        listView = (ListView) findViewById(R.id.list);
        LinkedList<String> friendList = UserManager.getFriends();
        listSize = friendList.size();
        String[] values = friendList.toArray(new String[listSize]);
        Log.e(this.getClass().getSimpleName(), friendList.size()+"");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                for(int i = 0; i < listSize; i++){
                    if (position == i) {
                        Intent myIntent = new Intent(view.getContext(), ListItemActivity.class);
                        startActivityForResult(myIntent, 0);
                    }
                }
            }
        });
    }
}