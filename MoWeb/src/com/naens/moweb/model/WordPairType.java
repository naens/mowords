package com.naens.moweb.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Entity
public class WordPairType {

	@Id @GeneratedValue
	private Long id;

	@OneToMany (cascade=CascadeType.ALL) @JoinColumn (name="pairtype")
	private List <PairTypeSideType> ptsts = new LinkedList <PairTypeSideType> ();

	//modify cascade => test delete non unique folder from pair type!
	@OneToMany (cascade=CascadeType.REFRESH)  @JoinColumn (name="pairtype")
	private List<WordFolder> folders = new LinkedList<WordFolder>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List <WordSideType> getSideTypes() {
		Map <Integer, WordSideType> result = new TreeMap <Integer, WordSideType> ();
		for (PairTypeSideType ptst : ptsts) {
			result.put(ptst.getNumber(), ptst.getSideType());
		}
//		System.out.println("WordPairType("+getId()+").getSideTypes: " + result);
		return Collections.unmodifiableList(new LinkedList <WordSideType>(result.values()));
	}

	public PairTypeSideType getPtst(WordSideType sideType, int number) {
		for (PairTypeSideType ptst : ptsts) {
			if (ptst.getSideType().getId().equals(sideType.getId()) && ptst.getNumber() == number) {
				return ptst;
			}
		}
		return null;
	}

	public void addPtst (PairTypeSideType ptst) {
		if (!ptsts.contains(ptst)) {
			ptsts.add(ptst);
			ptst.setPairType(this);
		}
	}

	public void addWordSideType(WordSideType wordSideType, int i) {
		PairTypeSideType ptst = new PairTypeSideType();
		ptst.setPairType (this);
		ptst.setSideType (wordSideType);
		ptst.setNumber(i);
		ptsts.add(ptst);
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
		WordPairType other = (WordPairType) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public List<PairTypeSideType> getPtsts() {
		return ptsts;
	}

	public void setPtsts(List<PairTypeSideType> ptsts) {
		this.ptsts = ptsts;
	}

	@Override
	public String toString() {
		String result = "WordPairType [id=" + id + ", ";

		for (PairTypeSideType ptst : ptsts) {
			result += "{" + ptst.getSideType() + ", n=" + ptst.getNumber() + "}";
		}

		return result + "]";
	}

	public List<WordFolder> getFolders() {
		return Collections.unmodifiableList(folders);
	}

}

