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

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.databind.ser.impl.IndexedStringListSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecSearchResultAdapter extends ArrayAdapter<Object>  {
    
    private final Context context;
    private String[] titles;
    private String[] authors;
    private String[] rFeelings;
    private String[] trackScores;
    private String[] imageUrls;
    Map<String, Integer> feelings;

    public RecSearchResultAdapter(Context context, String[] titles, String[] authors,
                                  String[] rFeelings, String[] trackScores, String[] imageUrls,
                                  Map<String, Integer> feelings){
        super(context, R.layout.rec_result_row);
        this.context = context;
        this.titles = titles;
        this.authors = authors;
        this.rFeelings = rFeelings;
        this.trackScores = trackScores;
        this.imageUrls = imageUrls;
        this.feelings = feelings;
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
        String imageApiUrl = context.getResources().getString(R.string.IP) + "/api/recommend/getEntryPic";
        String queryString = "?fileName=";
        Glide.with(view)
                .load(imageApiUrl + queryString + imageUrls[pos])
                .placeholder(R.drawable.no_img)
                .into(entryImage);

        TextView entryTitle = view.findViewById(R.id.recEntryTitle);
        entryTitle.setText(titles[pos]);

        TextView entryAuthor = view.findViewById(R.id.recEntryAuthor);
        entryAuthor.setText("by " + authors[pos]);

        ImageView entryFeeling = view.findViewById(R.id.recFeeling);
        int feeling = feelings.get(rFeelings[pos].toLowerCase());
        entryFeeling.setImageResource(feeling);

        TextView entryTrack = view.findViewById(R.id.recTrack);
        entryTrack.setText(trackScores[pos]);

        return view;
    }
}
