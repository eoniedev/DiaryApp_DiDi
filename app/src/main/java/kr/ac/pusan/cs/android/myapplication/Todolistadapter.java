package kr.ac.pusan.cs.android.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Todolistadapter extends BaseAdapter {
    private ArrayList<Todolistitem> listitem = new ArrayList<Todolistitem>();
    private ArrayList<Integer> checkchange = new ArrayList<Integer>();
    CheckBox checkBox;
    int tmpcheck = 0;
    public Todolistadapter() {

    }
    @Override
    public int getCount() {
        return listitem.size();
    }
    public int getcheck(int position) {return listitem.get(position).getchecked();}
    @Override
    public Object getItem(int position) {
        return listitem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item, parent, false);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.textView);
        checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
        Todolistitem todolistitem = listitem.get(position);
        textView.setText(todolistitem.getTodotext());
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()) {
                    tmpcheck = 1;
                }
                else {
                    tmpcheck = 0;
                }
                listitem.get(position).setchecked(tmpcheck);
                //checkchange.set(position,tmpcheck);
            }
        });
        if(listitem.get(position).getchecked() == 1)
            checkBox.setChecked(true);
        else
            checkBox.setChecked(false);
        return convertView;
    }
    public void addItem(String text,int check) {
        Todolistitem item = new Todolistitem();
        item.setTodotext(text);
        item.setchecked(check);
        listitem.add(item);
    }
    public void  setItem() {
        listitem.clear();
    }
    public ArrayList<Integer> getCheck() {return (checkchange);}
}
