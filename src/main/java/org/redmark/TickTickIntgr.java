package org.redmark;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

public class TickTickIntgr {
    public static ArrayList<String> getTasks() throws Exception{
        String url = "https://api.ticktick.com/open/v1/project/67a3c286507ac343cb16047e/data";
        String auth_key = "3a82edc2-e009-476a-bc12-3f4ec326a94e";
        ArrayList<String> taskList = new ArrayList<>();
        HttpResponse<String> reposne =
         Unirest.get(url).header("Authorization", "Bearer "+ auth_key).asString();
        //ystem.err.println(reposne.getBody());
         JSONObject jo = new JSONObject(reposne.getBody());
         JSONArray ja = jo.getJSONArray("tasks");
         ja.forEach(task->{
            JSONObject taskJson = new JSONObject(task.toString());
            String title = taskJson.getString("title");
            //System.out.println("Task Title => " + title);
            taskList.add(title);
        });

         return taskList;
    }
    
}
