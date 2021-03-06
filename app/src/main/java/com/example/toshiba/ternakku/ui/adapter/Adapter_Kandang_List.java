package com.example.toshiba.ternakku.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.toshiba.ternakku.R;
import com.example.toshiba.ternakku.swipelistview.ItemRow;

import java.util.List;

/**
 * Created by Toshiba on 12/11/2015.
 */
public class Adapter_Kandang_List extends ArrayAdapter<ItemRow> {

    List<ItemRow> data;
    Context context;
    int layoutResID;
    //private goCamera mGoCamera;

    public Adapter_Kandang_List(Context context, int layoutResourceId, List<ItemRow> data) {
        super(context, layoutResourceId, data);

        this.data = data;
        this.context = context;

        this.layoutResID = layoutResourceId;

        // TODO Auto-generated constructor stub
    }

    //public void goCm(goCamera camera) {
    //mGoCamera = camera;


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        NewsHolder holder = null;
        View row = convertView;
        holder = null;

        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            row = inflater.inflate(layoutResID, parent, false);

            holder = new NewsHolder();

            holder.itemName = (TextView) row.findViewById(R.id.example_itemname);
            holder.icon = (ImageView) row.findViewById(R.id.example_image_kandang);
            holder.button1 = (ImageView) row.findViewById(R.id.swipe_fotokandang);
            holder.button2 = (ImageView) row.findViewById(R.id.swipe_makan);
            row.setTag(holder);
        } else {
            holder = (NewsHolder) row.getTag();
        }

        ItemRow itemdata = data.get(position);
        holder.itemName.setText(itemdata.getItemName());
        holder.icon.setImageDrawable(itemdata.getIcon());

        holder.button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Log.e("WWW", String.valueOf(position));
                //mGoCamera.OnGetIdCamera(v,position);
            }

        });

        holder.button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "Button 2 Clicked", Toast.LENGTH_SHORT).show();
            }
        });




        return row;

    }


    static class NewsHolder {

        TextView itemName;
        ImageView icon;
        ImageView button1;
        ImageView button2;
    }

	/*public interface goCamera {
		public abstract void OnGetIdCamera(View view,int position);
	}*/

}







