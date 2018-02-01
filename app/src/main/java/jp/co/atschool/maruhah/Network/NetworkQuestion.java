package jp.co.atschool.maruhah.Network;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.Exclude;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jp.co.atschool.maruhah.Models.ModelQuestionList;

/**
 * Created by nttr on 2018/01/23.
 */

public class NetworkQuestion {
    private String twitter_id; // 質問された側のユニーク値
    private String body; // 質問内容
    private Timestamp created_date; // 質問した日

    public NetworkQuestion(String body, String twitter_id){
        this.twitter_id = twitter_id;
        this.body = body;
    }
    public String getTwitter_id(){
        return twitter_id;
    }
    public String getBody(){
        return body;
    }
    public Timestamp getCreated_date(){
        return created_date;
    }

    @Exclude
    public Map<String, Object> sendQuestionMap(){
        HashMap<String, Object> hashmap = new HashMap<>();
        hashmap.put("twitter_id", twitter_id);
        hashmap.put("body", body);
        hashmap.put("read", 0);
        hashmap.put("created_date", FieldValue.serverTimestamp());
        return hashmap;
    }

    public static void sendFirebaseQuestion(String key ,NetworkQuestion question){

//        Map<String, Object> map = new HashMap<>();
//        map.put(key, question.toMap());// keyをタイムスタンプにする！　と思いましたが、やめました。

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("questions").document()
                .set(question.sendQuestionMap())
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

    public static void getFirebaseQuestionList(final String twitter_id , final NetworkUtilListener networkUtilListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final ArrayList<ModelQuestionList> arrayList = new ArrayList<>();
        db.collection("questions")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int i = 0;
                            for (DocumentSnapshot document : task.getResult()) {

                                Log.d("tag", document.getId());

                                arrayList.add(document.toObject(ModelQuestionList.class));
                                arrayList.get(i).setDocument_key(document.getId());
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