package com.example.walli;

public class Image_Object {
    private String id;
    private String name,username,url;

    public Image_Object(String id,String url, String name, String username) {
        this.id = id;
        this.url = url;
        this.name = name;
        this.username = username;
    }

    public String getUrl() {
        return url;
    }

    public String getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }



}
