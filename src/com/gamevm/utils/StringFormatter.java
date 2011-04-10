package com.gamevm.utils;

import java.util.Iterator;

public class StringFormatter {
	
	public static <T> String printIterable(Iterable<T> iterable, String delimiter) {
		Iterator<T> i = iterable.iterator();
		StringBuilder b = new StringBuilder();
		if (i.hasNext()) {
			b.append(i.next());
		}
		while (i.hasNext()) {
			b.append(delimiter);
			b.append(i.next());
		}
		return b.toString();
	}
	
	public static <T> String printIterable(T[] iterable, String delimiter) {
		StringBuilder b = new StringBuilder();
		if (iterable.length > 0) {
			b.append(iterable[0]);
			for (int i = 1; i < iterable.length; i++) {
				b.append(delimiter);
				b.append(iterable[i]);
			}
		}
		return b.toString();
	}
	
	public static String generateWhitespaces(int number) {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < number; i++) {
			b.append(' ');
		}
		return b.toString();
	}

}
