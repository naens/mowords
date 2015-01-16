package com.naens.moweb.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.naens.moweb.dao.UserDao;
import com.naens.moweb.model.GoogleProfile;
import com.naens.moweb.model.User;

@WebServlet (value="/testlogin")
public class TestLogin extends HttpServlet{

	private static final long serialVersionUID = 2445846213616605681L;

	@Override 
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Long id = (long) 1;
		User user = new UserDao ().getById(id);
		if (user == null) {
			user = new User();		
			String json2 = "{ \"kind\": \"plus#person\", \"displayName\": \"Andrei Nesterov\", \"name\": {\"givenName\": \"Andrei\", \"familyName\": \"Nesterov\" }, "
					+ "\"language\": \"en\", \"isPlusUser\": true, \"url\": \"https://plus.google.com/116061730272696151765\", \"gender\": \"male\", "
					+ "\"image\": {\"url\": \"https://lh3.googleusercontent.com/-XdUIqdMkCWA/AAAAAAAAAAI/AAAAAAAAAAA/4252rscbv5M/photo.jpg?sz=50\", \"isDefault\": true}, "
					+ "\"emails\": [{\"type\": \"account\", \"value\": \"andrei.nesterov@gmail.com\"}], \"etag\": \"\\\"MoxPKeu0NQD8g5Gtts3ebh50504/0CnREJd4bU4DlmTtXdjFsj9mZvQ\\\"\", "
					+ "\"ageRange\": {\"min\": 21}, \"verified\": false, \"circledByCount\": 1, \"id\": \"116061730272696151765\", \"objectType\": \"person\" }";
			Gson gson = new Gson();
			GoogleProfile person = gson.fromJson(json2, GoogleProfile.class);
			user.setGoogleProfile(person);

			user.setFirstname(person.getName().getGivenName());
			user.setLastname(person.getName().getFamilyName());
	
			new UserDao().persist(user);
		}

		System.out.println("TestLogin:set user " + user);
		req.getSession().setAttribute("user", user);
		resp.sendRedirect("");
	}
}
