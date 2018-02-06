package jp.co.atschool.maruhah.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import butterknife.BindView;
import jp.co.atschool.maruhah.Api.CustomTwitterApiClient;
import jp.co.atschool.maruhah.R;
import jp.co.atschool.maruhah.Utils.DialogUtils;
import jp.co.atschool.maruhah.Utils.ImageUtils;

public class Activity03QuestionDetail extends AppCompatActivity {

    private String body = "";
    private String document_key = "";

    @BindView(R.id.ivBody) ImageView ivBody; // 質問内容
    @BindView(R.id.etAnswer) EditText etAnswer; // 回答入力枠
    @BindView(R.id.bSendAnswer) Button bSendAnswer; // 回答送信ボタン

    private Activity03QuestionDetail activity03QuestionDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_03_question_detail);

        activity03QuestionDetail = this;

        ivBody = findViewById(R.id.ivBody);
        etAnswer = findViewById(R.id.etAnswer);
        bSendAnswer = findViewById(R.id.bSendAnswer);

        // この画面を呼び出したActivityから引き渡された値を取得する
        Intent intent = getIntent();
        body = intent.getStringExtra("body");
        document_key = intent.getStringExtra("document_key");

        Bitmap bitmap = ImageUtils.getTextSynthesisImage(getApplicationContext() ,getResources(), body);
        ivBody.setImageBitmap(bitmap);

        // 回答ボタンを押したら、ツイートと回答内容DB保存を行う。
        bSendAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // DB保存
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference washingtonRef = db.collection("questions").document(document_key);
                washingtonRef
                        .update("answer", etAnswer.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("tag", "DocumentSnapshot successfully updated!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("tag", "Error updating document", e);
                            }
                        });

                // ツイート
                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();

                String twiit_body = "\nQ. " + body + "\n\nA. " + etAnswer.getText().toString() + "\n";

                CustomTwitterApiClient mTwitter = new CustomTwitterApiClient(session);
                mTwitter.tweet(getApplicationContext(), twiit_body);

                DialogUtils.generalDialog(activity03QuestionDetail, "回答しました。twitterを確認してみましょう。", "閉じる", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
            }
        });
    }
}
