package com.matrix_maeny.alarm.addTime;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.matrix_maeny.alarm.R;

import java.lang.reflect.Array;
import java.sql.Time;
import java.util.ArrayList;

public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.viewHolder> {

    Context context;
    ArrayList<TimeModel> list;

    TimeAdapterListener listener;

    public TimeAdapter(Context context, ArrayList<TimeModel> list) {
        this.context = context;
        this.list = list;

        try {
            listener = (TimeAdapterListener) context;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Some error occurred: TimeAdapter::34", Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.alarm_model, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        TimeModel model = list.get(position);
        holder.timeTextView.setText(model.getTimeString());

        holder.enableSwitch.setChecked(model.isEnabled());

        holder.enableSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {

            listener.getCheckedResponse(isChecked,holder.getAdapterPosition());

        });


        holder.cardView.setOnLongClickListener(v -> {

            PopupMenu popupMenu = new PopupMenu(context, holder.cardView);
            popupMenu.getMenuInflater().inflate(R.menu.time_adapter_popup_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                deleteTime(holder.getAdapterPosition());
                return true;
            });
            popupMenu.show();
            return true;
        });
    }

    private void deleteTime(int adapterPosition) {

        TimeDB db = new TimeDB(context.getApplicationContext());
        Cursor cursor = db.getData();

        for(int i=0;i<=adapterPosition;i++) cursor.moveToNext();
        String timeText =  cursor.getString(2);

        if (!db.deleteTime(adapterPosition)) {
            Toast.makeText(context, "Some error occurred: TimeAdapter::82", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();


            updateDataBase();
            listener.cancelAlarms(timeText);

        }
        db.close();


    }

    private void updateDataBase() {
        TimeDB db = new TimeDB(context.getApplicationContext());

        Cursor cursor = db.getData();

        ArrayList<TimeModel> tempList = new ArrayList<>();

        if(cursor.getCount() != 0){

            while (cursor.moveToNext()) {
                String timeTxt = cursor.getString(2);
                int enabled = cursor.getInt(3);
                String[] arr = timeTxt.split(":");

                int hour = Integer.parseInt(arr[0].trim());
                int minute = Integer.parseInt(arr[1].trim());
                int code = cursor.getInt(1);

                TimeModel model = new TimeModel(hour,minute,enabled);
                model.setCode(code);
                tempList.add(model);
            }

        }

        db.deleteAll();

        TimeModel m;
        for(int i=0;i<tempList.size();i++){
            m = tempList.get(i);
            if(!db.insertTime(i,m.getCode(),m.getTimeString(),m.getEnabled())){
                Toast.makeText(context, "Some error occurred: TimeAdapter:125", Toast.LENGTH_SHORT).show();
            }
        }

        listener.refresh();
        db.close();

    }


    public interface TimeAdapterListener {
        void getCheckedResponse(boolean seated,int adapterPosition);

        void cancelAlarms(String timeText);
        void refresh();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        TextView timeTextView;
        Switch enableSwitch;
        CardView cardView;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            timeTextView = itemView.findViewById(R.id.timeTextView);
            enableSwitch = itemView.findViewById(R.id.enableSwitch);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

}
