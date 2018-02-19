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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jp.co.atschool.maruhah.Models.ModelQuestionList;

/**
 * Created by nttr on 2018/01/23.
 */

public class NetworkQuestion {
    private String twitter_id; // 褒められた側のユニーク値
    private String body; // 褒め内容

    public NetworkQuestion(String body, String twitter_id){
        this.twitter_id = twitter_id;
        this.body = body;
    }

    @Exclude
    public Map<String, Object> sendQuestionMap(){
        HashMap<String, Object> hashmap = new HashMap<>();
        hashmap.put("twitter_id", twitter_id);
        hashmap.put("body", body);
//        hashmap.put("answer", " ");
        hashmap.put("read", 0);
        hashmap.put("created_date", FieldValue.serverTimestamp());
        return hashmap;
    }

    public static void sendFirebaseQuestion(String key ,NetworkQuestion question){
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

    public static void getFirebaseQuestionList(final String twitter_id , final Integer read, final NetworkUtilListener networkUtilListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final ArrayList<ModelQuestionList> arrayList = new ArrayList<>();
        db.collection("questions")
                .whereEqualTo("twitter_id", twitter_id)
                .whereEqualTo("read", read)
                .orderBy("created_date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int i = 0;
                            for (DocumentSnapshot document : task.getResult()) {
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
