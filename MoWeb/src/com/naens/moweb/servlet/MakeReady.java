package com.naens.moweb.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(value="/make-ready")
public class MakeReady extends HttpServlet {

	private static final long serialVersionUID = 7595167284676382230L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Boolean ready = Boolean.parseBoolean(req.getParameter("ready"));
//		req.getSession().setAttribute("ready", ready);
		System.out.println("ready: received! ready = " + ready);
		req.getServletContext().setAttribute("ready", ready);

		long wait = (long) (Math.random() * 4000) + 2000;
		System.out.println ("waiting " + wait/1000.0 + " s");
		try {
			Thread.sleep(wait);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		resp.addHeader("answer", String.format ("It took %.2f to answer...", wait/1000.0));
		req.getServletContext().setAttribute("last_update", new Long (Calendar.getInstance().getTimeInMillis()));
		req.getServletContext().setAttribute("took_update", new Long (wait));

//		super.doPost(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	    PrintWriter out = resp.getWriter();
		out.println("MakeReady servlet");
		System.out.println("MakeReady servlet");
		
//		super.doGet(req, resp);
	}
}
