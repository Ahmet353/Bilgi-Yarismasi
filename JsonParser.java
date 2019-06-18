package com.example.fatihdemirel.milyoner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class JsonParser {

    public List<Question> getJsonParseList(JSONArray jsonArray){
        String tagQuestion="question";
        String tagAnswer="questionAnswer";
        String tagOptionA="optionA";
        String tagOptionB="optionB";
        String tagOptionC="optionC";
        String tagOptionD="optionD";

        List<Question> l=new ArrayList<Question>();
        try{
            int lenght=jsonArray.length();
            for(int i=0;i<lenght;i++){
                JSONObject jsonQuestion= (JSONObject) jsonArray.get(i);
                Question q=new Question();
                q.setQuestion(jsonQuestion.getString(tagQuestion));
                q.setAnswer(jsonQuestion.getString(tagAnswer));
                q.setOptionA(jsonQuestion.getString(tagOptionA));
                q.setOptionB(jsonQuestion.getString(tagOptionB));
                q.setOptionC(jsonQuestion.getString(tagOptionC));
                q.setOptionD(jsonQuestion.getString(tagOptionD));
                l.add(q);
            }
        }
        catch (Exception e){}
        return l;
    }
}
