package make448greatagain.studybuddy.Messaging;

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
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import make448greatagain.studybuddy.ConnectivityReceiver;
import make448greatagain.studybuddy.NetworkingConnection;
import make448greatagain.studybuddy.UserManager;


/**
 * Created by Michael on 11/21/2016.
 *
 */
public class MessageHandler{

    private static BlockingQueue<PrivateMessage> messageQueue;
    private static Thread thread;
    private static Thread messageThread;
    private static ExecutorService mThreadPool = Executors.newSingleThreadExecutor();
    private static ExecutorService mThreadPool2 = Executors.newSingleThreadExecutor();
    final static Object messagesMutex = new Object();
    public static LinkedList<MessageData> userMessages = new LinkedList<>();


    private static class MessageReceiver{
        private static void updateData(){
            String[] messages = getMessagesForUser();

            LinkedList<MessageData> msg_times = new LinkedList<>();
            for (String msg : messages) {
                String[] result = msg.split("\\,");
                if (result.length == 4) {
                    MessageData msgdata = new MessageData(result[1], result[0], result[2], result[3]);
                    msgdata.msg = msgdata.from + ": " + msgdata.msg;
                    msg_times.add(msgdata);
                }
            }
            //Log.e(this.getClass().getSimpleName(), "List" + msg_times.size());
            Collections.sort(msg_times, new Comparator<MessageData>() {
                @Override
                public int compare(MessageData o1, MessageData o2) {
                    DateFormat formatter;
                    java.util.Date date1 = new java.util.Date();
                    java.util.Date date2 = new java.util.Date();
                    formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    int value;
                    try {
                        date1 = formatter.parse(o1.time);
                        date2 = formatter.parse(o2.time);
                    } catch (ParseException e) {
                        //
                    }
                    return (int) (date1.getTime() - date2.getTime());
                }
            });
            String[] lastResult = new String[msg_times.size()];
            for (int i = 0; i < msg_times.size(); i++) {
                lastResult[i] = msg_times.get(i).msg;
            }
            synchronized (messagesMutex){
                userMessages = msg_times;
            }

        }

        static String user;
        private static String[] getMessagesForUser() {
            user = UserManager.getUser().username;
            Log.e("", user);
            String result;
            try {
                result = pollDatabase();
            }catch (IOException e){
                result = "";
            }

            return result.split("\\$");
        }
        /**
         * Get list of all clients
         * @return String of nearby client results from DB
         * @throws IOException
         */
        private static String pollDatabase() throws IOException
        {

            if(!ConnectivityReceiver.isDataconnected())
            {
                throw new IOException();
            }
            URL url = new URL("https://people.eecs.ku.edu/~mnavicka/Android/getUserMessages.php");

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

            return result;
        }

        /**
         * Execute the request
         * @param httpcon HTTPConnection Instance
         * @throws IOException
         */
        private static void postData(final HttpURLConnection httpcon) throws IOException
        {
            OutputStream outputStream = httpcon.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(user, "UTF-8");
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
        }
    }


    public static void subscribeServices(){
        messageQueue = new LinkedBlockingQueue<>(10);
        messageThread = new Thread(){
            volatile boolean running = false;
            public void start(){
                running = true;
                super.start();
            }
            public void run(){

                while(running){
                    mThreadPool.execute(new Runnable(){
                        public void run(){
                            if(UserManager.getUser()!=null) {
                                MessageReceiver.updateData();
                                Log.e("MessageReceiver", userMessages.size() + "");
                            }
                        }
                    });
                    try{
                        sleep(1000);
                    }catch(InterruptedException e){

                    }

                }


            }
        };
        thread = new Thread(){
            boolean running = false;
            public void start(){
                running = true;
                super.start();
            }
            public void run(){
                while(running)
                {
                    try{
                        send( messageQueue.take() );
                    }catch(InterruptedException e){
                        Log.e("MessageHandler",e.getMessage());
                    }

                }
            }
        };
        thread.start();
        messageThread.start();
    }


    public static void enqueue(PrivateMessage message){
        if(messageQueue == null){
            messageQueue = new LinkedBlockingQueue<>(10);
        }
        Log.e("ENQUEUE",message.msg);
        messageQueue.add(message);
    }

    private static void send(final PrivateMessage message){
        mThreadPool2.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://people.eecs.ku.edu/~mnavicka/Android/postMessage.php");
                    HttpURLConnection httpcon = NetworkingConnection.createNewConnection(url);
                    postData( httpcon, message );

                    httpcon.getInputStream().close();

                    httpcon.disconnect();

                } catch (IOException e) {
                    Log.e("DBLocationManager", e.getMessage());
                }
            }
            private void postData(final HttpURLConnection httpcon, PrivateMessage message) throws IOException
            {

                OutputStream outputStream = httpcon.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("user_from", "UTF-8") + "=" + URLEncoder.encode(message.user_from,"UTF-8")
                        + "&" +
                        URLEncoder.encode("user_to", "UTF-8") + "=" + URLEncoder.encode(message.user_to,"UTF-8")
                        + "&" +
                        URLEncoder.encode("msg", "UTF-8") + "=" + URLEncoder.encode(message.msg,"UTF-8")
                        + "&" +
                        URLEncoder.encode("msg_id", "UTF-8") + "=" + message.msg_id;
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
            }
        });
    }
}
