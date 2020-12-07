package com.example.finalproject;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
/**
 * The class is used to demonstrate Fragment
 * @author Stewart King
 */
public class CovidDetailsFragment  extends Fragment {
    /**
     * bundle to pass data
     */
    private Bundle dataFromActivity;
    /**
     * Defines Context
     */
    private AppCompatActivity parentActivity;
    /**
     * Shows listview in a fragment
     */
    private FragmentAdapter fragmentAdapter=new FragmentAdapter();
    /**
     * Stores fragment data
     */
    ArrayList<String> fragmentList=new ArrayList();

    /**
     * Required empty constructor
     */
    public CovidDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dataFromActivity = getArguments();
        //Inflate the layout for this fragment
        fragmentList = (ArrayList<String>) dataFromActivity.getSerializable("ARRAYLIST");
        View result = inflater.inflate(R.layout.activity_covid_details, container, false);
        ListView detailView = result.findViewById(R.id.details);
        detailView.setAdapter(fragmentAdapter);

        Button hideButton = (Button)result.findViewById(R.id.hide);
        hideButton.setOnClickListener( clk -> parentActivity.getSupportFragmentManager().beginTransaction().remove(this).commit());
        return result;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parentActivity = (AppCompatActivity)context;
    }

    protected class FragmentAdapter extends BaseAdapter {
        /**
         * Gets counts of items
         * @return Count of items.
         */
        @Override
        public int getCount() {
            return fragmentList.size();
        }

        /**
         * Gets item from specified position
         * @param position Position of the item
         * @return value of the specified position.
         */
        @Override
        public String getItem(int position){
            return fragmentList.get(position);
        }

        /**
         * Get View to display data of the specificed position
         * @param position of the item
         * @param old view to reuse
         * @param parent parent that the view will go to
         * @return view of the data at specific position
         */
        @Override
        public View getView(int position, View old, ViewGroup parent) {
            String sr = getItem(position);
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.activity_covid_search_result, parent, false);
            if (sr != null) {
                TextView savedView = view.findViewById(R.id.CovidSearchResult);
                savedView.setText(sr);
            }
            return view;
        }
        /**
         * Get row id of specific location on list
         * @param position of the specific item
         * @return value of the id of the item at the specified position.
         */
        @Override
        public long getItemId(int position) {
            return position;
        }
    }

}