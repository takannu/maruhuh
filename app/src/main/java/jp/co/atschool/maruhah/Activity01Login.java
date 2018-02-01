package jp.co.atschool.maruhah;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import jp.co.atschool.maruhah.Api.CustomTwitterApiClient;
import jp.co.atschool.maruhah.Api.UserService;
import jp.co.atschool.maruhah.Network.NetworkUser;
import retrofit2.Call;

public class Activity01Login extends AppCompatActivity {

    private TwitterLoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_01_login);

        loginButton = (TwitterLoginButton) findViewById(R.id.login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls

                Toast toast = Toast.makeText(Activity01Login.this, "ログイン成功", Toast.LENGTH_LONG);
                toast.show();

                // ログインが成功したら、firabaseDBに登録する
                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                UserService userService = new CustomTwitterApiClient(session).getUserService();

                Call<User> calls = userService.show(session.getId(), null, null);
                calls.enqueue(new Callback<User>() {
                    @Override
                    public void success(Result<User> result) {
                        // 登録
                        NetworkUser.sendFirebaseUser(result.data.screenName, new NetworkUser(result.data.idStr,result.data.screenName));
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

                Toast toast = Toast.makeText(Activity01Login.this, "ログイン失敗", Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the login button.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }
}
