package com.naens.moweb.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "pairtype_sidetype")
public class PairTypeSideType {

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne (cascade=CascadeType.PERSIST) 
	@JoinColumn(name = "pairtype")
	private WordPairType pairType;

	@ManyToOne (cascade=CascadeType.PERSIST) 
	@JoinColumn(name = "sidetype")
	private WordSideType sideType;

	private int number;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public WordPairType getPairType() {
		return pairType;
	}

	public void setPairType(WordPairType pairType) {
		this.pairType = pairType;
	}

	public WordSideType getSideType() {
		return sideType;
	}

	public void setSideType(WordSideType sideType) {
		this.sideType = sideType;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + number;
		result = prime * result + ((pairType == null) ? 0 : pairType.hashCode());
		result = prime * result + ((sideType == null) ? 0 : sideType.hashCode());
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
		PairTypeSideType other = (PairTypeSideType) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (number != other.number)
			return false;
		if (pairType == null) {
			if (other.pairType != null)
				return false;
		} else if (!pairType.equals(other.pairType))
			return false;
		if (sideType == null) {
			if (other.sideType != null)
				return false;
		} else if (!sideType.equals(other.sideType))
			return false;
		return true;
	}
	
}
