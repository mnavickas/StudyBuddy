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


/**
 * Handle posting location data to database, off of the UI Thread
 * Created by Michael on 10/31/2016.
 */
class DBLocationManager extends AsyncTask<LocationWrapper,Void,Void>{

    /**
     * Execute the HTTP Request off of the UI Thread
     * @param params List of location Wrappers
     * @return Nothing
     */
    protected Void doInBackground(LocationWrapper... params){

        LocationWrapper loc = params[0];
        String username = loc.user.username;
        double lat = loc.lat;
        double lng = loc.lng;

        try{
            URL url = new URL("https://people.eecs.ku.edu/~mnavicka/Android/updateLocation.php");
            HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
            postData(url,httpcon,username,lat,lng);

            InputStream inputStream = httpcon.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
            Log.d("DBLocationManager","Read Data");
            Log.d("DBLocationManager","Already Read Data");
            bufferedReader.close();
            inputStream.close();
            httpcon.disconnect();

        }catch(IOException e){
            Log.e("DBLocationManager",e.getMessage());
        }
        return null;
    }

    /**
     * Execute the Request
     * @param url URL to request from
     * @param httpcon HTTPConnection Instance
     * @param user UserName data to post
     * @param lat Latitude data to post
     * @param lng Longitude Data to post.
     * @throws IOException
     */
    private void postData(URL url, final HttpURLConnection httpcon, String user, double lat, double lng) throws IOException
    {
        Log.d("DBLocationManager", "Start");
        Log.d("DBLocationManager", "URL " + url.toString());
        Log.d("DBLocationManager", "Open Connection");
        httpcon.setRequestMethod("POST");
        httpcon.setDoOutput(true);
        httpcon.setDoInput(true);
        OutputStream outputStream = httpcon.getOutputStream();
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
        Log.d("DBLocationManager", "BufferedWriter");
        String post_data = URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(user, "UTF-8")
                + "&" +
                URLEncoder.encode("lat", "UTF-8") + "=" + lat
                + "&" +
                URLEncoder.encode("lng", "UTF-8") + "=" + lng;
        bufferedWriter.write(post_data);
        bufferedWriter.flush();
        bufferedWriter.close();
        outputStream.close();
        Log.d("DBLocationManager","Posted Data: "+post_data);
    }

}
