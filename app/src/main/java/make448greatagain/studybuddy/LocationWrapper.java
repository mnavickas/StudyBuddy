package make448greatagain.studybuddy;

import com.google.android.gms.maps.model.LatLng;

/**
 * Wrapper for tying a user to a location, similar to LocationObject
 * Created by Michael on 10/31/2016.
 */
class LocationWrapper {
    /**
     * User whose coordinates we have
     */
    User user;
    /**
     * User coordinates
     */
    double lat, lng;

    /**
     * Ties values to current User
     * @param lat Latitude
     * @param lng Longitude
     */
    LocationWrapper(double lat, double lng){
        this.user = UserManager.getUser();
        this.lat = lat;
        this.lng = lng;
    }

    /**
     * Ties values to current User
     * @param coord User Coordinates
     */
    public LocationWrapper(LatLng coord){
        this.user = UserManager.getUser();
        this.lat = coord.latitude;
        this.lng = coord.longitude;
    }
}
