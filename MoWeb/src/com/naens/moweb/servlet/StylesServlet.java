package com.naens.moweb.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.naens.moweb.dao.SideTypeDao;
import com.naens.moweb.dao.TopicDao;
import com.naens.moweb.dao.UserDao;
import com.naens.moweb.model.Topic;
import com.naens.moweb.model.User;
import com.naens.moweb.model.WordSideType;
import com.naens.moweb.service.EMF;
import com.naens.moweb.service.StylesService;

@WebServlet(value = "/styles-servlet")
public class StylesServlet extends HttpServlet {

	private static final long serialVersionUID = 154588212301086099L;

	private StylesService stylesService = new StylesService();

	private SideTypeDao sideTypeDao = new SideTypeDao ();

	private TopicDao topicDao = new TopicDao ();

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String method = req.getParameter("method");
			User user = (User) req.getSession().getAttribute("user");
			if (user == null) {
				resp.sendRedirect("");
				return;
			}

			resp.setContentType("application/json; charset=UTF-8");
			PrintWriter printout = resp.getWriter();
			if (method.equals("add")) {
				Long topicId = Long.parseLong(req.getParameter("topicId"));
				String styleName = req.getParameter("styleName");
				printout.print(addStyle(topicId, styleName));
			} else if (method.equals("rename")) {
				Long styleId = Long.parseLong(req.getParameter("styleId"));
				String styleName = req.getParameter("styleName");
				printout.print(renameStyle(styleId, styleName));
			} else if (method.equals("delete")) {
				Long styleId = Long.parseLong(req.getParameter("styleId"));
				printout.print(deleteStyle(styleId));
			} else if (method.equals("position")) {
				Long topicId = Long.parseLong(req.getParameter("topicId"));
				int startPosition = Integer.parseInt(req.getParameter("startPosition"));
				int endPosition = Integer.parseInt(req.getParameter("endPosition"));
				printout.print(positionStyle(topicId, startPosition, endPosition));
			}
			printout.flush();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
		out.println("StylesServlet");
		System.out.println("StylesServlet");
	}

	private JSONObject addStyle (Long topicId, String styleName) throws JSONException {
		System.out.println ("StylesServlet.addStyle: " + styleName + " to topic " + topicId);
		JSONObject json = new JSONObject();
		Topic topic = topicDao.getById(topicId);
		WordSideType sideType = stylesService.getSideTypeByName(styleName, topic);
		System.out.println("add style:" + styleName + " in " + topic.getName());

		if (sideType == null) {	//new
			EntityManager entityManager = EMF.get().createEntityManager();
			entityManager.getTransaction().begin();

			topic = entityManager.merge(topic);

			sideType = new WordSideType();
			sideType.setTopic(topic);
			sideType.setName(styleName);
			int listPosition = stylesService.countByTopic(topic);
			sideType.setListPosition(listPosition);
			json.put("message", "Added Style: " + styleName);
			System.out.println("Added Style: " + styleName);
			entityManager.persist(sideType);

			entityManager.getTransaction().commit();
			json.put("state", "added");
		} else {	//not new
			json.put("state", "alreadyexists");
			System.out.println("Style already exists: " + styleName);
		}
		return json;
	}

	private JSONObject renameStyle (Long styleId, String styleName) throws JSONException {
		System.out.println ("StylesServlet.renameStyle: " + styleId + " to " + styleName);
		JSONObject json = new JSONObject();

		WordSideType sideType = sideTypeDao.getById(styleId);
		if (sideType != null) {
			System.out.println("rename style:" + sideType.getName() + " to " + styleName);
			WordSideType sideType2 = stylesService.getSideTypeByName(styleName, sideType.getTopic());
			if (sideType2 != null) {
				json.put("state", "alreadyexists");
				System.out.println("Style already exists: " + styleName);
			} else {
				sideType.setName(styleName);
				System.out.println("Renamed Style: " + sideType.getName() + " to: " + styleName);
				sideTypeDao.merge(sideType);
				json.put("state", "renamed");
			}
		} else {
			json.put("state", "notfound");
			System.out.println("Style " + styleId + " not found");
		}
		return json;
	}

	private JSONObject deleteStyle (Long styleId) throws JSONException {
		System.out.println ("StylesServlet.deleteStyle: " + styleId);
		JSONObject json = new JSONObject();

		WordSideType sideType = sideTypeDao.getById(styleId);
		if (sideType != null) {
			System.out.println("delete style: " + sideType.getName() + " id=" + sideType.getId());
			if (stylesService.isSideTypeinPTST (sideType)) {
				json.put("state", "inpairtpe");
				System.out.println("Style " + styleId + " used in pairtypes");
			} else {
				List <WordSideType> styles = stylesService.getSideTypes(sideType.getTopic());
				for (int i = sideType.getListPosition() + 1; i < styles.size(); ++ i) {
					WordSideType st = styles.get(i);
					st.setListPosition(i - 1);
					sideTypeDao.merge(st);
				}
				sideTypeDao.remove(sideType, sideType.getId());
				json.put("state", "deleted");
			}
		} else {
			json.put("state", "notfound");
			System.out.println("Style " + styleId + " not found");
		}
		return json;
	}

	private JSONObject positionStyle (Long topicId, int startPosition, int endPosition) throws JSONException {
		System.out.println ("StylesServlet.positionStyle: " + startPosition + " to " + endPosition);
		JSONObject json = new JSONObject();
		Topic topic = topicDao.getById(topicId);
		List <WordSideType> styles = stylesService.getSideTypes(topic);
		WordSideType styleToMove = styles.remove(startPosition);
		styles.add(endPosition, styleToMove);
		for (int i = Math.min(startPosition, endPosition); i <= Math.max(startPosition, endPosition); ++ i) {
			WordSideType style = styles.get(i);
			style.setListPosition(i);
			sideTypeDao.merge(style);
			System.out.printf("StylesServlet.positionStyle: style[%d]=%s\n", i, styles.get(i));
		}
		json.put("state", "positioned");
		return json;
	}

}
