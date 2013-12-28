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

import java.util.List;


public class ReplacedFragment implements Fragment {
	
	Fragment source;
	Fragment repl;

	public ReplacedFragment(Fragment source, Fragment repl) {
		this.source = source;
		this.repl = repl;
	}

	@Override
	public char charAt(int offset) {
		return repl.charAt(offset);
	}

	@Override
	public int getColumn(int offset) {
		return repl.getColumn(offset);
	}

	@Override
	public String getFileName(int offset) {
		return repl.getFileName(offset);
	}

	@Override
	public int getLength() {
		return repl.getLength();
	}

	@Override
	public int getLine(int offset) {
		return repl.getLine(offset);
	}

	@Override
	public Fragment part(int start, int end) {
		return new ReplacedFragment(source, repl.part(start, end));
	}

	@Override
	public int read(int offset, char[] cbuf, int off, int len) {
		return repl.read(offset, cbuf, off, len);
	}

	@Override
	public boolean isSource() {
		return false;
	}

	@Override
	public Fragment sourcePart(int start, int end) {
		return source.sourcePart(0, source.getLength());
	}

	public String toString() {
		return repl.toString();
	}

	@Override
	public List<Fragment> sourceList(int start, int end) {
		List<Fragment> l = repl.part(start, end).sourceList(start, end);
		l.add(sourcePart(start, end));
		return l;
	}

}
