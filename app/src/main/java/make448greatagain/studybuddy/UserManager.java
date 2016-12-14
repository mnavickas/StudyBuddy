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

import static android.R.attr.data;


/**
 * Keep track of active user
 * @author Michael
 */
public class UserManager {
    /**
     * Flag for if the user has been set
     */
    private static boolean isSet = false;

    /**
     * Current user
     */
    private static User sUser;

    /**
     * List of current users friends
     */
    private static LinkedList<String> friends;

    /**
     * Get Friends
     * @return list of current user's friends
     */
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

    /**
     * Logout the current user
     */
    public static void logout(){
        isSet = false;
        sUser = null;
        friends = null;
    }

    /**
     * Get User friends from database
     */
    public static void populateFriends(){

        try {
            friends = new friendQuery().execute(sUser).get();
        }catch(InterruptedException | ExecutionException e){
            Log.e("User Manager",e.getMessage());
        }
    }

    /**
     * Add a user friend and store on DB
     * @param friend Friend to add
     */
    public static void addFriend(String friend){
        if(!friends.contains(friend))
        {
            friends.add(friend);
            new addFriendTask().execute(friends);
        }

    }

    /**
     * Update User study info
     */
    public static void updateInfo(){
        new updateInfoTask().execute();
    }

    /**
     * Background task to update study info
     */
    private static class updateInfoTask extends AsyncTask<Void,Void,Void>{
        protected Void doInBackground(Void... params){

            try{
                pollDatabase();
            }catch(IOException e){
                //
            }

            return null;
        }
        private String pollDatabase() throws IOException
        {

            if(!ConnectivityReceiver.isDataconnected())
            {
                throw new IOException();
            }
            long time = System.currentTimeMillis();
            URL url = new URL("https://people.eecs.ku.edu/~mnavicka/Android/studyInfo.php");
            HttpURLConnection httpcon = NetworkingConnection.createNewConnection(url);
            postData(httpcon);
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
        private void postData(final HttpURLConnection httpcon) throws IOException
        {
            OutputStream outputStream = httpcon.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(sUser.username, "UTF-8")
                    + "&" +
                    URLEncoder.encode("course", "UTF-8") + "=" + URLEncoder.encode(sUser.courseName, "UTF-8")
                    + "&" +
                    URLEncoder.encode("subject", "UTF-8") + "=" + URLEncoder.encode(sUser.courseID, "UTF-8")
                    + "&" +
                    URLEncoder.encode("comments", "UTF-8") + "=" + URLEncoder.encode(sUser.comments, "UTF-8");
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
        }
    }
    /**
     * Background task to add a friend
     */
    private static class addFriendTask extends AsyncTask<LinkedList<String>,Void,Void>{

        @Override
        protected Void doInBackground(LinkedList<String>... params) {
            LinkedList<String> friends = params[0];
            int i = 0;
            String data = "";
            for(;i<friends.size()-1;i++){
                data+=friends.get(i);
                data+=",";
            }
            data+=friends.get(i);
            try{
                pollDatabase(data);
            }catch(IOException e){
                //
            }


            return null;
        }
        private String pollDatabase(String data) throws IOException
        {

            if(!ConnectivityReceiver.isDataconnected())
            {
                throw new IOException();
            }
            long time = System.currentTimeMillis();
            URL url = new URL("https://people.eecs.ku.edu/~mnavicka/Android/addUserFriends.php");
            HttpURLConnection httpcon = NetworkingConnection.createNewConnection(url);
            postData(httpcon,data);
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
        private void postData(final HttpURLConnection httpcon, String data) throws IOException
        {
            OutputStream outputStream = httpcon.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("friends", "UTF-8") + "=" + URLEncoder.encode(data, "UTF-8")
                    + "&" +
                    URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(sUser.username, "UTF-8");
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
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
