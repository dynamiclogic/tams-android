package com.dynamiclogic.tams.model;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import static com.dynamiclogic.tams.model.TypeE.valueOf;

/**
 * Created by Andreas on 11/5/2015.
 */
public class Type {
    private String key;//say stopsign
    private String value;//value string of stopsign "R1J4"
    //private TypeDictionary sType;
    //Create singelton HashMap...maybe
    //private static HashMap<String,String> typeTable = new HashMap<>();
   // private TypeTable typeTable = TypeTable.getInstance();
   //private TypeDictionary typeTable; // = new TypeDictionary();
    //private EnumSet dictionary = EnumSet.allOf(TypeE.class);
    private static List<String> listType;
    private TypeE type;

    public static List<String> createList(){
        listType = new ArrayList<>();
        for(TypeE thingy: TypeE.values()){
            listType.add(thingy.getType());

        }
        return listType;
    }

    public TypeE getType() {
        return type;
    }

    public void setType(TypeE type) {
        this.type = type;
    }

    public Type(){
        this.key = "XXX";
        this.value = "XXXX";
    }

    public Type(String key) {

        this.key = key;
        this.type = TypeE.vvalueOf(key);
       //this.value = typeTable.valueOf(key).toString();
    }

    public String getKey(){
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName(){
        return type.name().toString();
    }


    enum TypeE{
        RJ34("Stop Sign"),
        D334("Yield Sign"),
        HR83("Tree"),
        EEP("Traffic Light"),
        XXXY("XXXX");

        //(C3P0), C3P0;

        private String type;
        //private String type;
        private static Map<String,TypeE> map = new HashMap<String,TypeE>();

        static{
            for(TypeE t: TypeE.values()){
                map.put(t.type,t);
            }
        }
        TypeE(String s) {
            type = s;
        }
        //private String code;
        //private EnumSet<TypeDictionary> set = EnumSet.allOf(TypeDictionary.class);
        // private


        private static TypeE vvalueOf(String type){
            return map.get(type);
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }



    }



}
