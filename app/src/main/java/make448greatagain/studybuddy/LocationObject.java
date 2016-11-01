package make448greatagain.studybuddy;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Michael on 10/31/2016.
 */

public class LocationObject {
    String user;
    double lat, lng;
    public LocationObject(String user,double lat, double lng){
        this.user = user;
        this.lat = lat;
        this.lng = lng;
    }

}
