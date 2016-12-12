package make448greatagain.studybuddy.Messaging;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.Toast;

import make448greatagain.studybuddy.R;
import make448greatagain.studybuddy.UserManager;

import static android.R.id.message;


/**
 * Created by Michael on 11/23/2016.
 *
 */
public class PopupMessageCreator {

    public static void create(final Context context, final String user_to){
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle("Message to:");
        alert.setMessage(user_to);

        // Set an EditText view to get user input
        final EditText input = new EditText(context);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String msg = input.getText().toString();
                if(msg.matches(".*[,\'\\\\\"$%^&*()].*"))
                {
                    Toast myToast = Toast.makeText(context, "Invalid Character", Toast.LENGTH_LONG);
                    myToast.show();
                    return;
                }
                PrivateMessage message = new PrivateMessage(user_to, UserManager.getUser().username, msg);
                MessageHandler.enqueue(message);
                Toast myToast = Toast.makeText(context, "Sending", Toast.LENGTH_SHORT);
                myToast.show();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }
}
