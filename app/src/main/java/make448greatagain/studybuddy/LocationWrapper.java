package make448greatagain.studybuddy;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Michael on 10/31/2016.
 */

public class LocationWrapper {
    User user;
    double lat, lng;
    public LocationWrapper(double lat, double lng){
        this.user = UserManager.getUser();
        this.lat = lat;
        this.lng = lng;
    }
    public LocationWrapper(LatLng coord){
        this.user = UserManager.getUser();
        this.lat = coord.latitude;
        this.lng = coord.longitude;
    }
}
