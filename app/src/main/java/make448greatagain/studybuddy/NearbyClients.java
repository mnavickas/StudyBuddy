package make448greatagain.studybuddy;

import android.support.annotation.NonNull;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import static android.os.Build.VERSION_CODES.M;
import static com.google.android.gms.internal.zznu.is;
import static com.google.android.gms.internal.zznu.it;

/**
 * Background Process for polling DB to get Client Locations.
 * Created by Michael on 10/31/2016.
 */
class NearbyClients extends Thread {
    private static final int timeToRunMS = 5000;

    final Object timerMutex = new Object();

    /**
     * Lists of all client locations
     */
    static LinkedList<LocationObject> locations, expiredLocations;

    /**
     * Thread Status
     */
    private volatile boolean running = false,suspended = false;

    /**
     * Singleton instance
     */
    private static NearbyClients sInstance = null;

    /**
     * Single instance
     * @return this Instance
     */
    static NearbyClients getInstance()
    {
       if(sInstance == null)
       {
           sInstance = new NearbyClients();
       }
        return sInstance;
    }

    /**
     * Create an Instance
     */
    private NearbyClients(){
        locations = new LinkedList<>();
        expiredLocations = new LinkedList<>();
    }

    /**
     * Wrapper to start Thread
     */
    void startThread(){
        this.start();
    }


    /**
     *  Start thread
     */
    public void start()
    {
        if(running) return;
        running = true;
        super.start();
    }

    /**
     * Run thread until suspended
     */
    public void run()
    {
        while(running)
        {
            //poll database and update list
            String result = null;
            if( ConnectivityReceiver.isDataconnected() )
            {
                try{
                    result = pollDatabase();
                    //clear out the old lists
                }catch(IOException e){
                    result = null;
                }
            }

            if(result != null) {
                LinkedList<LocationObject> tempLocations = new LinkedList<>();
                LinkedList<LocationObject> tempExpiredLocations = new LinkedList<>();

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
                    int minutes = 100;
                    long age = Long.MAX_VALUE;
                    if(date != null)
                    {
                        age = Math.abs(date.getTime() - current.getTime());
                    }
                    long ageMinutes = age/(60*1000);
                    long ageSeconds = age%(60*1000);


                    if (date != null && age < 1000 * 60 * minutes) {
                        Log.d(this.getClass().getSimpleName(), "Updated: User="+args[0]+" Lat=" + args[1] + " Lng=" + args[2] +" Age="+ageMinutes+":"+ageSeconds);
                        LocationObject locationObject = new LocationObject(args[0], Double.parseDouble(args[1]), Double.parseDouble(args[2]));
                            tempLocations.add(locationObject);
                    } else if (date != null) {
                        Log.d(this.getClass().getSimpleName(),"Expired: User="+args[0]+" Lat=" + args[1] + " Lng=" + args[2] +" Age="+ageMinutes+":"+ageSeconds);
                        LocationObject locationObject = new LocationObject(args[0], Double.parseDouble(args[1]), Double.parseDouble(args[2]));
                            tempExpiredLocations.add(locationObject);
                    }
                }
                //if there is a change
                if( isChanged(tempLocations,tempExpiredLocations) )
                {
                    locations = tempLocations;
                    expiredLocations = tempExpiredLocations;
                    synchronized (timerMutex)
                    {
                        Log.e(this.getClass().getSimpleName(),"Retriggering");
                        timerMutex.notify();
                    }
                }
                else
                {
                    Log.e(this.getClass().getSimpleName(),"Not Retriggering");
                }
            }
            try
            {
                //Wait and do it again.
                sleep(timeToRunMS);
            }
            catch(InterruptedException e)
            {
                Log.e("NearbyClients",e.getMessage());
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private boolean isChanged( LinkedList<LocationObject> tempNorm,
                               LinkedList<LocationObject> tempExp)
    {
        final double latEpsilon = 0.000000;
        final double lngEpsilon = 0.0000000;
        //One of them has changed in size, retrigger
        if(locations.size() != tempNorm.size() || expiredLocations.size() != tempExp.size())
        {
            return true;
        }
        else
        {
            //If all of the new locations have an exact match, no change.
            for(LocationObject locNew: tempNorm)
            {

                if(( UserManager.getUser() != null &&
                         UserManager.getUser().username.equals(locNew.user)))
                {
                    continue;
                }

                boolean noMatch = true;
                for(LocationObject locOld: locations)
                {

                    if (
                        locOld.user.equals(locNew.user) &&
                        Math.abs(locOld.lng - locNew.lng) <= lngEpsilon &&
                        Math.abs(locOld.lat - locNew.lat) <= latEpsilon

                        )
                    {
                        noMatch = false;
                        break;
                    }
                }
                if( noMatch )
                {
                    return true;
                }


            }
            for(LocationObject locNew: tempExp)
            {
                boolean noMatch = true;
                for(LocationObject locOld: expiredLocations)
                {
                    if  (
                         locOld.user.equals(locNew.user) &&
                         Math.abs(locOld.lng - locNew.lng) <= lngEpsilon&&
                         Math.abs(locOld.lat - locNew.lat) <= latEpsilon
                        )
                    {
                        noMatch = false;
                        break;
                    }
                }
                if( noMatch )
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get list of all clients
     * @return String of nearby client results from DB
     * @throws IOException
     */
    private String pollDatabase() throws IOException
    {

        if(!ConnectivityReceiver.isDataconnected())
        {
            throw new IOException();
        }
        long time = System.currentTimeMillis();
        URL url = new URL("https://people.eecs.ku.edu/~mnavicka/Android/getAllLocations.php");
        HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
        postData(url,httpcon);

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
        return result;
    }

    /**
     * Execute the request
     * @param url URL to request from
     * @param httpcon HTTPConnection Instance
     * @throws IOException
     */
    private void postData(URL url, final HttpURLConnection httpcon) throws IOException
    {
            Log.d("HTTP", "URL " + url.toString());
            httpcon.setRequestMethod("POST");
            httpcon.setDoOutput(true);
            httpcon.setDoInput(true);
            OutputStream outputStream = httpcon.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = "";
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
    }
}
