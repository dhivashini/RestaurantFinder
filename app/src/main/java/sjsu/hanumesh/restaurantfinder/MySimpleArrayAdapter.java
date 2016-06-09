package sjsu.hanumesh.restaurantfinder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class MySimpleArrayAdapter extends ArrayAdapter<List> {
    private final Context context;
    private final List values;

    public MySimpleArrayAdapter(Context context, List values) {
        super(context, R.layout.rowlayout, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
        HashMap<String, String> detail= (HashMap<String, String>) values.get(position);
        TextView name = (TextView) rowView.findViewById(R.id.name);
        TextView rating = (TextView) rowView.findViewById(R.id.ratingText);
        TextView location = (TextView) rowView.findViewById(R.id.location);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        name.setText(detail.get("name"));
        rating.setText("Rating: "+detail.get("rating"));
        location.setText(detail.get("location"));
        try {
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(detail.get("imageUrl")).getContent());
            imageView.setImageBitmap(bitmap);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
            return rowView;
        }
}
