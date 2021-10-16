package dte.masteriot.mdp.loadwebcontent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    public static final String LOGSLOADWEBCONTENT = "LOGSLOADWEBCONTENT"; // to clearly identify logs
    private String logTag; // to clearly identify logs
    private static final String URL_CAMERAS = "https://informo.madrid.es/informo/tmadrid/CCTV.kml";
    private static final String URL_PARK = "https://datos.madrid.es/egob/catalogo/200761-0-parques-jardines.json";
    private static final String URL_IMAGEN = "https://masteriot.etsist.upm.es/wp-content/uploads/2018/02/multimedia-internet-de-las-cosas-350x322.png";
    private static final String CONTENT_TYPE_KML = "application/vnd.google-earth.kml+xml";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String CONTENT_TYPE_IMAGE = "image/png";

    private Button btKML;
    private Button btJSON;
    private Button btIMAGE;
    private TextView text;
    private ImageView image;
    ExecutorService es;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Build the logTag with the Thread and Class names:
        logTag = LOGSLOADWEBCONTENT + ", Thread = " + Thread.currentThread().getName() + ", Class = " +
                this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".") + 1);

        // Get references to UI elements:
        btKML = findViewById(R.id.loadKML);
        btJSON = findViewById(R.id.loadJSON);
        btIMAGE = findViewById(R.id.loadIMAGE);
        text = findViewById(R.id.HTTPTextView);
        image = findViewById(R.id.HTTPImageView);


        // Create an executor for the background tasks:
        es = Executors.newSingleThreadExecutor();
    }

    // Define the handler that will receive the messages from the background thread:
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            // message received from background thread: load complete (or failure)
            String string_result;
            super.handleMessage(msg);
            Log.d(logTag, "message received from background thread");
            if((string_result = msg.getData().getString("text")) != null) {
                text.setText(string_result);
            }

            toggle_buttons(true); // re-enable the buttons
        }
    };

    // Define the handler that will receive the messages from the background thread:
    Handler handler2 = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            // message received from background thread: load complete (or failure)
            Bitmap bitmap;
            super.handleMessage(msg);
            Log.d(logTag, "message received from background thread");
            if((bitmap = msg.getData().getParcelable("img")) != null) {
                image.setImageBitmap(bitmap);
                Log.d("Bitmap", "AQUI SI QUE LLEGA");
            }

            text.setText("");





            toggle_buttons(true); // re-enable the buttons
        }
    };


    public void readKML(View view) {
        toggle_buttons(false); // disable the buttons until the load is complete
        text.setText("Loading " + URL_CAMERAS + "..."); // Inform the user by means of the TextView

        // Execute the loading task in background:
        LoadURLContents loadURLContents = new LoadURLContents(handler, CONTENT_TYPE_KML, URL_CAMERAS, false);
        es.execute(loadURLContents);
    }


    public void readJSON(View view) {
        toggle_buttons(false); // disable the buttons until the load is complete
        text.setText("Loading " + URL_PARK + "..."); // Inform the user by means of the TextView

        // Execute the loading task in background:
        LoadURLContents loadURLContents = new LoadURLContents(handler, CONTENT_TYPE_JSON, URL_PARK, false);
        es.execute(loadURLContents);
    }

    public void readIMAGE(View view) {
        toggle_buttons(false); // disable the buttons until the load is complete
        text.setText("Loading " + URL_IMAGEN + "..."); // Inform the user by means of the TextView


        // Execute the loading task in background:
        LoadURLContents loadURLContents = new LoadURLContents(handler2, CONTENT_TYPE_IMAGE, URL_IMAGEN, true);
        es.execute(loadURLContents);
    }

    private void toggle_buttons(boolean state) {
        // enable or disable buttons (depending on state)
        btKML.setEnabled(state);
        btIMAGE.setEnabled(state);
        btJSON.setEnabled(state);

    }

}