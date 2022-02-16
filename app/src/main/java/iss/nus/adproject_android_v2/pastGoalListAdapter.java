package iss.nus.adproject_android_v2;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class pastGoalListAdapter extends ArrayAdapter<Object> {

    private final Context context;
    private final ArrayList<Goal> PastGoals;

    public pastGoalListAdapter(Context context,ArrayList<Goal> PastGoals){

        super(context,R.layout.mealrow);
        this.context = context;
        this.PastGoals = PastGoals;

        addAll(new Object[PastGoals.size()]);

    }

    public View getView(int pos, View view, @NonNull ViewGroup parent){
        if (view == null){

            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.pastgoalrow,parent,false);

        }

        Goal goal = PastGoals.get(pos);


        TextView titleView = view.findViewById(R.id.goalTitle);
        titleView.setText(goal.getGoalDescription());

        TextView startView = view.findViewById(R.id.goalStartDate);
        startView.setText(goal.getStartDate() + " to ");

        TextView endView = view.findViewById(R.id.goalEndDate);
        endView.setText(goal.getEndDate());

        TextView StatusView = view.findViewById(R.id.goalStatus);
        StatusView.setText(goal.getStatus());
        return view;
    }
}
