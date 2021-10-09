package com.example.android.myapplication;

public class Notes {

    public String title;
    public String data;
    public String time;
    public String user_id;

    public Notes( String db_title, String db_data, String db_time,String db_userid) {

        this.title = db_title;
        this.data = db_data;
        this.time = db_time;
        this.user_id = db_userid;
    }

    public Notes() {

    }

    public String getTitle() {
        return title;
    }

    public String getData() {
        return data;
    }

    public String getTime() {
        return time;
    }

    public String getUser_id() {
        return user_id;
    }
}
