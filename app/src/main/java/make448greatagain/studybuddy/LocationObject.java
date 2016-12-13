package make448greatagain.studybuddy;

/**
 * Wrapper for tying a user to a location
 * Created by Michael on 10/31/2016.
 */
class LocationObject {
    /**
     * User whose coordinates we have
     */
    String user;
    /**
     * User coordinates
     */
    double lat, lng;

    String course,subject,comment;
    LocationObject(String user, double lat, double lng,String course,String subject,String comment){
        this.user = user;
        this.lat = lat;
        this.lng = lng;
        this.comment = comment;
        this.course = course;
        this.subject = subject;
    }

}
