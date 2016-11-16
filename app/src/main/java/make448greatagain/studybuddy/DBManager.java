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

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Manager for General Database Access from Login Screen, off UI Thread
 * Created by Michael on 10/25/2016.
 */
class DBManager extends AsyncTask<Void, Void, Boolean>{
    /**
     * Error Codes From Connection
     */
    static final int SUCCESS = 0,USERNAME_ERROR = 1, PASSWORD_ERROR = 2,CONNECTION_ERROR = 3;
    /**
     * User information
     */
    private final String user,pass;

    /**
     * Create an instance of the manager with default user and password data1
     * @param user Username
     * @param pass Password
     */
    DBManager(final String user, final String pass)
    {
        this.user = user;
        this.pass = pass;
    }

    /**
     * Execute the request
     * @param url URL to request from
     * @param httpcon HTTPConnection Instance
     * @throws IOException
     */
    private void postData(URL url, final HttpURLConnection httpcon) throws IOException
    {

        Log.d("DBManager", "URL " + url.toString());
        httpcon.setRequestMethod("POST");
        httpcon.setDoOutput(true);
        httpcon.setDoInput(true);
        OutputStream outputStream = httpcon.getOutputStream();
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
        String post_data = URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(this.user, "UTF-8") + "&" +
                URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(this.pass, "UTF-8");
        bufferedWriter.write(post_data);
        bufferedWriter.flush();
        bufferedWriter.close();
        outputStream.close();

    }
    /**
     * Execute the HTTP Request off of the UI Thread
     * @param params Nothing
     * @return Nothing
     */
    protected Boolean doInBackground(Void... params)
    {
        try{
            if(!ConnectivityReceiver.isDataconnected())
            {
                return FALSE;
            }
            URL url = new URL("https://people.eecs.ku.edu/~mnavicka/Android/adduser.php");
            HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
            postData(url,httpcon);

            InputStream inputStream = httpcon.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
            bufferedReader.close();
            inputStream.close();
            httpcon.disconnect();
            return TRUE;

        }catch(IOException e){
            Log.d("DBManager",e.getMessage());
            return FALSE;

        }
    }

    /**
     * Check if the User exists on the database
     * @return true if exists
     * @throws IOException
     */
    Boolean checkUserExist() throws IOException
    {
        if(!ConnectivityReceiver.isDataconnected())
        {
            throw new IOException();
        }

        URL url = new URL("https://people.eecs.ku.edu/~mnavicka/Android/checkexists.php");
        HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
        postData(url,httpcon);

        InputStream inputStream = httpcon.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
        Log.d("DBManager","Read Data");
        String result = "";
        String line;
        while((line = bufferedReader.readLine())!= null)
        {
            result+=line;
        }
        bufferedReader.close();
        inputStream.close();
        httpcon.disconnect();
        Log.d("DBManager",result);
        return result.equals("Success");

    }

    /**
     * Attempt to verify user information for login
     * @return User Object if successful.
     * @throws IOException
     */
    User tryLogin() throws IOException
    {
        if(!ConnectivityReceiver.isDataconnected())
        {
            throw new IOException();
        }

        URL url = new URL("https://people.eecs.ku.edu/~mnavicka/Android/trylogin.php");
        HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
        postData(url,httpcon);

        InputStream inputStream = httpcon.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
        Log.d("DBManager","Read Data");
        String result = "";
        String line;
        while((line = bufferedReader.readLine())!= null)
        {
            result+=line;
        }
        bufferedReader.close();
        inputStream.close();
        httpcon.disconnect();
        Log.d("DBManager",result);
        if(result.equals("Success"))
        {
            return new User(0,user,pass);
        }
        else
        {
            return null;
        }
    }
}
