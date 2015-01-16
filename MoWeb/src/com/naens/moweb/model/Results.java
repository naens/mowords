package com.naens.moweb.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.UniqueConstraint;

@Entity
public class Results {

	@Id
	@GeneratedValue
	private Long id;

	private long date;

	@ManyToOne
	private User player;

	@ManyToMany (cascade = CascadeType.ALL)
	@JoinTable(name = "results_lists",
			joinColumns = {@JoinColumn(name="results_id", referencedColumnName="id")},
			inverseJoinColumns = {@JoinColumn(name="list_id", referencedColumnName="id")},
			uniqueConstraints = {@UniqueConstraint(columnNames = {"results_id", "list_id" })})
	private List<WordFile> lists = new LinkedList<WordFile>();

	@OneToOne (cascade = CascadeType.ALL)
	private Game firstGame;

	@OneToOne (cascade = CascadeType.ALL)
	private Game secondGame;

	public Results() {
	}

	public Results(User user, long date, List<WordFile> files, int done, int total, boolean inverse, int gameTime) {
		this.player = user;
		this.date = date;
		this.lists = files;
		firstGame = new Game();
		firstGame.setDone(done);
		firstGame.setInverse(inverse);
		firstGame.setTotal(total);
		firstGame.setTime(gameTime);
	}

	public Results(User user, long date, List<WordFile> files, int done, int done2, int total, boolean inverse, int gameTime, int gameTime2) {
		this(user, date, files, done, total, inverse, gameTime);
		secondGame = new Game();
		secondGame.setDone(done2);
		secondGame.setInverse(!inverse);
		secondGame.setTotal(total);
		secondGame.setTime(gameTime2);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public List<WordFile> getLists() {
		return Collections.unmodifiableList(lists);
	}

	public Game getFirstGame() {
		return firstGame;
	}

	public void setFirstGame(Game firstGame) {
		this.firstGame = firstGame;
	}

	public Game getSecondGame() {
		return secondGame;
	}

	public void setSecondGame(Game secondGame) {
		this.secondGame = secondGame;
	}

	@Entity
	public static class Game {

		@Id
		@GeneratedValue
		private Long id;

		private int done;
		private int total;
		private boolean inverse;
		private long time;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public int getDone() {
			return done;
		}

		public void setDone(int done) {
			this.done = done;
		}

		public int getTotal() {
			return total;
		}

		public void setTotal(int total) {
			this.total = total;
		}

		public boolean isInverse() {
			return inverse;
		}

		public void setInverse(boolean inverse) {
			this.inverse = inverse;
		}

		public long getTime() {
			return time;
		}

		public void setTime(long time) {
			this.time = time;
		}

	}

	public User getPlayer() {
		return player;
	}

	public void setPlayer(User player) {
		this.player = player;
	}

}
