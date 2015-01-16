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
public class WordPair {

	@Id @GeneratedValue
	private Long id;

	@OneToMany (cascade = CascadeType.ALL) @JoinColumn (name="wordpair")
	List <WordSide> wordSides = new LinkedList <WordSide>();

	@ManyToOne @JoinColumn (name="wordlist")
	private WordFile wordFile;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<WordSide> getWordSides() {
		return Collections.unmodifiableList(wordSides);
	}

	public WordFile getWordFile() {
		return wordFile;
	}

	public void setWordFile(WordFile wordFile) {
		if (!wordFile.wordPairs.contains(this)) {
			wordFile.wordPairs.add(this);
		}
		this.wordFile = wordFile;
	}

}
