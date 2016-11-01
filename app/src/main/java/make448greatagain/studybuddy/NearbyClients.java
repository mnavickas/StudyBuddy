package make448greatagain.studybuddy;

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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

/**
 * Created by Michael on 10/31/2016.
 */
public class NearbyClients extends Thread {

    static LinkedList<LocationObject> locations;
    volatile boolean running = false;

    private static NearbyClients sInstance = null;

    public static NearbyClients getInstance()
    {
       if(sInstance == null)
       {
           sInstance = new NearbyClients();
       }
        return sInstance;
    }
    private NearbyClients(){
        locations = new LinkedList<>();
    };

    public void start()
    {
        if(running) return;

        running = true;

        super.start();
    }
    public void run()
    {
        while(running)
        {
            //poll database and update list
            String result;
            try{
                result = pollDatabase();
                synchronized (locations)
                {
                    //clear out the old list
                    locations.clear();
                }
            }catch(IOException e){
                result = null;
            }
            if(result != null) {
                //parse result string
                String[] results = result.split("&");
                for (int i = 0; i < results.length; i++) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    sdf.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));

                    String args[] = results[i].split(",");
                    java.util.Date date;
                    try {
                        date = sdf.parse(args[3]);
                    } catch (Exception e) {
                        date = null;
                    }

                    TimeZone timeZone = TimeZone.getTimeZone("UTC");
                    Calendar calendar = Calendar.getInstance(timeZone);
                    java.util.Date current = calendar.getTime();

                    //Accept only data that is less than x Minutes old;
                    int minutes = 1000;
                    if (date != null && Math.abs(date.getTime() - current.getTime()) < 1000 * 60 * minutes) {
                        Log.d("Client", "Lat=" + args[1] + " Lng=" + args[2]);
                        LocationObject locationObject = new LocationObject(args[0], Double.parseDouble(args[1]), Double.parseDouble(args[2]));
                        synchronized (locations) {
                            locations.add(locationObject);
                        }
                    }


                }
            }
            try
            {
                //Wait 5 seconds and do it again.
                sleep(5000);
            }
            catch(InterruptedException e)
            {

            }
        }
    }

    public String pollDatabase() throws IOException
    {
            URL url = new URL("https://people.eecs.ku.edu/~mnavicka/Android/getAllLocations.php");
            HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
            postData(url,httpcon);

            InputStream inputStream = httpcon.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
            String result = "";
            String line = "";
            while((line = bufferedReader.readLine())!= null)
            {
                result+=line;
            }
            bufferedReader.close();
            inputStream.close();
            httpcon.disconnect();
            Log.d("CONNECTION",result);
            return result;
    }

    private void postData(URL url, final HttpURLConnection httpcon) throws IOException
    {
            Log.d("HTTP", "URL " + url.toString());
            httpcon.setRequestMethod("POST");
            httpcon.setDoOutput(true);
            httpcon.setDoInput(true);
            OutputStream outputStream = httpcon.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8");
            Log.d("HTTP","Post data= "+post_data);
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
    }

}
