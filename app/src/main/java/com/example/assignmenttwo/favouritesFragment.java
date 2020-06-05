package com.example.assignmenttwo;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

public class favouritesFragment extends Fragment
{
    RecyclerView favouriteRecycler ;
    favouritesAdapter favouritesAdapter;

    MapsActivity mapsActivity = new MapsActivity();
//    Integer[] imageList;
    String[] namesList;

    ArrayList<com.example.assignmenttwo.favouritesData> data = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater favInflater, ViewGroup container, Bundle savedInstanceState)
    {

        super.onCreateView(favInflater, container, savedInstanceState);
        View root = favInflater.inflate(R.layout.favourites_fragment, container, false);

        favouriteRecycler = root.findViewById(R.id.favouritesRecycler);

        favouritesAdapter = new favouritesAdapter(data, getActivity().getBaseContext(), mapsActivity.siteFrag, mapsActivity.favFrag);

        favouriteRecycler.setAdapter(favouritesAdapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        favouriteRecycler.setLayoutManager(layoutManager);

        for(int i = 0; i < mapsActivity.favs.size(); i++)
        {
            String name = mapsActivity.favs.get(i);
            int image = mapsActivity.imagesList.get(i).getResourceId(i, -1);
            com.example.assignmenttwo.favouritesData favouritesData = new com.example.assignmenttwo.favouritesData(name,i, image);
            data.add(favouritesData);
        }




        return root;
    }
}
