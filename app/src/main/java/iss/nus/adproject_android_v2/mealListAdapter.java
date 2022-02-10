package iss.nus.adproject_android_v2;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class mealListAdapter extends ArrayAdapter<Object> {

    private final Context context;
    private final ArrayList<MealHelper> Meals;

    public mealListAdapter(Context context,ArrayList<MealHelper> Meals){

        super(context,R.layout.mealrow);
        this.context = context;
        this.Meals = Meals;

        addAll(new Object[Meals.size()]);

    }

    public View getView(int pos, View view, @NonNull ViewGroup parent){
        if (view == null){

            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.mealrow,parent,false);

        }

        MealHelper meals = Meals.get(pos);

        ImageView mealImage = view.findViewById(R.id.mealImage);
        mealImage.setImageResource(R.drawable.food2);

        TextView titleView = view.findViewById(R.id.mealtitle);
        titleView.setText(meals.getTitle());

        TextView timeView = view.findViewById(R.id.mealtime);
        timeView.setText(meals.getTimeStamp());

        TextView scoreView = view.findViewById(R.id.mealscore);
        String ScoreStr = "Score: " + meals.getTrackScore();
        scoreView.setText(ScoreStr);

        return view;
    }



}
