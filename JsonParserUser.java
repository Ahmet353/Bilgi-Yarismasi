package com.example.fatihdemirel.milyoner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class JsonParserUser {
    String tagID = "id";
    String tagUserName = "userName";
    String tagPassword = "userPassword";
    String tagPoint = "point";
    String tagLife = "life";
    String tagNumberLevel1="numberlevel1";
    String tagNumberLevel2="numberlevel2";
    String tagNumberLevel3="numberlevel3";

    public Gamer getJsonParse(JSONArray jsonArray) {
        Gamer user = new Gamer();
        try {
            int lenght = jsonArray.length();
            for (int i = 0; i < lenght; i++) {
                JSONObject jsonQuestion = (JSONObject) jsonArray.get(i);
                user.userID = (jsonQuestion.getString(tagID));
                user.username = (jsonQuestion.getString(tagUserName));
                user.password = (jsonQuestion.getString(tagPassword));
                user.point = (jsonQuestion.getInt(tagPoint));
                user.life = (jsonQuestion.getInt(tagLife));
                user.numberLevel1=jsonQuestion.getInt(tagNumberLevel1);
                user.numberLevel2=jsonQuestion.getInt(tagNumberLevel2);
                user.numberLevel3=jsonQuestion.getInt(tagNumberLevel3);
            }
        } catch (Exception e) {
        }
        return user;
    }

    public List<Gamer> getJsonParseList(JSONArray jsonArray) {
        List<Gamer> list = new ArrayList<Gamer>();
        try {
            int lenght = jsonArray.length();
            for (int i = 0; i < lenght; i++) {
                Gamer user = new Gamer();
                JSONObject jsonQuestion = (JSONObject) jsonArray.get(i);
                user.userID = (jsonQuestion.getString(tagID));
                user.username = (jsonQuestion.getString(tagUserName));
                user.password = (jsonQuestion.getString(tagPassword));
                user.point = (jsonQuestion.getInt(tagPoint));
                user.life = (jsonQuestion.getInt(tagLife));
                list.add(user);
            }
        } catch (Exception e) {
        }
        return list;
    }
}
