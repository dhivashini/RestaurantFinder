package sjsu.hanumesh.restaurantfinder;

import android.app.ActionBar;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yelp.clientlib.entities.Business;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class SingleBusinessActivity extends AppCompatActivity {
    DatabaseHelper myDb;
    Business business;
    boolean isPresentInFav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_single_business);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.restaurantfinder2);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4c8994")));
        myDb = new DatabaseHelper(this);
        Bundle extras = getIntent().getExtras();
        final ImageButton heart = (ImageButton) findViewById(R.id.white);
        if (extras != null) {
            business = (Business) extras.get("business");
            TextView name = (TextView) findViewById(R.id.name);
            TextView rating = (TextView) findViewById(R.id.rating);
            TextView location = (TextView) findViewById(R.id.location);
            ImageView imageView = (ImageView) findViewById(R.id.icon);
            TextView phone = (TextView) findViewById(R.id.phone);
            TextView noOfReviews = (TextView) findViewById(R.id.no_of_reviews);
            ImageView map = (ImageView) findViewById(R.id.map);
            name.setText(business.name());
            rating.setText(business.rating().toString());
            location.setText(business.location().displayAddress().toString().replace("[","").replace("]",""));
            phone.setText(business.phone());
            noOfReviews.setText(business.reviewCount().toString());
            TextView snippet = (TextView) findViewById(R.id.snippet);
            snippet.setText(business.snippetText());
            isPresentInFav = myDb.isPresent(business.name().replace("'",""),business.location().displayAddress().toString().replace("[", "").replace("]", ""));
            if (isPresentInFav)
                heart.setImageResource(R.drawable.ired);

            //whiteHeart.setClickable(false);
            try {
                Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(business.imageUrl()).getContent());
                imageView.setImageBitmap(bitmap);
                double lati = business.location().coordinate().latitude();
                double longi = business.location().coordinate().longitude();
                String URL = "https://maps.googleapis.com/maps/api/staticmap?center=" +lati + "," + longi + "&zoom=15&size=200x200&" +
                        "&markers=color:red%7C" +lati + "," + longi +"&key=AIzaSyDNNGuz9L4WNLSio3AyE13yqTba_M_ufCE";
                Bitmap bmp = BitmapFactory.decodeStream((InputStream) new URL(URL).getContent());
                map.setImageBitmap(bmp);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!myDb.isPresent(business.name().replace("'",""), business.location().displayAddress().toString().replace("[", "").replace("]", ""))) {
                    String isInserted = myDb.insertData(business.name().replace("'",""), business.rating().toString(), business.imageUrl(), business.location().displayAddress().toString().replace("[", "").replace("]", ""));
                    if (isInserted.equals("added")) {
                        Toast.makeText(SingleBusinessActivity.this, "Added To Favorites", Toast.LENGTH_SHORT).show();
                        //Drawable d = Drawable.createFromPath("@drawable/redcolor");
                        heart.setImageResource(R.drawable.ired);
                        //whiteHeart.setClickable(false);
                    } else if (isInserted.equals("error"))
                        Toast.makeText(SingleBusinessActivity.this, "Unable To Add !", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(SingleBusinessActivity.this, "Already in Favorites !", Toast.LENGTH_SHORT).show();
                } else {
                    boolean deleteStatus = myDb.deleteRecord(business.name().replace("'",""), business.location().displayAddress().toString().replace("[", "").replace("]", ""));
                    heart.setImageResource(R.drawable.iwhite);
                    Toast.makeText(SingleBusinessActivity.this, "Removed from Favorites !", Toast.LENGTH_SHORT).show();
                }
            }

        });


    }

}
