package sjsu.hanumesh.restaurantfinder;

import android.app.ActionBar;
import android.app.ListActivity;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FavoritesActivity extends ListActivity {
    DatabaseHelper myDb;
    List list = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);


        myDb = new DatabaseHelper(this);

        Cursor res = myDb.getAllData();
        while (res.moveToNext()){
            HashMap<String, String> hmap = new HashMap<String, String>();
            hmap.put("name",res.getString(1));
            hmap.put("rating",res.getString(2));
            hmap.put("imageUrl",res.getString(3));
            hmap.put("location",res.getString(4));
            list.add(hmap);
        }
        ListAdapter adapter = new MySimpleArrayAdapter(this, list);

        setListAdapter(adapter);


    }
}
