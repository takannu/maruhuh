package jp.co.atschool.maruhah.Models;

import java.util.Date;

/**
 * Created by nttr on 2018/01/29.
 */

public class ModelQuestionList {
    private Date created_date;
    private String body;
    private String twitter_id;
    private String document_key;

    public ModelQuestionList() {
    }

    public ModelQuestionList(Date created_date, String body, String twitter_id) {
        this.created_date = created_date;
        this.body = body;
        this.twitter_id = twitter_id;
    }

    public Date getCreated_date() {
        return created_date;
    }

    public String getBody() {
        return body;
    }

    public String getTwitter_id() {
        return twitter_id;
    }

    public String getDocument_key() { return document_key; }

    public void setDocument_key(String document_key) { this.document_key = document_key; }
}
