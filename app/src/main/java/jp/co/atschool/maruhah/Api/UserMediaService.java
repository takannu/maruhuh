package jp.co.atschool.maruhah.Api;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.models.Media;

import retrofit.mime.TypedFile;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UserMediaService {
    @Multipart
    @POST("/1.1/media/upload.json")
    void upload(@Part("media") TypedFile media, Callback<Media> cb);
}
