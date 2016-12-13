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
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


/**
 * Background Process for polling DB to get Client Locations.
 * Created by Michael on 10/31/2016.
 */
class NearbyClients extends Thread {
    private static final int timeToRunMS = 5000;

    //Accept only data that is less than x Minutes old;
    private final int minutes = 100;

    final Object timerMutex = new Object();

    /**
     * Lists of all client locations
     */
    static LinkedList<LocationObject> locations, expiredLocations;

    /**
     * Thread Status
     */
    private volatile boolean running = false;

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
    public static void subscribeServices(){
        getInstance().startThread();
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

                for (String param: results) {
                    //[User, lat, long, time,course,subject,comments]
                    String args[] = param.split(",");
                    long date;
                    try {
                        date = Long.parseLong(args[3]);
                    } catch (Exception e) {
                        date = Long.MAX_VALUE;
                    }
                    if(args.length!=7){
                        String[] temp = {args[0],args[1],args[2],args[3],"","",""};
                        args = temp;
                    }

                    long current = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime().getTime();

                    long age = Long.MAX_VALUE;
                    if(date != Long.MAX_VALUE)
                    {
                        age = Math.abs(date - current);
                    }
                    String ages =  String.format(Locale.getDefault(),"%d:%02d minutes", TimeUnit.MILLISECONDS.toMinutes(age), TimeUnit.MILLISECONDS.toSeconds(age) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(age)) );

                    if (date != Long.MAX_VALUE && age < 1000 * 60 * minutes) {
                        Log.d(this.getClass().getSimpleName(), "Updated: User="+args[0]+" Lat=" + args[1] + " Lng=" + args[2] +" Age="+ages);
                        tempLocations.add(new LocationObject(args[0], Double.parseDouble(args[1]), Double.parseDouble(args[2]),args[4],args[5],args[6]));
                    } else if (date != Long.MAX_VALUE) {
                        Log.d(this.getClass().getSimpleName(),"Expired: User="+args[0]+" Lat=" + args[1] + " Lng=" + args[2] +" Age="+ages);
                        tempExpiredLocations.add(new LocationObject(args[0], Double.parseDouble(args[1]), Double.parseDouble(args[2]),args[4],args[5],args[6]));
                    }
                }
                //if there is a change
                if( isChanged(tempLocations,tempExpiredLocations) )
                {
                    locations = tempLocations;
                    expiredLocations = tempExpiredLocations;
                    Log.e(this.getClass().getSimpleName(),"Retriggering");
                    synchronized (timerMutex)
                    {
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
                Log.e(this.getClass().getSimpleName(),e.getMessage());
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
        Log.e(this.getClass().getSimpleName(),url.toString());
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
            String post_data = "";
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
    }
}
