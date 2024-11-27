package io.Manager;

import cucumber.api.Scenario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagMap {

    public Map<String, Boolean> tagMap;
    public Map<String,String> tagMapName;
    private static TagMap instance = null;
    private int TestIdCounter;
    List<String> testIds = new ArrayList<>();

    private TagMap(){
        tagMapName = new HashMap<>();
        tagMap = new HashMap<>();
    }

    public static TagMap getInstance(){
        if(instance == null){
            instance = new TagMap();
        }
        return instance;
    }

    public void tagMapping(final Scenario scenario){
        this.tagMap.put("Rest",false);
        this.tagMap.put("Test_Id",false);
        for(String tag : scenario.getSourceTagNames()){
            String tagName = tag.replace("@","").trim();
            if(tag.toLowerCase().equalsIgnoreCase("@test_id")){
                this.tagMap.replace("Test_Id",true);
                this.tagMapName.put("Test_Id",tagName);
            } else if(tag.toLowerCase().equalsIgnoreCase("rest")){
                this.tagMap.replace("Test_Id",true);
                this.tagMapName.put("Test_Id",tagName);
            }
        }
    }

    public int getCounterOfTestId(){
        return TestIdCounter;
    }

    public String getCurrentTestId(int counter){
        return testIds.get(counter);
    }

    public void setTagMapName(Map<String,String> tagMapName){
        this.tagMapName = tagMapName;
    }

    public Map<String,String> getTagMapName(){
        return tagMapName;
    }

    public void setTagMap(Map<String,Boolean> tagMap){
        this.tagMap = tagMap;
    }

    public Map<String,Boolean> getTagMap(){
        return tagMap;
    }
}
