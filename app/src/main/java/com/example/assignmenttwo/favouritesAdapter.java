package com.example.assignmenttwo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class favouritesAdapter extends RecyclerView.Adapter<favouritesAdapter.ViewHolder>
{
    private ArrayList<com.example.assignmenttwo.favouritesData> data;

    private Context c;

    private View siteFrag;

    private View favFrag;


    public favouritesAdapter(ArrayList<com.example.assignmenttwo.favouritesData> data, Context context, View siteFrag, View favFrag)
    {
        this.c = context;
        this.data = data;
        this.siteFrag = siteFrag;
        this.favFrag = favFrag;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView nameText;
        public ImageView image;

        public ViewHolder(View itemView)
        {
            super(itemView);

            nameText = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.siteImageRecycler);

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(c).inflate(R.layout.favourites_recycler_layout, parent, false);

        return new favouritesAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position)
    {

        holder.image.setImageResource(data.get(position).getImage());
        holder.nameText.setText(data.get(position).getName());


    }

    @Override
    public int getItemCount() {
        return data.size();
    }


}
