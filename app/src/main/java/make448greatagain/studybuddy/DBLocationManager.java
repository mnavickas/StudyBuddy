package make448greatagain.studybuddy;

import android.location.Location;
import android.os.AsyncTask;
import android.text.format.Time;
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
import java.util.Date;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Created by Michael on 10/31/2016.
 */

public class DBLocationManager extends AsyncTask<LocationWrapper,Void,Void>{

    protected Void doInBackground(LocationWrapper... params){

        Log.e("ALT","ONE");
        LocationWrapper loc = params[0];
        String username = loc.user.username;
        double lat = loc.lat;
        double lng = loc.lng;
        Log.e("ALT","TWO");

        try{
            URL url = new URL("https://people.eecs.ku.edu/~mnavicka/Android/updateLocation.php");
            HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
            postData(url,httpcon,username,lat,lng);

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
    private void postData(URL url, final HttpURLConnection httpcon, String user, double lat, double lng) throws IOException
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
            String post_data = URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(user, "UTF-8")
                    + "&" +
                    URLEncoder.encode("lat", "UTF-8") + "=" + lat
                    + "&" +
                    URLEncoder.encode("lng", "UTF-8") + "=" + lng;
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            Log.d("CONNECTION", "Posted Data");
            Log.d("Location",post_data);
        }catch(IOException e){
            throw e;
        }
    }

}
