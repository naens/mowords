package com.naens.moweb.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class WordSide {

	@Id @GeneratedValue
	private Long id;

	private int number;

	private String text;

	private String encoding;

	@ManyToOne @JoinColumn (name="wordsidetype")
	private WordSideType wordSideType;

	private byte [] data;

	@ManyToOne @JoinColumn(name="wordpair")
	private WordPair wordPair;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public WordSideType getWordSideType() {
		return wordSideType;
	}

	public void setWordSideType(WordSideType wordSideType) {
		this.wordSideType = wordSideType;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public WordPair getWordPair() {
		return wordPair;
	}

	public void setWordPair(WordPair wordPair) {
		if (!wordPair.wordSides.contains(this)) {
			wordPair.wordSides.add(this);
		}
		this.wordPair = wordPair;
	}
}
