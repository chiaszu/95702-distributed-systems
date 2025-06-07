package edu.heinz.ds.androidinterestingpicture;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class InterestingPicture extends AppCompatActivity {

    InterestingPicture me = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
         * The click listener will need a reference to this object, so that upon successfully finding a picture from Flickr, it
         * can callback to this object with the resulting picture Bitmap.  The "this" of the OnClick will be the OnClickListener, not
         * this InterestingPicture.
         */
        final InterestingPicture ma = this;

        /*
         * Find the "submit" button, and add a listener to it
         */
        Button submitButton = (Button)findViewById(R.id.submit);


        // Add a listener to the send button
        submitButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View viewParam) {
                String searchTerm = ((EditText)findViewById(R.id.searchTerm)).getText().toString();
                // System.out.println does not work, there's no console
                // To print debugging messages, use Log.d("some key string", "your debugging message");
                Log.d("Trace", "searchTerm = " + searchTerm);
                GetPicture gp = new GetPicture();
                gp.search(searchTerm, me, ma); // Done asynchronously in another thread.  It calls ip.pictureReady() in this thread when complete.
            }
        });
    }

    /*
     * This is called by the GetPicture object when the picture is ready.  This allows for passing back the Bitmap picture for updating the ImageView
     */
    public void pictureReady(Bitmap picture) {
        ImageView pictureView = (ImageView)findViewById(R.id.interestingPicture);
        TextView searchView = (EditText)findViewById(R.id.searchTerm);
        if (picture != null) {
            pictureView.setImageBitmap(picture);
            pictureView.setVisibility(View.VISIBLE);
        } else {
            pictureView.setImageResource(R.mipmap.ic_launcher);
            Log.d("Trace", "No picture found");
            pictureView.setVisibility(View.INVISIBLE);
        }
        searchView.setText("");
        pictureView.invalidate();
    }
}
