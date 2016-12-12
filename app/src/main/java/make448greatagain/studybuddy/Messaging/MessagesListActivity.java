package make448greatagain.studybuddy.Messaging;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import make448greatagain.studybuddy.Activities.AppViewMessageActivity;
import make448greatagain.studybuddy.R;
import make448greatagain.studybuddy.UserManager;

public class MessagesListActivity extends AppViewMessageActivity{

    ListView listView;
    String otherUser;
    String user;
    ArrayAdapter<String> adapter;
    Timer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messagelist);
        listView = (ListView) findViewById(R.id.msglist);
        Intent myIntent = getIntent();
        otherUser = myIntent.getStringExtra("otherUser");
        user = myIntent.getStringExtra("user");

        setTitle("Messaging: " + otherUser);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (adapter != null && MessageHandler.userMessages.size() > 0) {
                            adapter.clear();

                            LinkedList<MessageData> results = MessageHandler.userMessages;
                            LinkedList<String> resultString = new LinkedList<>();
                            for(int i = 0; i < results.size(); i++){
                                if( results.get(i).to.equals(otherUser) ||  results.get(i).from.equals(otherUser) )
                                {
                                    resultString.add(results.get(i).msg);
                                }
                            }
                            adapter.addAll(resultString.toArray(new String[resultString.size()]));

                            adapter.notifyDataSetChanged();
                            listView.invalidateViews();
                        }
                    }
                });
            }
        };

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadUI();
            }
        });

        timer = new Timer(true);
        timer.schedule(task, 3000, 1000);
    }
    public void onBackPressed(){
     timer.cancel();
        timer.purge();
        super.onBackPressed();
    }

    private void loadUI()
    {
        LinkedList<String> resultString;
        synchronized (MessageHandler.messagesMutex) {
            LinkedList<MessageData> results = MessageHandler.userMessages;
            resultString = new LinkedList<>();
            for (int i = 0; i < results.size(); i++) {
                if (results.get(i).to.equals(otherUser) || results.get(i).from.equals(otherUser)) {
                    resultString.add(results.get(i).msg);
                }
            }
        }

        if(adapter != null){
            adapter.clear();
            adapter.addAll(resultString.toArray(new String[resultString.size()]));
        }else{
            ArrayList<String> lst = new ArrayList<>(resultString);
            adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, lst);
            listView.setAdapter(adapter);
        }

    }
}