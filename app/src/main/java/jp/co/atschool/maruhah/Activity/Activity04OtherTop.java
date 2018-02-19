package jp.co.atschool.maruhah.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;

import butterknife.BindView;
import jp.co.atschool.maruhah.Api.CustomTwitterApiClient;
import jp.co.atschool.maruhah.Api.UserService;
import jp.co.atschool.maruhah.Network.NetworkQuestion;
import jp.co.atschool.maruhah.R;
import jp.co.atschool.maruhah.Utils.AsyncImageView;
import jp.co.atschool.maruhah.Utils.DialogUtils;
import jp.co.atschool.maruhah.Utils.LemonProgressDialog;
import jp.co.atschool.maruhah.configs.Define;
import retrofit2.Call;

public class Activity04OtherTop extends AppCompatActivity {

    @BindView(R.id.tvScreenName)
    TextView tvScreenName;

    @BindView(R.id.ivMyProfile)
    ImageView ivMyProfile;

    @BindView(R.id.bSendQuestion)
    Button bSendQuestion;

    @BindView(R.id.etQuestion)
    EditText etQuestion;

    private LemonProgressDialog progressDialog;
    private String twitter_id;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity04_other_top);

        mActivity = this;

        // 前のページからの情報をセットしておく。
        Intent intent = getIntent();
        twitter_id = intent.getStringExtra("twitter_id");

        // インジゲータ
        progressDialog = new LemonProgressDialog(this);

        tvScreenName = (TextView) findViewById(R.id.tvScreenName);
        ivMyProfile = (ImageView) findViewById(R.id.ivMyProfile);
        bSendQuestion = (Button) findViewById(R.id.bSendQuestion);
        etQuestion = (EditText) findViewById(R.id.etQuestion);

        // 質問を送ったら、CloudFirestoreにデータを送る
        bSendQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NetworkQuestion.sendFirebaseQuestion(twitter_id, new NetworkQuestion(etQuestion.getText().toString(),twitter_id));

                DialogUtils.generalDialog(mActivity, "褒め文を送りました。\n相手のお礼を待ちましょう。", "閉じる", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
            }
        });

        reloadView(Define.ReloadType.Overall);
    }

    public void reloadView(Define.ReloadType refreshType) {

        if (refreshType == Define.ReloadType.Overall) {
            progressDialog.show();
        }

        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        UserService userService = new CustomTwitterApiClient(session).getUserService();

        progressDialog.show(); // イン時ゲータ

        Long twitter_id_long = Long.parseLong(twitter_id);

        Call<User> calls = userService.show(twitter_id_long, null, null);
        calls.enqueue(new Callback<User>() {
            @Override
            public void success(Result<User> result) {

                tvScreenName.setText(result.data.screenName + "の褒め場");

                // 画像が荒いので、normalという文字を消す。
                String profile_image_url = result.data.profileImageUrlHttps;
                profile_image_url = profile_image_url.replace("_normal", "");
                new AsyncImageView(ivMyProfile)
                        .execute(profile_image_url);

                progressDialog.dismiss();
            }

            public void failure(TwitterException exception) {
                //Do something on failure
                Log.d("LOG", "失敗。。");
                progressDialog.dismiss();
            }
        });
    }
}
