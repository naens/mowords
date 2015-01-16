package com.naens.moweb.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.naens.moweb.model.User;

@WebServlet (value="/logout")
public class LogoutServlet extends HttpServlet {

	private static final long serialVersionUID = 8495691647747386211L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		User user = (User) req.getSession().getAttribute("user");
		if (user == null) {
		}
		req.getSession().setAttribute("user", null);
		resp.sendRedirect("");
	}
}
