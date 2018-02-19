package jp.co.atschool.maruhah.Api;

/**
 * Created by nttr on 2018/01/12.
 */

import android.content.Context;
import android.widget.Toast;

import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Media;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.MediaService;
import com.twitter.sdk.android.core.services.StatusesService;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomTwitterApiClient extends TwitterApiClient {
    public CustomTwitterApiClient(TwitterSession session) {
        super(session);
    }

    public UserService getUserService() {
        return getService(UserService.class);
    }

    public UserMediaService getUserMediaService() {
        return getService(UserMediaService.class);
    }

    public void tweet(final Context context, String message) {
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        StatusesService statusesService = twitterApiClient.getStatusesService();

        Call<Tweet> call = statusesService.update(message, null, false, null, null, null, false, false, null);
        call.enqueue(new Callback<Tweet>() {
            @Override
            public void onResponse(Call<Tweet> call, Response<Tweet> response) {
                Toast.makeText(context, "post success", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Tweet> call, Throwable t) {
                Toast.makeText(context, "post fail", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void media_upload(final Context context, String message) {
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        MediaService mediaService = twitterApiClient.getMediaService();

        File file1 = new File("path/to/your/file1");
        RequestBody requestBody1 = RequestBody.create(MediaType.parse("multipart/form-data"), file1);

        Call<Media> call = mediaService.upload(requestBody1,requestBody1,requestBody1);

        call.enqueue(new Callback<Media>() {
            @Override
            public void onResponse(Call<Media> call, Response<Media> response) {
                Toast.makeText(context, "post success", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Media> call, Throwable t) {
                Toast.makeText(context, "post fail", Toast.LENGTH_LONG).show();
            }
        });
    }
}
