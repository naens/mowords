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
public class Topic {

	@Id
	@GeneratedValue
	private Long id;

	private String name;

	private int position;

	@ManyToOne (cascade=CascadeType.PERSIST) @JoinColumn (name="owner")
	private User owner;

	@OneToMany (mappedBy="topic",  cascade = CascadeType.ALL)
	List <WordFolder> folders = new LinkedList <WordFolder>();

	@OneToMany (cascade = CascadeType.ALL) @JoinColumn (name="topic")
	List <WordSideType> sideTypes = new LinkedList <WordSideType>(); 

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

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public List <WordFolder> getFolders() {
		System.out.print("Topic.folders(" + folders.size() + "):");
		for (WordFolder folder : folders) {
			System.out.print(folder + " ");
		}
		System.out.println();
		return Collections.unmodifiableList(folders);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Topic other = (Topic) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public List<WordSideType> getSideTypes() {
		return Collections.unmodifiableList (sideTypes);
	}

	@Override
	public String toString() {
		return "Topic [id=" + id + ", name=" + name + "]";
	}
}
