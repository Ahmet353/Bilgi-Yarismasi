package com.example.ahmet.milyoner;


public class Gamer {
    public  String userID;
    public  String username;
    public  String password;
    public  int point;
    public  int life;
    public  int numberLevel1;
    public  int numberLevel2;
    public  int numberLevel3;

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getImage(){
        String url=null;
        if(userID.length()>10)
            url= "https://graph.facebook.com/" + userID + "/picture?type=large";
        return  url;
    }
}
