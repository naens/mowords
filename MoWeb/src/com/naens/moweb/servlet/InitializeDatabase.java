package com.naens.moweb.servlet;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet (value="/initialize_database")
public class InitializeDatabase extends HttpServlet {

	private static final long serialVersionUID = 5952064819956037347L;

    @PersistenceContext(unitName = "db-create")
    private EntityManager em;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		if (em.isOpen()) {
			if (em.getEntityManagerFactory().isOpen()) {
				System.out.println("ok");
			}
		}

		resp.sendRedirect("");
	}
}
