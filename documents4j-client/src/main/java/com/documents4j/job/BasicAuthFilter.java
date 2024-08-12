package com.documents4j.job;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;

public class BasicAuthFilter implements ClientRequestFilter {
    private String user;
    private String password;

    public BasicAuthFilter(String user, String password) {
            this.user = user;
            this.password = password;
        }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        requestContext.getHeaders().add(
                HttpHeaders.AUTHORIZATION, getBasicAuthentication());
    }

    private String getBasicAuthentication() {
        String userAndPassword = this.user + ":" + this.password;
        try {
            byte[] userAndPasswordBytes = userAndPassword.getBytes("UTF-8");
            return "Basic " + Base64.getEncoder().encodeToString(userAndPasswordBytes);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

}
