package com.naens.moweb.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class  WordSideType {

	@Id @GeneratedValue
	private Long id;

	private String name;

	private int listPosition;

	@ManyToOne(cascade=CascadeType.ALL)  @JoinColumn (name="topic")
	private Topic topic;

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

	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}

	@Override
	public String toString() {
		return "WordSideType [id=" + id + ", name=" + name + ", topic=" + topic.getName() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public int getListPosition() {
		return listPosition;
	}

	public void setListPosition(int listPosition) {
		this.listPosition = listPosition;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WordSideType other = (WordSideType) obj;
		if (id == null) {
			throw new RuntimeException("SideType Equals: id is null");
//			if (other.id != null)
//				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
