package make448greatagain.studybuddy;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Michael on 11/21/2016.
 */

public class NetworkingConnection {
    public static HttpURLConnection createNewConnection(URL url) throws IOException
    {
        HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
        httpcon.setRequestMethod("POST");
        httpcon.setDoOutput(true);
        httpcon.setDoInput(true);
        return httpcon;
    }

}
