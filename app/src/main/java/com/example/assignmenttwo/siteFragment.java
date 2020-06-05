package com.example.assignmenttwo;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.model.Marker;

import java.lang.reflect.Array;
import java.util.Objects;

public class siteFragment extends Fragment {
    private TypedArray images;
    private String[] names;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);



        Resources res = getResources();
        images = res.obtainTypedArray(R.array.images);
        names = res.getStringArray(R.array.names);

        View root1 = inflater.inflate(R.layout.site_fragment, container, false);



//            image.setImageResource(R.drawable.campsite);



        return root1;
    }

}
