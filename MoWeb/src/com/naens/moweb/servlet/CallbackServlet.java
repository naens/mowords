package com.naens.moweb.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeCallbackServlet;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gson.Gson;
import com.naens.moweb.dao.UserDao;
import com.naens.moweb.model.GoogleProfile;
import com.naens.moweb.model.User;

@WebServlet (value="/oauth2callback")
public class CallbackServlet extends AbstractAuthorizationCodeCallbackServlet {

	private static final long serialVersionUID = -7676018911767398277L;

	@EJB
	private UserDao userDao;
//	private EntityManager em = EMF.get().createEntityManager();

	@Override
	protected void onSuccess(HttpServletRequest req, HttpServletResponse resp, Credential credential)
			throws ServletException, IOException {
		Calendar cal = Calendar.getInstance();
		String ct = new SimpleDateFormat("HH:mm:ss").format(cal.getTime());
		System.out.println("-----------------------\nCallbackServlet.success. Time: " + ct);
		String accessToken = credential.getAccessToken();
		System.out.println("CallbackServlet.success: accessToken=" + accessToken);

		//people.get	https://www.googleapis.com/plus/v1/people/userId?access_token=...
		//web page:		https://developers.google.com/+/api/latest/people/get
		//playground:	https://developers.google.com/oauthplayground/
		URL url = new URL(String.format("https://www.googleapis.com/plus/v1/people/me?access_token=%s", accessToken));
		InputStream input = url.openStream();
		GoogleProfile googleProfile = new Gson().fromJson(new InputStreamReader(input, "UTF-8"), GoogleProfile.class); 

		User user = userDao.getByEmail(googleProfile.getEmail());

		if (user == null) {//new user
			user = new User(googleProfile);
			System.out.println("CallbackServlet.success: saving user...");
			userDao.persist(user);
			System.out.println("CallbackServlet.success: user saved: id=" + user.getId()
					+ " email:" + user.getEmail());
		} else {
			System.out.println("CallbackServlet.success: user exists: id=" + user.getId()
					+ " email:" + user.getEmail());
		}

		String gid = googleProfile.getId();
		req.getSession().setAttribute("user_gid", gid);
		System.out.println("CallbackServlet.success: gid=" + gid);

		Long id = user.getId();
		req.getSession().setAttribute("user_id", id);
		System.out.println("CallbackServlet.success: id=" + id);
		req.getSession().setAttribute("user", user);

		resp.sendRedirect("");
	}

	@Override
	protected void onError(HttpServletRequest req, HttpServletResponse resp, AuthorizationCodeResponseUrl errorResponse)
			throws ServletException, IOException {
		System.err.println("authorization error");
	}

	@Override
	protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
		GenericUrl url = new GenericUrl(req.getRequestURL().toString());
		url.setRawPath("/oauth2callback");
		String redirectUri = url.build();
		System.out.println("CallbackServlet.getRedirectUri: " + redirectUri);
		return redirectUri;
	}

	@Override
	protected String getUserId(HttpServletRequest req) throws ServletException, IOException {
		String gid = (String) req.getServletContext().getAttribute("user_gid");
		System.out.println("CallbackServlet.getUserId: " + gid);
		return gid;
	}

	@Override
	protected AuthorizationCodeFlow initializeFlow() throws ServletException, IOException {
		System.out.println("CallbackServlet.initializeFlow");
		Reader clientSecretReader = new InputStreamReader(
				LoginServlet.class.getResourceAsStream("/client_secrets.json"));
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(new JacksonFactory(), clientSecretReader);

		return new GoogleAuthorizationCodeFlow.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(),
				clientSecrets, Arrays.asList("https://www.googleapis.com/auth/plus.login", "email")).build();
	}

}
