package com.example.petcare;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonParser {
    private HashMap<String,String> parseJsonObject(JSONObject object){
        //intialize hashmap
        HashMap<String,String>dataList=new HashMap<>();
        try {
            //get name from object
            String name =object.getString("name");
            //get latitude from the object
            String latitude = object.getJSONObject("geometry").getJSONObject("location").getString("lat");
            //get longitude from object
            String longitude =object.getJSONObject("geometry").getJSONObject("location").getString("lng");
            //put all value in hashmap
            dataList.put("name",name);
            dataList.put("lat",latitude);
            dataList.put("lng",longitude);
        }

        catch (JSONException e){
            e.printStackTrace();
        }
        return dataList;
    }
    private List<HashMap<String,String>> parseJsonArray(JSONArray jsonArray){
        //initialize hashmap list
        List<HashMap<String,String>> dataList=new ArrayList<>();
        for(int i=0;i<jsonArray.length();i++){
            //initialize hashmap
            try {
                HashMap<String,String>data=parseJsonObject((JSONObject)jsonArray.get(i));
                //add data
                dataList.add(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return dataList;
    }
    public  List<HashMap<String,String>> parseResult(JSONObject object){
        //intialize json array
        JSONArray jsonArray=null;
        //get result
        try {
            jsonArray=object.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parseJsonArray(jsonArray);
    }

}
