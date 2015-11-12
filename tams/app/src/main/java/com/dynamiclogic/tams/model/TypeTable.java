package com.dynamiclogic.tams.model;

import java.util.HashMap;

/**
 * Created by Andreas on 11/5/2015.
 */
public class TypeTable extends HashMap<String,String>{
    private static TypeTable ourInstance = new TypeTable();
    //private static HashMap<String,String> typeTable = new HashMap<>();
    public static TypeTable getInstance() {
        return ourInstance;
    }

    private TypeTable() {
        this.put("Stop Sign","R1T1");
        this.put("Tree","C3P0");
        this.put("Traffic Light","44LP");
        this.put("Yield Sign","6689");
    }
}
