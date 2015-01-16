package com.naens.moweb.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.naens.moweb.model.User;

@WebServlet(name="CheckByMail", urlPatterns={"/checkbymail"})
public class CheckByMail extends HttpServlet {

	private static final long serialVersionUID = -7004190089157628417L;

//    private UserService userService = new UserService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	String googleEmail = req.getParameter("google_email");
		System.out.println("checkbymail: do post " + googleEmail);
//		User user = userService.getByGoogleEmail(googleEmail);
		User user = null;//TODO: userService.getByGoogleEmail(googleEmail);
		resp.addHeader("is_new", Boolean.toString(user == null));
		System.out.println("is_new=" + Boolean.toString(user == null));
    }

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("checkbymail: do get");
	    PrintWriter out = resp.getWriter();
		out.println("checkbymail: do get");
	}
}
