package com.gamevm.utils;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ListAdapter<T> implements List<T>, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7256508950637003445L;

	public static class AdaptedIterator<T> implements Iterator<T>  {
		
		private Iterator<? super T> delegate;
		
		public AdaptedIterator(Iterator<? super T> delegate) {
			this.delegate = delegate;
		}

		@Override
		public boolean hasNext() {
			return delegate.hasNext();
		}

		@SuppressWarnings("unchecked")
		@Override
		public T next() {
			return (T)delegate.next();
		}

		@Override
		public void remove() {
			delegate.remove();
		}
		
	}
	
	public static class AdaptedListIterator<T> implements ListIterator<T> {
		
		private ListIterator<? super T> delegate;
		
		public AdaptedListIterator(ListIterator<? super T> delegate) {
			this.delegate = delegate;
		}

		@Override
		public void add(T e) {
			delegate.add(e);
		}

		@Override
		public boolean hasNext() {
			return delegate.hasNext();
		}

		@Override
		public boolean hasPrevious() {
			return delegate.hasPrevious();
		}

		@SuppressWarnings("unchecked")
		@Override
		public T next() {
			return (T)delegate.next();
		}

		@Override
		public int nextIndex() {
			return delegate.nextIndex();
		}

		@SuppressWarnings("unchecked")
		@Override
		public T previous() {
			return (T)delegate.previous();
		}

		@Override
		public int previousIndex() {
			return delegate.previousIndex();
		}

		@Override
		public void remove() {
			delegate.remove();
		}

		@Override
		public void set(T e) {
			delegate.set(e);
		}
		
	}
	
	private List<? super T> delegate;
	
	public ListAdapter(List<? super T> list) {
		this.delegate = list;
	}

	@Override
	public boolean add(T e) {
		return delegate.add(e);
	}

	@Override
	public void add(int index, T element) {
		delegate.add(index, element);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		return delegate.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		return delegate.addAll(index, c);
	}

	@Override
	public void clear() {
		delegate.clear();
	}

	@Override
	public boolean contains(Object o) {
		return delegate.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return delegate.containsAll(c);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(int index) {
		return (T)delegate.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return delegate.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	@Override
	public Iterator<T> iterator() {
		return new AdaptedIterator<T>(delegate.iterator());
	}

	@Override
	public int lastIndexOf(Object o) {
		return delegate.lastIndexOf(o);
	}

	@Override
	public ListIterator<T> listIterator() {
		return new AdaptedListIterator<T>(delegate.listIterator());
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		return new AdaptedListIterator<T>(delegate.listIterator(index));
	}

	@Override
	public boolean remove(Object o) {
		return delegate.remove(o);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T remove(int index) {
		return (T)delegate.remove(index);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return delegate.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return delegate.retainAll(c);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T set(int index, T element) {
		return (T)delegate.set(index, element);
	}

	@Override
	public int size() {
		return delegate.size();
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		// TODO:
		return null;
	}

	@Override
	public Object[] toArray() {
		return delegate.toArray();
	}

	@Override
	public <X> X[] toArray(X[] a) {
		return delegate.toArray(a);
	}
	
	@SuppressWarnings("unchecked")
	private void writeObject(ObjectOutputStream out) throws IOException {
		List<T> newList = new ArrayList<T>(delegate.size());
		for (Object o : delegate) {
			newList.add((T)o);
		}
		out.writeObject(newList);
	}
}
