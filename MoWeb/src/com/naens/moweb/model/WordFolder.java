package com.naens.moweb.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class WordFolder {

	@Id @GeneratedValue
	private Long id;

	private String name;

	@ManyToOne @JoinColumn (name="pairType")
	private WordPairType pairType;

	@ManyToOne (cascade=CascadeType.MERGE) @JoinColumn (name="topic")
	private Topic topic;

	@OneToMany (cascade = CascadeType.ALL) @JoinColumn (name="folder")
	List <WordFile> files = new LinkedList<WordFile>();

	private int orderNumber;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<WordFile> getFiles() {
		return Collections.unmodifiableList(files);
	}

	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		if (!topic.folders.contains(this)) {
			topic.folders.add(this);
		}
		this.topic = topic;
	}

	public WordPairType getPairType() {
		return pairType;
	}

	public void setPairType(WordPairType wordPairType) {
		this.pairType = wordPairType;
	}

	public void setOrderNumber(int i) {
		this.orderNumber=i;
	}

	public int getOrderNumber() {
		return orderNumber;
	}

	@Override
	public String toString() {
		return "WordFolder [id=" + id + ", name=" + name + ", topic=" + topic.getName()
				+ ", orderNumber=" + orderNumber + "]";
	}

}
