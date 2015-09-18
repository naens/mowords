package com.naens.dao.androiddao;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public abstract class LazyList <E> implements List <E>, Serializable {

	private static final long serialVersionUID = -5559388183763724433L;

	private List <E> list;

	abstract List <E> load (); //loads list

	@Override
	public boolean add (E e) {
		if (list == null) {
			list = load ();
		}
		return list.add (e);
	}

	@Override
	public void add (int index, E element) {
		if (list == null) {
			list = load ();
		}
		list.add (index, element);
	}

	@Override
	public boolean addAll (Collection <? extends E> c) {
		if (list == null) {
			list = load ();
		}
		return list.addAll (c);
	}

	@Override
	public boolean addAll (int index, Collection <? extends E> c) {
		if (list == null) {
			list = load ();
		}
		return list.addAll (index, c);
	}

	@Override
	public void clear () {
		if (list == null) {
			list = load ();
		}
		list.clear ();
	}

	@Override
	public boolean contains (Object o) {
		if (list == null) {
			list = load ();
		}
		return list.contains (o);
	}

	@Override
	public boolean containsAll (Collection <?> c) {
		if (list == null) {
			list = load ();
		}
		return list.containsAll (c);
	}

	@Override
	public E get (int index) {
		if (list == null) {
			list = load ();
		}
		return list.get (index);
	}

	@Override
	public int indexOf (Object o) {
		if (list == null) {
			list = load ();
		}
		return list.indexOf (o);
	}

	@Override
	public boolean isEmpty () {
		if (list == null) {
			list = load ();
		}
		return list.isEmpty ();
	}

	@Override
	public Iterator <E> iterator () {
		if (list == null) {
			list = load ();
		}
		return list.iterator ();
	}

	@Override
	public int lastIndexOf (Object o) {
		if (list == null) {
			list = load ();
		}
		return list.lastIndexOf (o);
	}

	@Override
	public ListIterator <E> listIterator () {
		if (list == null) {
			list = load ();
		}
		return list.listIterator ();
	}

	@Override
	public ListIterator <E> listIterator (int index) {
		if (list == null) {
			list = load ();
		}
		return list.listIterator (index);
	}

	@Override
	public boolean remove (Object o) {
		if (list == null) {
			list = load ();
		}
		return list.remove (o);
	}

	@Override
	public E remove (int index) {
		if (list == null) {
			list = load ();
		}
		return list.remove (index);
	}

	@Override
	public boolean removeAll (Collection <?> c) {
		if (list == null) {
			list = load ();
		}
		return list.removeAll (c);
	}

	@Override
	public boolean retainAll (Collection <?> c) {
		if (list == null) {
			list = load ();
		}
		return list.retainAll (c);
	}

	@Override
	public E set (int index, E element) {
		if (list == null) {
			list = load ();
		}
		return list.set (index, element);
	}

	@Override
	public int size () {
		if (list == null) {
			list = load ();
		}
		return list.size ();
	}

	@Override
	public List <E> subList (int fromIndex, int toIndex) {
		if (list == null) {
			list = load ();
		}
		return list.subList (fromIndex, toIndex);
	}

	@Override
	public Object [] toArray () {
		if (list == null) {
			list = load ();
		}
		return list.toArray ();
	}

	@Override
	public <T> T [] toArray (T [] a) {
		if (list == null) {
			list = load ();
		}
		return list.toArray (a);
	}

}
