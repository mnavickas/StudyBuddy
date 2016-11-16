package make448greatagain.studybuddy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by Michael on 11/3/2016.
 *
 */
public class ConnectivityReceiver extends BroadcastReceiver {

    private static volatile boolean dataconnected = true;
    private static volatile boolean gpsconnected = true;

    public static void setDataConnected(boolean d)
    {
        dataconnected = d;
    }
    public static void setGpsConnected(boolean d)
    {
        gpsconnected = d;
    }

    public static boolean isDataconnected(){
        return dataconnected;
    }
    public static boolean isGpsconnected(){
        return gpsconnected;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(ConnectivityReceiver.class.getSimpleName(), "action: "
                + intent.getAction());
       ConnectivityManager cm = ((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE));
        if(cm != null ) {
            NetworkInfo ni = cm.getActiveNetworkInfo();
            if (ni != null) {
                dataconnected = ni.isConnected();
            } else {
                dataconnected = false;
            }
        }
        Log.e(ConnectivityReceiver.class.getSimpleName(), "Data action: "
                + (dataconnected?"Data Connected":"Data Not Connected"));

        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if(lm != null)
        {
            gpsconnected = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        Log.e(ConnectivityReceiver.class.getSimpleName(), "GPS action: "
                + (gpsconnected?"GPS Connected":"GPS Not Connected"));
    }
}