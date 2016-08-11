package com.example.owlslubic.oauthlabroundii;

/**
 * Created by owlslubic on 8/10/16.
 */
public class TwitterAPIResponse {
    String text;
    String created_at;

    public TwitterAPIResponse() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
