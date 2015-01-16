package com.naens.moweb.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.naens.moweb.dao.FolderDao;
import com.naens.moweb.dao.TopicDao;
import com.naens.moweb.dao.UserDao;
import com.naens.moweb.dao.WordPairTypeDao;
import com.naens.moweb.model.Topic;
import com.naens.moweb.model.User;
import com.naens.moweb.model.WordFolder;
import com.naens.moweb.model.WordPairType;
import com.naens.moweb.model.WordSideType;
import com.naens.moweb.service.TopicService;

@WebServlet(value = "/topic-servlet")
public class TopicServlet extends HttpServlet {

	private static final long serialVersionUID = 154588212301086099L;

	private TopicService topicService = new TopicService();

	private TopicDao topicDao = new TopicDao();

	private User user;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String method = req.getParameter("method");
			user = (User) req.getSession().getAttribute("user");
			if (user == null) {
				resp.sendRedirect("");
				return;
			}
			resp.setContentType("application/json; charset=UTF-8");
			PrintWriter printout = resp.getWriter();
			if (method.equals("add")) {
				String topicName = req.getParameter("topicName");
				printout.print(addTopic(topicName));
			} else if (method.equals("rename")) {
				Long topicId = Long.parseLong(req.getParameter("topicId"));
				String topicName = req.getParameter("topicName");
				printout.print(renameTopic(topicId, topicName));
			} else if (method.equals("delete")) {
				Long topicId = Long.parseLong(req.getParameter("topicId"));
				printout.print(deleteTopic(topicId));
			} else if (method.equals("position")) {
				int startPosition = Integer.parseInt(req.getParameter("startPosition"));
				int endPosition = Integer.parseInt(req.getParameter("endPosition"));
				printout.print(positionTopic(startPosition, endPosition));
			}
			printout.flush();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
		out.println("TopicServlet");
		System.out.println("TopicServlet");
	}

	private JSONObject addTopic (String topicName) throws JSONException {

		JSONObject json = new JSONObject();
		Topic topic = topicService.getByName(topicName, user);
		if (topic == null) {
			topic = new Topic();
			topic.setName(topicName);
			topic.setOwner(user);
			topic.setPosition(topicService.countByUser(user));
	
			WordSideType style1 = new WordSideType();
			style1.setName ("style1");
			style1.setTopic (topic);
	
			WordSideType style2 = new WordSideType();
			style2.setName("style2");
			style2.setTopic (topic);
	
			WordPairType wp = new WordPairType();
			wp.addWordSideType (style1, 0);
			wp.addWordSideType (style2, 1);
			new WordPairTypeDao().persist(wp);

			WordFolder folder = new WordFolder();
			folder.setName("folder1");
			folder.setTopic(topic);
			folder.setOrderNumber(0);
			folder.setPairType(wp);
			folder.setTopic(topic);
			new FolderDao().persist(folder);
//			topicDao.persist(folder.getTopic());
			System.out.println ("TopicServlet.add: id=" + topic.getId() + " name=" + topicName);
			json.put("state", "added");
			json.put("topicId", topic.getId());
		} else {
			json.put("state", "alreadyexists");
		}
		return json;
	}

	private JSONObject renameTopic (Long topicId, String topicName) throws JSONException {
		JSONObject json = new JSONObject();
		System.out.println ("TopicServlet.renameTopic: " + topicId + " to " + topicName);
		Topic topic = topicDao.getById(topicId);
		if (topic != null) {
			Topic topic2 = topicService.getByName(topicName, topic.getOwner());
			if (topic2 != null) {
				json.put("state", "alreadyexists");
				System.out.println("Topic already exists: " + topicName);
			} else {
				topic.setName(topicName);
				topicDao.merge(topic);
				json.put("state", "renamed");
			}
		} else {
			json.put("state", "notfound");
			System.out.println("Topic " + topicId + " not found");
		}
		return json;
	}

	private JSONObject deleteTopic (Long topicId) throws JSONException {
		JSONObject json = new JSONObject();
		System.out.println ("TopicServlet.deleteTopic: " + topicId);
		Topic topic = topicDao.getById(topicId);
		if (topic != null) {
			if (topic.getFolders().size() > 0) {
				json.put("state", "containsfolders");
			} else {
				List <Topic> topics = topicService.getTopicsSortedByPosition(user);
				for (int i = topic.getPosition() + 1; i < topics.size(); ++ i) {
					Topic t = topics.get(i);
					t.setPosition(i - 1);
					topicDao.merge(t);
				}
				topicDao.remove(topic, topic.getId());
				json.put("state", "deleted");
			}
		} else {
			json.put("state", "notfound");
		}
		return json;
	}

	private JSONObject positionTopic (int startPosition, int endPosition) throws JSONException {
		JSONObject json = new JSONObject();
		System.out.println("TopicServlet.positionTopic: " + startPosition + " to " + endPosition);
		List <Topic> topics = topicService.getTopicsSortedByPosition(user);
		topics.add(endPosition, topics.remove(startPosition));
		for (int i = Math.min(startPosition, endPosition); i <= Math.max(startPosition, endPosition); ++ i) {
			Topic t = topics.get(i);
			t.setPosition(i);
			topicDao.merge(t);
			System.out.println("TopicServlet.positionTopic ["+t.getPosition()+"]=" + t.getId());
		}
		json.put("state", "positioned");
		return json;
	}

}
