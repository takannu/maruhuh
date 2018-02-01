package jp.co.atschool.maruhah.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import butterknife.BindView;
import jp.co.atschool.maruhah.Api.CustomTwitterApiClient;
import jp.co.atschool.maruhah.Api.UserService;
import jp.co.atschool.maruhah.R;
import jp.co.atschool.maruhah.Utils.DialogUtils;
import jp.co.atschool.maruhah.Utils.ImageUtils;

public class Activity03QuestionDetail extends AppCompatActivity {

    private String body = "";

    @BindView(R.id.ivBody) ImageView ivBody; // 質問内容
    @BindView(R.id.etAnswer) EditText etAnswer; // 回答入力枠
    @BindView(R.id.bSendAnswer) Button bSendAnswer; // 回答送信ボタン

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_03_question_detail);

        ivBody = findViewById(R.id.ivBody);
        bSendAnswer = findViewById(R.id.bSendAnswer);

        // この画面を呼び出したActivityから引き渡された値を取得する
        Intent intent = getIntent();
        body = intent.getStringExtra("body");

        Bitmap bitmap = ImageUtils.getTextSynthesisImage(getApplicationContext() ,getResources(), body);
        ivBody.setImageBitmap(bitmap);

        // 回答ボタンを押したら、ツイートと回答内容DB保存を行う。
        bSendAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // DB保存
                


                // ツイート
                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                UserService userService = new CustomTwitterApiClient(session).getUserService();

                CustomTwitterApiClient mTwitter = new CustomTwitterApiClient(session);
                mTwitter.tweet(getApplicationContext(), etAnswer.getText().toString());

                DialogUtils.generalDialog((Activity) getApplicationContext(), "回答しました。twitterを確認してみましょう。", "閉じる", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
            }
        });
    }
}
