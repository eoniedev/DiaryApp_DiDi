package kr.ac.pusan.cs.android.myapplication;

import android.app.PendingIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class MyRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView textEvent, textTime;
        ExerciseInfo mitem;
        MyViewHolder(View view){
            super(view);
            view.setOnClickListener(this);
            textEvent = view.findViewById(R.id.textEvent);
            textTime = view.findViewById(R.id.textTime);
        }

        public void setItem(ExerciseInfo item){
            mitem = item;

        }
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick " + getPosition() + " ");
        }
    }
    private ArrayList<ExerciseInfo> exerciseInfos;

    MyRVAdapter(ArrayList<ExerciseInfo> exerciseInfos){
        this.exerciseInfos = exerciseInfos;
    }
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.exerciselist, parent, false);
        return new MyViewHolder(v);
    }

    //리사이클러뷰에 리스트 항목View들이 생성됨
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        MyViewHolder myViewHolder = (MyViewHolder) holder;
        myViewHolder.textEvent.setText(exerciseInfos.get(position).event);
        myViewHolder.textTime.setText(exerciseInfos.get(position).time);
    }
    //FoodInfoArrayList의 전체 리스트 항목 갯수를 반환해준다
    @Override
    public int getItemCount() {
        return exerciseInfos.size();
    }
}

