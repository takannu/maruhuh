package jp.co.atschool.maruhah;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import jp.co.atschool.maruhah.Api.CustomTwitterApiClient;
import jp.co.atschool.maruhah.Api.UserService;
import jp.co.atschool.maruhah.Fragment.Fragment01Top;
import jp.co.atschool.maruhah.Fragment.Fragment02History;
import jp.co.atschool.maruhah.Network.NetworkUser;
import jp.co.atschool.maruhah.Network.NetworkUserCheckUtilListener;
import retrofit2.Call;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // ここにActivity全体で使うクラスや変数を書く
    private TextView tvFooterTop;
    private TextView tvFooterHistory;
    private View viewInterceptor;

    private Fragment01Top mFragment01Top;
    private Fragment02History mFragment02History;

    private FirebaseAnalytics mFirebaseAnalytics;

    private TwitterLoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // twitterのログイン状況を判断する。
        twitters();
        // firebaseのanalyticsをセット
        firebases();

        if (TwitterCore.getInstance().getSessionManager().getActiveSession() == null) {
            // Login画面にする
            setContentView(R.layout.activity_01_login);

            loginButton = (TwitterLoginButton) findViewById(R.id.login_button);
            loginButton.setCallback(new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    // Do something with result, which provides a TwitterSession for making API calls

                    Toast toast = Toast.makeText(MainActivity.this, "ログイン成功", Toast.LENGTH_LONG);
                    toast.show();

                    // ログインが成功したら、firabaseDBに登録する
                    TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                    UserService userService = new CustomTwitterApiClient(session).getUserService();

                    Call<User> calls = userService.show(session.getId(), null, null);
                    calls.enqueue(new Callback<User>() {
                        @Override
                        public void success(final Result<User> result) {

                            NetworkUser.getFirebaseUser(result.data.idStr,new NetworkUserCheckUtilListener() {
                                @Override
                                public void OnSuccess(String twitter_id) {
                                    // 存在していたら、登録しない
                                    if (twitter_id == "zumi") {
                                        // 通常画面にする
                                        setTop();
                                    } else {
                                        // 登録
                                        NetworkUser.sendFirebaseUser(result.data.screenName, new NetworkUser(result.data.idStr, result.data.screenName));

                                        // 通常画面にする
                                        setTop();
                                    }
                                }
                            });
                        }

                        public void failure(TwitterException exception) {
                            //Do something on failure
                            Log.d("LOG", "失敗。。");
                        }
                    });
                }

                @Override
                public void failure(TwitterException exception) {
                    // Do something on failure

                    Toast toast = Toast.makeText(MainActivity.this, "ログイン失敗", Toast.LENGTH_LONG);
                    toast.show();
                }
            });
        } else {
            // 通常画面にする
            setTop();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void setTop(){
        // 通常画面にする
        setContentView(R.layout.activity_main);
        // footerの各ボタンを有効化
        setButtons();
        // defaultで選択されているfooterをセット
        switchTab(R.id.tvFooterTop);

        Toast toast = Toast.makeText(MainActivity.this, "ログイン中", Toast.LENGTH_LONG);
        toast.show();
    }

    private void setButtons(){
        tvFooterTop = (TextView)findViewById(R.id.tvFooterTop);
        tvFooterTop.setOnClickListener(this);
        tvFooterHistory = (TextView)findViewById(R.id.tvFooterHistory);
        tvFooterHistory.setOnClickListener(this);
        viewInterceptor = (View)findViewById(R.id.viewInterceptor);

        // このViewは何に使うかわからないので、一旦非表示にする。
        viewInterceptor.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View view) {
        int viewID = view.getId();
        switch (viewID){
            case R.id.tvFooterTop:
                switchTab(viewID);
                break;
            case R.id.tvFooterHistory:
                switchTab(viewID);
                break;
            default:
                break;
        }
    }

    public void switchTab(int viewID) {
        // footerをタップすると該当の画像の色を変更する。(塗りつぶす)
        View[] footer = new View[]{tvFooterTop, tvFooterHistory};
        for(int i=0; i<footer.length; i++){
            footer[i].setSelected(viewID == footer[i].getId());
        }

        // 選択したタブのフラグメントを表示する。
        Fragment fragment;
        switch (viewID){
            case R.id.tvFooterTop:
                if(null == mFragment01Top) mFragment01Top = new Fragment01Top();
                fragment = mFragment01Top;
                break;
            case R.id.tvFooterHistory:
                mFragment02History = new Fragment02History();
                fragment = mFragment02History;
                break;
            default:
                if(null == mFragment01Top) mFragment01Top = new Fragment01Top();
                fragment = mFragment01Top;
                break;
        }
        replaceFragment(fragment);
    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.flMain, fragment);
        fragmentTransaction.commit();
    }

    public void twitters() {
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig("Vfz0NgAgWfWfyuntSwqRBxkup", "TbuKva8G3VosXEGUUMfOUIdP2gAm3SseO8Vxoexl2S9kncncyr"))
                .debug(true)
                .build();
        Twitter.initialize(config);
    }

    public void firebases() {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the login button.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }
}
