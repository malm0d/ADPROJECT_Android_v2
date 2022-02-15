package iss.nus.adproject_android_v2;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class RecSearchResultAdapter extends ArrayAdapter<Object>  {
//    private final Context context;
//    protected String[] images, titles;
//    public RecSearchResultAdapter(Context context, String[] titles){
//        super(context, R.layout.rec_result_row);
//        this.context = context;
//        //this.images = images;
//        this.titles = titles;
//
//        addAll(new Object[titles.length]);
//        System.out.println("Inside Adapter");
//        for (String s : titles){
//            System.out.println("adapter " + s);
//        }
//    }
//
//    @androidx.annotation.NonNull
//    public View getView(int pos, View view, @NonNull ViewGroup parent) {
//        if (view == null) {
//            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
//                    Activity.LAYOUT_INFLATER_SERVICE);
//            // if we are not responsible for adding the view to the parent,
//            // then attachToRoot should be 'false' (which is in our case)
//            view = inflater.inflate(R.layout.rec_result_row, parent, false);
//        }
//
//        // set the image for ImageView
////        ImageView imageView = view.findViewById(R.id.foodImage);
////        int id = context.getResources().getIdentifier(images[pos],
////                "drawable", context.getPackageName());
////        imageView.setImageResource(id);
//
//        // set the text for TextView
//        TextView textView = view.findViewById(R.id.foodTitle);
//        textView.setText(titles[pos]);
//
//        return view;
//    }
    
    private final Context context;
    private String[] titles;
    private String[] authors;
    private String[] rFeelings;
    private String[] trackScores;
    private String[] imageUrls;
    public RecSearchResultAdapter(Context context, String[] titles, String[] authors,
                                  String[] rFeelings, String[] trackScores, String[] imageUrls){
        super(context, R.layout.rec_result_row);
        this.context = context;
        this.titles = titles;
        this.authors = authors;
        this.rFeelings = rFeelings;
        this.trackScores = trackScores;
        this.imageUrls = imageUrls;
        addAll(new Object[titles.length]);
    }

    @androidx.annotation.NonNull
    public View getView(int pos, View view, @NonNull ViewGroup parent) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.rec_result_row, parent, false);
        }

        ImageView entryImage= view.findViewById(R.id.recEntryImage);
        entryImage.setImageResource(R.drawable.emoji_happy);

        TextView entryTitle = view.findViewById(R.id.recEntryTitle);
        entryTitle.setText(titles[pos]);

        TextView entryAuthor = view.findViewById(R.id.recEntryAuthor);
        entryAuthor.setText("by " + authors[pos]);

        ImageView entryFeeling = view.findViewById(R.id.recFeeling);
        String feel = rFeelings[pos].toLowerCase();
        if (feel.equalsIgnoreCase("cry")){
            System.out.println("1");
            entryFeeling.setImageResource(R.drawable.emoji_crying);
        }
        else if (feel.equalsIgnoreCase("pensive")){
            System.out.println("2");
            entryFeeling.setImageResource(R.drawable.emoji_pensive);
        }
        else if (feel.equalsIgnoreCase("happy")){
            System.out.println("3");
            entryFeeling.setImageResource(R.drawable.emoji_happy);
        }
        else if (feel.equalsIgnoreCase("joy")){
            System.out.println("4");
            entryFeeling.setImageResource(R.drawable.emoji_joyful);
        }

        TextView entryTrack = view.findViewById(R.id.recTrack);
        entryTrack.setText(trackScores[pos]);

        return view;
    }
}
