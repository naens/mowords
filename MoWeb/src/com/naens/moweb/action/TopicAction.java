package com.naens.moweb.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;

import com.naens.moweb.model.Topic;
import com.naens.moweb.model.User;
import com.naens.moweb.service.TopicService;
import com.opensymphony.xwork2.ActionSupport;

public class TopicAction extends ActionSupport {

	private static final long serialVersionUID = 4954307333704762008L;

	private TopicService topicService = new TopicService();
	
	private List <Topic> topics;

	private Topic currentTopic;

	@Override
	@Action(value = "/topics", results = { @Result(name = "input", location = "/WEB-INF/content/topics.jsp") ,
			 @Result(name = "error", type="redirect", location="/") })
	public String execute() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			return "redirect";
		}
		topics = topicService.getTopicsSortedByPosition (user);
		String currentTopicParameter = request.getParameter("topicId");
		if (topics.size() != 0) {
			if (currentTopicParameter == null) {
				currentTopic = topics.get(0);
			} else {
				Long currentTopicId = Long.parseLong(currentTopicParameter);
				for (Topic t : topics) {
					if (t.getId() == currentTopicId) {
						currentTopic = t;
					}
				}
			}
		}
		return "input";
	}

	public List<Topic> getTopics() {
		return topics;
	}

	public Topic getCurrentTopic() {
		return currentTopic;
	}
}
