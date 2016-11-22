package make448greatagain.studybuddy.Messaging;

import android.os.AsyncTask;
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
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import make448greatagain.studybuddy.NetworkingConnection;


/**
 * Created by Michael on 11/21/2016.
 *
 */
public class MessageHandler{

    private static BlockingQueue<PrivateMessage> messageQueue;
    private static Thread thread;
    private static ExecutorService mThreadPool = Executors.newSingleThreadExecutor();

    public static void subscribeServices(){
        messageQueue = new LinkedBlockingQueue<>(50);
        thread = new Thread(){
            boolean running = false;
            public void start(){
                running = true;
                super.start();
            }
            public void run(){
                while(running)
                {
                    Log.e(getClass().getSimpleName(),"RUNNING");
                    try{
                        send( messageQueue.take() );
                    }catch(InterruptedException e){
                        Log.e("MessageHandler",e.getMessage());
                    }

                }
            }
        };
        thread.start();
    }


    public static void enqueue(PrivateMessage message){
        if(messageQueue == null){
            messageQueue = new LinkedBlockingQueue<>(50);
        }
        messageQueue.add(message);
    }

    private static void send(final PrivateMessage message){
        mThreadPool.execute(new Runnable() {
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
    public static LinkedList<PrivateMessage> receiveFor(final String user){
        LinkedList<PrivateMessage> list;
        try{
            list = new messageReceiver().execute( user ).get();
        }catch(ExecutionException | InterruptedException e){
            list = new LinkedList<>();
            Log.wtf("MessageHandler",e.getMessage());
        }
        return list;
    }

    private static class messageReceiver extends AsyncTask<String,Void,LinkedList<PrivateMessage>>
    {
        public LinkedList<PrivateMessage> doInBackground(String... param)
        {
            final LinkedList<PrivateMessage> list = new LinkedList<>();
            try {
                URL url = new URL("https://people.eecs.ku.edu/~mnavicka/Android/readAndDeleteMessagesForUser.php");
                HttpURLConnection httpcon = NetworkingConnection.createNewConnection(url);
                postData( httpcon, param[0] );

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

                String[] messages = result.split("&");
                if(messages.length > 0)
                {
                    for(String message: messages) {

                        String[] tokens = message.split(",");
                        if(tokens.length == 4){
                            list.add(new PrivateMessage(tokens[0], tokens[1], tokens[2], Long.parseLong(tokens[3])));
                        }
                    }
                }
            } catch (IOException e) {
                Log.e(this.getClass().getSimpleName(), e.getMessage());
            }
            return list;
        }
        private void postData(final HttpURLConnection httpcon,final String user) throws IOException
        {

            OutputStream outputStream = httpcon.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("user_to", "UTF-8") + "=" + URLEncoder.encode(user,"UTF-8");
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
        }
    }
}
