package jp.co.atschool.maruhah.Network;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.Exclude;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jp.co.atschool.maruhah.Models.ModelUserList;

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
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document()
                .set(user.toMap())
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

    public static void getFirebaseUserList(final String screen_name, final NetworkUserUtilListener networkUtilListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final ArrayList<ModelUserList> arrayList = new ArrayList<>();
        db.collection("users")
                .whereEqualTo("twitter_screen_name", screen_name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int i = 0;
                            for (DocumentSnapshot document : task.getResult()) {
                                arrayList.add(document.toObject(ModelUserList.class));
//                                arrayList.get(i).set(document.getId());
                                i++;
                            }
                            networkUtilListener.OnSuccess(arrayList);
                        } else {
                            Log.d("tag", "エラー: ", task.getException());
                        }
                    }
                });
    }
}
