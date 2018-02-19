package jp.co.atschool.maruhah.Models;

/**
 * Created by nttr on 2018/01/29.
 */

public class ModelUserList {
    private String twitter_id = "";
    private String twitter_screen_name = "";

    public ModelUserList() {
    }

    public ModelUserList(String twitter_id, String twitter_screen_name) {
        this.twitter_id = twitter_id;
        this.twitter_screen_name = twitter_screen_name;
    }

    public String getTwitter_id() {
        return twitter_id;
    }

    public String getTwitter_screen_name() { return twitter_screen_name; }

}
