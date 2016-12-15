package make448greatagain.studybuddy;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;

import java.util.concurrent.ExecutionException;

import make448greatagain.studybuddy.Messaging.MessageHandler;
import make448greatagain.studybuddy.Messaging.PrivateMessage;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static make448greatagain.studybuddy.Messaging.MessageHandler.userMessages;
import static make448greatagain.studybuddy.NearbyClients.locations;
import static make448greatagain.studybuddy.R.id.login;

/**
 * Created by Michael on 12/13/2016.
 */

public class TestingSuite {

    public static final String DEBUG_TAG = "TestingSuite";
    public static final String TEST_USER_NAME ="TestingDummy";
    public static final String TEST_PASSWORD = "TestingPassword";
    Context context;

    public void execute(Context con) {
        Log.e(DEBUG_TAG,"Running Tests");
        context = con;
        testLogin();
        testSendReceiveMessage();
        testUpdateLocation();


    }

    private boolean testLogin(){
        Log.e(DEBUG_TAG,"Running Login Tests");
        LoginActivity loginActivity = new LoginActivity();
        try{
            loginActivity.new UserLoginTask("Michael","Password",DBManager.SUCCESS).execute().get();
            loginActivity.new UserLoginTask("Michael","Invalid",DBManager.PASSWORD_ERROR).execute().get();
            loginActivity.new UserLoginTask(TEST_USER_NAME,TEST_PASSWORD,DBManager.USERNAME_ERROR).execute().get();
        }catch(Exception e){
            //
        }
        boolean ret = !loginActivity.isEmailValid("&&") && loginActivity.isEmailValid("Michael")
                && !loginActivity.isPasswordValid(";;") && loginActivity.isPasswordValid("Password");

        Log.e(DEBUG_TAG,"Login Page-Username and Password validations: "+(ret?"Success":"Failure"));
        return true;
    }
    private boolean testSendReceiveMessage(){
        Log.e(DEBUG_TAG,"Running Messaging Tests");
        User user = new User(0,"MessagingTestUser","Password");
        try{
            UserManager.addUser(user);
        }catch (NewUserBeforeRemoveException e){
            //
        }

        MessageHandler.subscribeServices();

        try{
            Log.e(DEBUG_TAG,"Waiting 10s for Message Get-Sync.");
            Thread.sleep(10000);
        }catch(InterruptedException e){
            //
        }
        int original_size = MessageHandler.userMessages.size();
        MessageHandler.enqueue( new PrivateMessage("MessagingTestUserTo","MessagingTestUser","Message"));
        try{
            Log.e(DEBUG_TAG,"Waiting 20s for Message Post-Sync.");
            Thread.sleep(20000);
        }catch(InterruptedException e){
            //
        }
        int new_size = MessageHandler.userMessages.size();
        boolean pass = (new_size > original_size);
        Log.e(DEBUG_TAG,"Messaging-Sending and Receiving new message: "+(pass?"Success":"Failure"));
        UserManager.logout();
        return true;
    }
    private boolean testUpdateLocation(){
        Log.e(DEBUG_TAG,"Running UpdateLocation Tests");
        User user = new User(0,"LoginTestUser","Password");
        try{
            UserManager.addUser(user);
        }catch (NewUserBeforeRemoveException e){
            //
        }
        LocationUpdater.test = true;
        try{
            Log.e(DEBUG_TAG,"Delaying 2s for start Threads to settle.");
            Thread.sleep(10000);
        }catch(InterruptedException e){
            //
        }
        NearbyClients.subscribeServices();
        LocationUpdater locationUpdater = new LocationUpdater();
        try{
            Log.e(DEBUG_TAG,"Waiting 10s for UpdateLocation Initialization.");
            Log.e(DEBUG_TAG,"Waiting 10s for NearbyClient Initialization.");
            Thread.sleep(10000);
        }catch(InterruptedException e){
            //
        }
        LocationWrapper location1 = new LocationWrapper(45.7128,74.0059);
        LocationWrapper location2 = new LocationWrapper(45.7128,74.0059);
        locationUpdater.onLocationChanged(location1);
        try{
            Log.e(DEBUG_TAG,"Waiting 20s for UpdateLocation Update.");
            Thread.sleep(20000);
        }catch(InterruptedException e){
            //
        }
        boolean pass = false;
        for(LocationObject loc : NearbyClients.locations){
            if(Math.abs(loc.lat - 45.7128)<0.1 && Math.abs(loc.lat- 74.0059)<0.1 ){
                pass = true;
            }
        }
        try{
            Log.e(DEBUG_TAG,"Delaying 10s Between Updates.");
            Thread.sleep(10000);
        }catch(InterruptedException e){
            //
        }

        locationUpdater.onLocationChanged(location2);
        try{
            Log.e(DEBUG_TAG,"Waiting 20s for UpdateLocation Update.");
            Thread.sleep(20000);
        }catch(InterruptedException e){
            //
        }
        boolean pass2 = false;
        for(LocationObject loc : NearbyClients.locations){
            if(Math.abs(loc.lat - 45.7128) <0.1 && Math.abs(loc.lng - 74.0059)<0.1 ){
                pass2 = true;
            }
        }
        boolean rtn = pass && pass2;
        Log.e(DEBUG_TAG,"Location-Updating Location1: " + ((pass2)?"Success":"Failure"));
        Log.e(DEBUG_TAG,"Location-Updating Location2: " + ((pass2)?"Success":"Failure"));

        LocationUpdater.test = false;
        return true;
    }
}
