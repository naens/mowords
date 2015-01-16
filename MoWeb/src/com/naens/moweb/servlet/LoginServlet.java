package com.naens.moweb.servlet;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeServlet;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

@WebServlet (value="/login")
public class LoginServlet extends AbstractAuthorizationCodeServlet {

	private static final long serialVersionUID = -5149933343940691595L;

	@Override
	protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
		GenericUrl url = new GenericUrl(req.getRequestURL().toString());
		url.setRawPath("/oauth2callback");
//		url.setScheme("https");
		String redirectUri = url.build();
		System.out.println("LoginServlet.getRedirectUri: " + redirectUri);
		return redirectUri;
	}

	@Override
	protected String getUserId(HttpServletRequest req) throws ServletException, IOException {
		String userId = (String) req.getServletContext().getAttribute("user_id");
		System.out.println("LoginServlet.getUserId: " + userId);
		return userId;
	}

	@Override
	protected AuthorizationCodeFlow initializeFlow() throws ServletException, IOException {
		System.out.println("LoginServlet.initializeFlow");
		Reader clientSecretReader = new InputStreamReader(LoginServlet.class.getResourceAsStream("/client_secrets.json"));
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(new JacksonFactory(), clientSecretReader);

		return new GoogleAuthorizationCodeFlow.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(),
				clientSecrets, Arrays.asList("https://www.googleapis.com/auth/plus.login", "email")).build();
	}

}
