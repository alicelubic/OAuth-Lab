package com.example.owlslubic.oauthlabroundii;

/**
 * Created by owlslubic on 8/10/16.
 */
public class TwitterOAuthResponse {
    //GSON OBJECT

    //we need two strings: token type, and access token because that's what's being returned in our request
    String token_type, access_token;

    public TwitterOAuthResponse() {
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
}
