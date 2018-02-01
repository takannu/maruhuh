package jp.co.atschool.maruhah.Api;

import com.twitter.sdk.android.core.models.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by nttr on 2018/01/12.
 */

public interface UserService {
    @GET("/1.1/users/show.json")
    Call<User> show(@Query("user_id") Long id,
                     @Query("screen_name") Boolean trimUser,
                     @Query("include_entities") Boolean includeMyRetweet);
}
