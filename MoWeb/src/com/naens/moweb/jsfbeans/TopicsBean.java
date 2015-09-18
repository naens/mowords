package com.naens.moweb.jsfbeans;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import com.naens.moweb.dao.TopicDao;
import com.naens.moweb.model.Topic;
import com.naens.moweb.model.User;
import com.naens.moweb.model.WordFolder;
import com.naens.moweb.model.WordPairType;
import com.naens.moweb.model.WordSideType;
import com.naens.moweb.service.FolderService;
import com.naens.moweb.service.StylesService;
import com.naens.moweb.service.TopicService;

@ManagedBean
@ViewScoped
public class TopicsBean {

	@EJB
	private StylesService stylesService;

	@EJB
	private FolderService folderService;

	@EJB
	private TopicDao topicDao;

	@EJB
	private TopicService topicService;
	
	private Topic topic;

	private Long topicId;
	
	private List <Topic> topics;

    @PostConstruct
    public void init(){
		FacesContext facesContext = FacesContext.getCurrentInstance(); 
		User user = (User) facesContext.getExternalContext().getSessionMap().get("user"); 
		if (user == null) {
			//TODO "redirect"; location="/"
		}

		topics = topicService.getTopicsSortedByPosition (user);
	    String currentTopicParameter = facesContext.getExternalContext().getRequestParameterMap().get("topicId");

	    Long currentTopicId = null;
	    try {
		    currentTopicId = Long.parseLong(currentTopicParameter);
	    }
	    catch(NumberFormatException nfe) {  
	    }  
		if (topics.size() != 0) {
			if (currentTopicId == null) {
				topic = topics.get(0);
			} else {
				for (Topic t : topics) {
					Long tid = t.getId();
					if (currentTopicId.equals(tid)) {
						topic = t;
					}
				}
			}
		}
		System.out.println("Topics:init " + topic);
	}

	public Topic getTopic() {
		return topic;
	}

	public Long getTopicId() {
		return topicId;
	}

	public List<Topic> getTopics() {
		return topics;
	}

	public List <WordPairType> getPairTypes () {
		List <WordPairType> r = stylesService.getPairTypes(topic);
		return r;
	}

	public List <WordFolder> getFoldersByPairTypeSortedByNumber () {
		List <WordFolder> folders = folderService.getByPairTypeSortedByNumber(topic);
		return folders;
	}

	public List <WordFolder> getFoldersByPairTypeSortedByNumber (WordPairType pairType) {
		List <WordFolder> folders = folderService.getByPairTypeSortedByNumber(pairType);
		return folders;
	}

	public List <WordSideType> getSideTypes () {
		return stylesService.getSideTypes (topic);
	}
}
