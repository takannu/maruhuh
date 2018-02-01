package jp.co.atschool.maruhah.Network;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nttr on 2018/01/18.
 */

public class NetworkUser {
    private String twitter_id;
    private String twitter_screen_name;

    public NetworkUser(String twitter_id, String twitter_screen_name){
        this.twitter_id = twitter_id;
        this.twitter_screen_name = twitter_screen_name;
    }
    public String getTwitterId(){
        return twitter_id;
    }
    public String getTwitterScreenName(){
        return twitter_screen_name;
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> hashmap = new HashMap<>();
        hashmap.put("twitter_id", twitter_id);
        hashmap.put("twitter_screen_name", twitter_screen_name);
        return hashmap;
    }

    public static void sendFirebaseUser(String key ,NetworkUser user){

        Map<String, Object> map = new HashMap<>();
        map.put(key, user.toMap());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(key)
                .set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("tag", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("tag", "Error writing document", e);
                    }
                });
    }
}
