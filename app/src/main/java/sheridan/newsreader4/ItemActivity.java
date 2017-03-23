package sheridan.newsreader4;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ItemActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        // get references to widgets
        TextView titleTextView = (TextView) findViewById(R.id.titleTextView);
        TextView pubDateTextView = (TextView) findViewById(R.id.pubDateTextView);

        TextView linkTextView = (TextView) findViewById(R.id.linkTextView);

        // get the intent
        Intent intent = getIntent();

        // get data from the intent
        String pubDate = intent.getStringExtra("pubDate");
        String title = intent.getStringExtra("title");


        // display data on the widgets
        pubDateTextView.setText(pubDate);
        titleTextView.setText(title);


        // set listener
        linkTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // get the intent
        Intent intent = getIntent();

        // get the Uri for the link
        String link = intent.getStringExtra("link");
        Uri viewUri = Uri.parse(link);

        // create the intent and start it
        Intent viewIntent = new Intent(Intent.ACTION_VIEW, viewUri);
        startActivity(viewIntent);
    }
}
