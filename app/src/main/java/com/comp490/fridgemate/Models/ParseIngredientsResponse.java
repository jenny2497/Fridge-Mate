package com.comp490.fridgemate.Models;

import java.util.ArrayList;

public class ParseIngredientsResponse {
    public int id;
    public String original;
    public String originalName;
    public String name;
    public String nameClean;
    public double amount;
    public String unit;
    public String unitShort;
    public String unitLong;
    public ArrayList<String> possibleUnits;
    public EstimatedCost estimatedCost;
    public String consistency;
    public String aisle;
    public String image;
    public ArrayList<String> meta;
    public Nutrition nutrition;
}
