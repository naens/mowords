package com.naens.moweb.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;

import com.naens.moweb.model.User;
import com.opensymphony.xwork2.ActionSupport;

public class Index extends ActionSupport {

	private static final long serialVersionUID = 1L;
	private User user;

	@Action(value = "/index", results = {@Result(name = "input", location = "/WEB-INF/content/index.jsp")})

	public String execute() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		setUser((User) session.getAttribute("user"));
		System.out.println("Index:execute");
		return "input";
	}

	@Override
	public String input() throws Exception {
		System.out.println("Index:input");
		return super.input();
	}

	public User getUser() {
		System.out.println("Index:get user " + user);
		return user;
	}

	public void setUser(User user) {
		System.out.println("Index:set user " + user);
		this.user = user;
	}

}
