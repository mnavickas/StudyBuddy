package make448greatagain.studybuddy;

import android.app.Application;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import make448greatagain.studybuddy.Messaging.MessageHandler;

/**
 * Created by Michael on 11/3/2016.
 */

public class AppStart extends Application {

    public AppStart(){

    }
    @Override
    public void onCreate() {
        super.onCreate();
        NearbyClients.getInstance().startThread();
        new LocationUpdater(this);
        MessageHandler.subscribeServices();

        boolean dataconnected = false;
        boolean gpsconnected = false;
        ConnectivityManager cm = ((ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE));
        if(cm != null )
        {
            NetworkInfo ni = cm.getActiveNetworkInfo();
            dataconnected = ni!= null && ni.isConnected();
        }
        LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if(lm != null)
        {
            gpsconnected = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }

        ConnectivityReceiver.setDataConnected(dataconnected);
        ConnectivityReceiver.setGpsConnected(gpsconnected);
        Log.e(this.getClass().getSimpleName(),"Data: "+dataconnected);
        Log.e(this.getClass().getSimpleName(),"GPS: "+gpsconnected);
    }
}
