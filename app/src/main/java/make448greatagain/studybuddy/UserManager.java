package make448greatagain.studybuddy;


import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;


/**
 * Keep track of active user
 * @author Michael
 */
public class UserManager {

    private static boolean isSet = false;

    private static User sUser;

    private static LinkedList<String> friends;

    static LinkedList<String> getFriends(){
        return friends;
    }

    /**
     * Add the current user
     * @param user current User
     */
    static void addUser(User user) throws NewUserBeforeRemoveException
    {
        if(isSet){
            throw new NewUserBeforeRemoveException();
        }
        sUser = user;
        isSet = true;
        populateFriends();
    }
    
    /**
    * Remove the current user
    */
    public static void removeUser()
    {
        friends = null;
        sUser = null;
        isSet = false;
    }
    
    /**
    * Get the current user
    * @return active user
    */
    public static User getUser()
    {
        if(isSet)
        {
            return sUser;
        }
        else
        {
            return null;
        }
    }
    public static void logout(){
        isSet = false;
        sUser = null;
        friends = null;
    }
    public static void populateFriends(){

        try {
            friends = new friendQuery().execute(sUser).get();
        }catch(InterruptedException | ExecutionException e){
            Log.e("User Manager",e.getMessage());
        }
    }
    private static class friendQuery extends AsyncTask<User, Void, LinkedList<String>>{

        @Override
        protected LinkedList<String> doInBackground(User... params) {
            User user = params[0];
            String result;
            try{
                result = pollDatabase(user);
            }catch(IOException e){
                result = "";
                Log.e(this.getClass().getSimpleName(), e.getMessage());
            }
            String[] friends = result.split(",");

            return new LinkedList<>(Arrays.asList(friends));
        }
        /**
         * Get list of all clients
         * @return String of nearby client results from DB
         * @throws IOException
         */
        private String pollDatabase(User user) throws IOException
        {

            if(!ConnectivityReceiver.isDataconnected())
            {
                throw new IOException();
            }
            long time = System.currentTimeMillis();
            URL url = new URL("https://people.eecs.ku.edu/~mnavicka/Android/getUserFriends.php");
            HttpURLConnection httpcon = NetworkingConnection.createNewConnection(url);
            postData(httpcon,user);
            InputStream inputStream = httpcon.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
            String result = "";
            String line;
            while((line = bufferedReader.readLine())!= null)
            {
                result+=line;
            }
            bufferedReader.close();
            inputStream.close();
            httpcon.disconnect();
            time = System.currentTimeMillis()-time;
            Log.e(this.getClass().getSimpleName(),"Execution Time: "+time);
            Log.e(this.getClass().getSimpleName(),result);
            return result;
        }

        /**
         * Execute the request
         * @param httpcon HTTPConnection Instance
         * @throws IOException
         */
        private void postData(final HttpURLConnection httpcon, User user) throws IOException
        {
            OutputStream outputStream = httpcon.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(user.username, "UTF-8");
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
        }
    }

}
