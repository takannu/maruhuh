package jp.co.atschool.maruhah.Fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.atschool.maruhah.Api.CustomTwitterApiClient;
import jp.co.atschool.maruhah.Api.UserService;
import jp.co.atschool.maruhah.Network.NetworkQuestion;
import jp.co.atschool.maruhah.Network.NetworkUser;
import jp.co.atschool.maruhah.R;
import jp.co.atschool.maruhah.Utils.AsyncImageView;
import jp.co.atschool.maruhah.Utils.DialogUtils;
import jp.co.atschool.maruhah.Utils.ImageUtils;
import retrofit2.Call;

public class Fragment01Top extends Fragment {
    private Activity mActivity;

    private String twitter_screen_name;
    private String twitter_id;

    @BindView(R.id.ibTweetOpen) ImageButton ibTweetOpen; // 質問を募集する
    @BindView(R.id.tvUserName) TextView tvUserName; // ユーザ名
    @BindView(R.id.tvUserId) TextView tvUserId; // twitter screen_name
    @BindView(R.id.ivMyProfile) ImageView ivMyProfile; // twitterプロフィール画像
    @BindView(R.id.etQuestion) EditText etQuestion; // 質問入力フィールド
    @BindView(R.id.bSendQuestion) Button bSendQuestion; // 質問を送るボタン
    @BindView(R.id.ivTestImage) ImageView ivTestImage; //

    // ライフサイクル1回目に呼び出される。
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // MainActivityを呼び出し、セットする。
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // このクラスをMainActivityにセットする(多分)
        View view = inflater.inflate(R.layout.fragment_01_top, container, false);
        ButterKnife.bind(this, view);

        mActivity.setTitle("Maruhah");

        ibTweetOpen = (ImageButton) view.findViewById(R.id.ibTweetOpen);
        tvUserName = (TextView) view.findViewById(R.id.tvUserName);
        ivMyProfile = (ImageView) view.findViewById(R.id.ivMyProfile);
        tvUserId = (TextView) view.findViewById(R.id.tvUserId);
        etQuestion = (EditText) view.findViewById(R.id.etQuestion);
        bSendQuestion = (Button) view.findViewById(R.id.bSendQuestion);
        ivTestImage = (ImageView) view.findViewById(R.id.ivTestImage);

        // 質問募集ボタンを押したら、ツイートを行う。
        ibTweetOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                UserService userService = new CustomTwitterApiClient(session).getUserService();

                CustomTwitterApiClient mTwitter = new CustomTwitterApiClient(session);
                mTwitter.tweet(getContext(), "なんでも質問して下さい。");

                DialogUtils.generalDialog(getActivity(), "ツイートしました。", "閉じる", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
            }
        });

        // 質問を送ったら、CloudFirestoreにデータを送る
        bSendQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NetworkQuestion.sendFirebaseQuestion(twitter_id, new NetworkQuestion(etQuestion.getText().toString(),twitter_id));

                DialogUtils.generalDialog(getActivity(), "質問を送りました。", "閉じる", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
            }
        });

        Bitmap bitmap = ImageUtils.getTextSynthesisImage(getContext() ,getResources(), "これから朝ごはんを食べますが、何がオススメですか？\nあと、これからジョギングに出かけるのですが、汗をかかないためにスポーツウェアをきて行こうと考えています。スポーツウェアが売っているオススメなチェーン店は東京都品川区にありますか？");

        ivTestImage.setImageBitmap(bitmap);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // クラスをMainActivityにセットする(多分)
        super.onViewCreated(view, savedInstanceState);
        setInformation();

        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        UserService userService = new CustomTwitterApiClient(session).getUserService();

        Call<User> calls = userService.show(session.getId(), null, null);
        calls.enqueue(new Callback<User>() {
            @Override
            public void success(Result<User> result) {

                twitter_id = result.data.idStr;
                twitter_screen_name = result.data.screenName;

                tvUserName.setText(result.data.name);
                tvUserId.setText("@" + twitter_screen_name + "の質問箱");

                // 画像が荒いので、normalという文字を消す。
                String profile_image_url = result.data.profileImageUrlHttps;
                profile_image_url = profile_image_url.replace("_normal", "");
                new AsyncImageView(ivMyProfile)
                        .execute(profile_image_url);

                // DBにuser情報を取得する。
                NetworkUser.sendFirebaseUser(twitter_id, new NetworkUser(twitter_id,twitter_screen_name));
            }

            public void failure(TwitterException exception) {
                //Do something on failure
                Log.d("LOG", "失敗。。");
            }
        });
    }

    private void setInformation(){
        // MainActivityの関数を呼び出す例
//        ((MainActivity)mActivity).showInterceptor();
    }
}