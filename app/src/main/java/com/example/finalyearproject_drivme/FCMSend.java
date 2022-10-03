package com.example.finalyearproject_drivme;

import android.content.Context;
import android.os.StrictMode;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FCMSend {
    private static String BASE_URL = "https://fcm.googleapis.com/fcm/send";
    private static String SERVER_KEY = "key=AAAARqlCGio:APA91bFzPPZQZnzaeFLW_XZBwenYBYUK9M9Yz3jV4yfmI-gT0qQq_GslpGsn5arc2aQxGUo4_PSU5_z5F9U1c9WwrOZV83FED0PDj_ZRqPB4lOtn-u3LkNBGge6dClXvayP8YWJqKW25";

    public static void pushNotification(Context context, String token, String title, String text){
        StrictMode.ThreadPolicy tPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(tPolicy);

        RequestQueue rQueue = Volley.newRequestQueue(context);

        try {
            JSONObject sendTo = new JSONObject();
            JSONObject notificationContent = new JSONObject();
            sendTo.put("to", token);
            notificationContent.put("title", title);
            notificationContent.put("body", text);
            sendTo.put("notification", notificationContent);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, BASE_URL, sendTo, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> param = new HashMap<>();
                    param.put("Content-Type", "application/json");
                    param.put("Authorization", SERVER_KEY);
                    return param;
                }
            };

            rQueue.add(jsonObjReq);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
