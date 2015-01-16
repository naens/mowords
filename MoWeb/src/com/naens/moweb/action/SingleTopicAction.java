package com.naens.moweb.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;

import com.naens.moweb.dao.TopicDao;
import com.naens.moweb.model.Topic;
import com.naens.moweb.model.User;
import com.naens.moweb.model.WordFolder;
import com.naens.moweb.model.WordPairType;
import com.naens.moweb.model.WordSideType;
import com.naens.moweb.service.FolderService;
import com.naens.moweb.service.StylesService;
import com.opensymphony.xwork2.ActionSupport;

public class SingleTopicAction extends ActionSupport {

	private static final long serialVersionUID = 4954307333704762008L;

	private StylesService stylesService = new StylesService();

	private FolderService folderService = new FolderService();

	private TopicDao topicDao = new TopicDao();
	
	private Topic topic;

	private Long topicId;

	@Override
	@Action(value = "/topic", results = { @Result(name = "input", location = "/WEB-INF/content/topic.jsp") ,
			 @Result(name = "error", type="redirect", location="/") })
	public String execute() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			return "redirect";
		}
		if (topicId != null) {
			topic = topicDao.getById(topicId);
		}
		return "input";
	}

	public Topic getTopic() {
		return topic;
	}

	public Long getTopicId() {
		return topicId;
	}

	public void setTopicId(Long topicId) {
		System.out.println("topicId=" + topicId);
		this.topicId = topicId;
	}

	public List <WordPairType> getPairTypes (Topic topic) {
		List <WordPairType> r = stylesService.getPairTypes(topic);
		return r;
	}

	public List <WordFolder> getFoldersByPairTypeSortedByNumber (Topic topic) {
		List <WordFolder> folders = folderService.getByPairTypeSortedByNumber(topic);
		return folders;
	}

	public List <WordFolder> getFoldersByPairTypeSortedByNumber (WordPairType pairType) {
		List <WordFolder> folders = folderService.getByPairTypeSortedByNumber(pairType);
		return folders;
	}

	public List <WordSideType> getSideTypes (Topic topic) {
		return stylesService.getSideTypes (topic);
	}
}
