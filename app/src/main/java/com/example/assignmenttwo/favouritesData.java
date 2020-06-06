package com.example.assignmenttwo;

import android.content.res.TypedArray;
import android.util.TypedValue;

import java.lang.reflect.Type;
//Favourites data class
//Gets and sets the data
public class favouritesData
{
    private int id;
    private String name;
    private int image;

    public favouritesData(String name, int id, int image)
    {
        this.image = image;
        this.name = name;
        this.id = id;
    }

    public int getImage()
    {
        return image;



    }

    public void setImage(int image)
    {
        this.image = image;
    }

    public int getId()
    {
        return id;

    }
    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
