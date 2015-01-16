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
public class WordFile {

	@Id @GeneratedValue
	private Long id;

	private String name;

	@ManyToOne @JoinColumn (name="author")
	private User author;

	@OneToMany (cascade = CascadeType.ALL) @JoinColumn (name="wordfile")
	List <WordPair> wordPairs = new LinkedList <WordPair> ();

	@ManyToOne @JoinColumn (name="folder")
	private WordFolder folder;

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

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public WordFolder getFolder() {
		return folder;
	}

	public void setFolder(WordFolder folder) {
		if (!folder.files.contains(this)) {
			folder.files.add(this);
		}
		this.folder = folder;
	}

	public List <WordPair> getWordPairs() {
		return Collections.unmodifiableList(wordPairs);
	}

	public int getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}

}
