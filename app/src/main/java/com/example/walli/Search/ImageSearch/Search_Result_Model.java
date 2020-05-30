package com.example.walli.Search.ImageSearch;

public class Search_Result_Model {
    private String object_name;
    private float object_confidence;

    public Search_Result_Model(String object_name, float object_confidence) {
        this.object_name = object_name;
        this.object_confidence = object_confidence;
    }

    public String getObject_name() {
        return object_name;
    }

    public double getObject_confidence() {
        return object_confidence;
    }
}
