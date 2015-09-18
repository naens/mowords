package com.naens.moweb.jsfbeans;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import com.naens.moweb.model.User;

@ManagedBean
public class IndexBean {

	private User user;

    @PostConstruct
    public void init(){
		FacesContext facesContext = FacesContext.getCurrentInstance(); 
		Map<String, Object> map =facesContext.getExternalContext().getSessionMap(); 
		setUser((User) map.get("user"));
		System.out.println("Index:init");
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
