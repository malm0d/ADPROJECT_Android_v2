package iss.nus.adproject_android_v2;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class RecSearchResultAdapter extends ArrayAdapter<Object>  {
    private final Context context;
    protected String[] images, titles;
    public RecSearchResultAdapter(Context context, String[] titles){
        super(context, R.layout.rec_result_row);
        this.context = context;
        //this.images = images;
        this.titles = titles;

        addAll(new Object[titles.length]);
        System.out.println("Inside Adapter");
        for (String s : titles){
            System.out.println("adapter " + s);
        }
    }

    @androidx.annotation.NonNull
    public View getView(int pos, View view, @NonNull ViewGroup parent) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Activity.LAYOUT_INFLATER_SERVICE);
            // if we are not responsible for adding the view to the parent,
            // then attachToRoot should be 'false' (which is in our case)
            view = inflater.inflate(R.layout.rec_result_row, parent, false);
        }

        // set the image for ImageView
//        ImageView imageView = view.findViewById(R.id.foodImage);
//        int id = context.getResources().getIdentifier(images[pos],
//                "drawable", context.getPackageName());
//        imageView.setImageResource(id);

        // set the text for TextView
        TextView textView = view.findViewById(R.id.foodTitle);
        textView.setText(titles[pos]);

        return view;
    }
}
