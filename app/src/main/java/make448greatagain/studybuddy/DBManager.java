package make448greatagain.studybuddy;

import android.content.Context;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Created by Michael on 10/25/2016.
 */

public class DBManager extends AsyncTask<Void, Void, Void>{
    public static final int SUCCESS = 0;
    public static final int PASSWORD_ERROR = 2;
    public static final int USERNAME_ERROR = 1;

    final String user;
    final String pass;
    public DBManager(final String user, final String pass)
    {
        this.user = user;
        this.pass = pass;
    }

    private void postData(URL url, final HttpURLConnection httpcon)
    {
        try {
            Log.d("CONNECTION", "Start");
            Log.d("CONNECTION", "URL " + url.toString());
            Log.d("CONNECTION", "Open Connection");
            httpcon.setRequestMethod("POST");
            httpcon.setDoOutput(true);
            httpcon.setDoInput(true);
            OutputStream outputStream = httpcon.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            Log.d("CONNECTION", "BufferedWriter");
            String post_data = URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(this.user, "UTF-8") + "&" +
                    URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(this.pass, "UTF-8");
            Log.d("CONNECTION", post_data);
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            Log.d("CONNECTION", "Posted Data");
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    protected Void doInBackground(Void... params)
    {
        try{
            URL url = new URL("https://people.eecs.ku.edu/~mnavicka/Android/adduser.php");
            HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
            postData(url,httpcon);

            InputStream inputStream = httpcon.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
            Log.d("CONNECTION","Read Data");
            Log.d("CONNECTION","Already Read Data");
            bufferedReader.close();
            inputStream.close();
            httpcon.disconnect();

        }catch(IOException e){
            e.printStackTrace();
            Log.d("CONNECTION",e.getMessage());

        }
        return null;
    }

    public Boolean checkUserExist(){

        try{
            URL url = new URL("https://people.eecs.ku.edu/~mnavicka/Android/checkexists.php");
            HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
            postData(url,httpcon);

            InputStream inputStream = httpcon.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
            Log.d("CONNECTION","Read Data");
            String result = "";
            String line = "";
            while((line = bufferedReader.readLine())!= null)
            {
                result+=line;
            }
            Log.d("CONNECTION","Already Read Data");
            bufferedReader.close();
            inputStream.close();
            httpcon.disconnect();
            Log.d("CONNECTION",result);
            return result.equals("Success");
        }catch(IOException e){
            e.printStackTrace();
            Log.d("CONNECTION",e.getMessage());
            return FALSE;
        }
    }

    public User tryLogin()
    {

        try{
            URL url = new URL("https://people.eecs.ku.edu/~mnavicka/Android/trylogin.php");
            HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
            postData(url,httpcon);

            InputStream inputStream = httpcon.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
            Log.d("CONNECTION","Read Data");
            String result = "";
            String line = "";
            while((line = bufferedReader.readLine())!= null)
            {
                result+=line;
            }
            Log.d("CONNECTION","Already Read Data");
            bufferedReader.close();
            inputStream.close();
            httpcon.disconnect();
            Log.d("CONNECTION",result);
            if(result.equals("Success"))
            {
                return new User(0,user,pass);
            }
            else
            {
                return null;
            }
        }catch(IOException e){
            e.printStackTrace();
            Log.d("CONNECTION",e.getMessage());
            return null;
        }
    }
}
