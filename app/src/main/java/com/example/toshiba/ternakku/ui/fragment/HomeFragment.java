package com.example.toshiba.ternakku.ui.fragment;

/**
 * Created by Ravi on 29/07/15.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.toshiba.ternakku.R;
import com.example.toshiba.ternakku.swipelistview.BaseSwipeListViewListener;
import com.example.toshiba.ternakku.swipelistview.ItemRow;
import com.example.toshiba.ternakku.swipelistview.SwipeListView;
import com.example.toshiba.ternakku.ui.Detail_Ternak;
import com.example.toshiba.ternakku.ui.TambahTernak;
import com.example.toshiba.ternakku.ui.adapter.Adapter_Ternak_List;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    FloatingActionButton fabBtn;
    SwipeListView swipelistview;
    Adapter_Ternak_List adapter;
    List<ItemRow> itemData;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        fabBtn = (FloatingActionButton) root.findViewById(R.id.fabbuttonternak);
        ColorStateList rippleColor = getResources().getColorStateList(R.color.hijau);
        fabBtn.setBackgroundTintList(rippleColor);

        fabBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent post = new Intent(getActivity(), TambahTernak.class);
                startActivity(post);
            }
        });

        swipelistview = (SwipeListView) root.findViewById(R.id.example_swipe_lv_list);
        itemData=new ArrayList<ItemRow>();
        adapter=new Adapter_Ternak_List(getActivity().getApplicationContext(),R.layout.costum_row_ternak,itemData);



        swipelistview.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onOpened(int position, boolean toRight) {
            }

            @Override
            public void onClosed(int position, boolean fromRight) {
            }

            @Override
            public void onListChanged() {
            }

            @Override
            public void onMove(int position, float x) {
            }

            @Override
            public void onStartOpen(int position, int action, boolean right) {
                Log.d("swipe", String.format("onStartOpen %d - action %d", position, action));
            }

            @Override
            public void onStartClose(int position, boolean right) {
                Log.d("swipe", String.format("onStartClose %d", position));
            }

            @Override
            public void onClickFrontView(int position) {
                Log.d("swipe", String.format("onClickFrontView %d", position));

                pindahPage();
                swipelistview.openAnimate(position); //when you touch front view it will open


            }

            @Override
            public void onClickBackView(int position) {
                Log.d("swipe", String.format("onClickBackView %d", position));

                swipelistview.closeAnimate(position);//when you touch back view it will close
            }

            @Override
            public void onDismiss(int[] reverseSortedPositions) {

            }

        });

        //These are the swipe listview settings. you can change these
        //setting as your requirement
        swipelistview.setSwipeMode(SwipeListView.SWIPE_MODE_BOTH); // there are five swiping modes
        swipelistview.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_DISMISS); //there are four swipe actions
        swipelistview.setSwipeActionRight(SwipeListView.SWIPE_ACTION_REVEAL);
        swipelistview.setOffsetLeft(convertDpToPixel(5f)); // left side offset
        swipelistview.setOffsetRight(convertDpToPixel(5f)); // right side offset
        swipelistview.setAnimationTime(500); // Animation time
        swipelistview.setSwipeOpenOnLongPress(true); // enable or disable SwipeOpenOnLongPress

        swipelistview.setAdapter(adapter);

        for (int i = 0; i < 10; i++) {
            itemData.add(new ItemRow("Swipe Item" + i, getResources().getDrawable(R.drawable.noimage)));

        }

        adapter.notifyDataSetChanged();


        return root;
    }


    private void pindahPage() {

        Intent intenn = new Intent(getActivity(), Detail_Ternak.class);

        startActivity(intenn);

    }
    private void updateList() {

       adapter=new Adapter_Ternak_List(getActivity(),R.layout.costum_row_ternak,itemData);
       adapter.goCm(new Adapter_Ternak_List.goCamera() {
            @Override
            public void OnGetIdCamera(View view, int position) {
               // getIdCamera(view, position);

            }
        });

        swipelistview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void getIdCamera(View view, final int pos) {

       /* Log.e("sssss", String.valueOf(pos));
        Intent update = new Intent(getActivity(), Updat_Foto_Ternak.class);
        startActivity(update);
        getIdCamera(view, pos);*/
    }


    public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;

    }

        @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
