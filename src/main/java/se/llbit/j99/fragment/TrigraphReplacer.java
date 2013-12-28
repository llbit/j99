/*
 * Copyright 2010 Jesper Öqvist <jesper@llbit.se>
 *
 * This file is part of J99.
 *
 * J99 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * J99 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with J99.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.llbit.j99.fragment;

import java.io.*;
import java.util.ArrayList;

public class TrigraphReplacer implements Fragmenter {

	private Fragmenter in;

	public TrigraphReplacer(Fragmenter fragmenter) {
		in = fragmenter;
	}

	@Override
	public Fragment nextFragment() throws IOException {
		Fragment f = in.nextFragment();

		int tristart = nextTrigraph(0, f);
		if (tristart >= 0) {
			ArrayList<Fragment> parts = new ArrayList<Fragment>();
			
			parts.add(f.part(0, tristart));
			parts.add(new CharFragment(
					translate(tristart, f),
					f.getFileName(tristart),
					f.getLine(tristart),
					f.getColumn(tristart)
					));
			int start = tristart+3;
			while ((tristart = nextTrigraph(start, f)) >= 0) {
				parts.add(f.part(start, tristart));
				parts.add(new CharFragment(
						translate(tristart, f),
						f.getFileName(tristart),
						f.getLine(tristart),
						f.getColumn(tristart)
						));
				start = tristart+3;
			}
			if (start+1 < f.getLength())
				parts.add(f.part(start, f.getLength()));
			return new CompositeFragment(parts);
		} else {
			return f;
		}
	}

	private int nextTrigraph(int start, Fragment f) {
		for (int i = start; i+2 < f.getLength(); ++i) {
			if (f.charAt(i) == '?' && f.charAt(i+1) == '?') {
				switch (f.charAt(i+2)) {
				case '=':
				case '/':
				case '\'':
				case '(':
				case ')':
				case '!':
				case '<':
				case '>':
				case '-':
					return i;
				default:
					i += 2;
				}
			}
		}
		return -1;
	}
	
	private char translate(int start, Fragment f) {
		switch (f.charAt(start+2)) {
		case '=':
			return '#';
		case '/':
			return '\\';
		case '\'':
			return '^';
		case '(':
			return '[';
		case ')':
			return ']';
		case '!':
			return '|';
		case '<':
			return '{';
		case '>':
			return '}';
		case '-':
			return '~';
		default:
			throw new RuntimeException("could not identify trigraph");
		}
	}

	@Override
	public boolean ready() throws IOException {
		return in.ready();
	}

	@Override
	public void reset() throws IOException {
		in.reset();
	}

}

