package com.example.toshiba.ternakku.ui.adapter;

import android.content.Context;
import android.util.Log;
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

public class Adapter_Ternak_List extends ArrayAdapter<ItemRow> {

	List<ItemRow> data;
	Context context;
	int layoutResID;
	private goCamera mGoCamera;

	public Adapter_Ternak_List(Context context, int layoutResourceId, List<ItemRow> data) {
		super(context, layoutResourceId, data);

		this.data = data;
		this.context = context;

		this.layoutResID = layoutResourceId;

		// TODO Auto-generated constructor stub
	}

	public void goCm(goCamera camera) {
		mGoCamera = camera;

	}
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
			holder.icon = (ImageView) row.findViewById(R.id.example_image);
			holder.button1 = (ImageView) row.findViewById(R.id.swipe_button1);
			holder.button2 = (ImageView) row.findViewById(R.id.swipe_button2);
			holder.button3 = (ImageView) row.findViewById(R.id.swipe_button3);
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

				Log.e("WWWWWWW", String.valueOf(position));
				mGoCamera.OnGetIdCamera(v,position);
			}

		});

		holder.button2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(context, "Button 2 Clicked", Toast.LENGTH_SHORT).show();
			}
		});

		holder.button3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(context, "Button 3 Clicked", Toast.LENGTH_SHORT);
			}
		});


		return row;

	}
	public interface goCamera {
		public abstract void OnGetIdCamera(View view,int position);
	}

	static class NewsHolder {

		TextView itemName;
		ImageView icon;
		ImageView button1;
		ImageView button2;
		ImageView button3;
	}



}






