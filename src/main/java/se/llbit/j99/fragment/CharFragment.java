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

import java.util.LinkedList;
import java.util.List;


public class CharFragment implements Fragment {

	private char data;
	private int column;
	private int line;
	private String fileName;

	public CharFragment(char data) {
		this.data = data;
		this.column = 0;
		this.line = 0;
		this.fileName = "";
	}

	public CharFragment(char data, Fragment source) {
		this.data = data;
		this.column = source.getColumn(0);
		this.line = source.getLine(0);
		this.fileName = source.getFileName(0);
	}
	
	public CharFragment(char data, String fileName, int line, int column) {
		this.data = data;
		this.column = column;
		this.line = line;
		this.fileName = fileName;
	}
	
	@Override
	public char charAt(int offset) {
		return data;
	}

	@Override
	public int getColumn(int offset) {
		return column;
	}

	@Override
	public String getFileName(int offset) {
		return fileName;
	}

	@Override
	public int getLength() {
		return 1;
	}

	@Override
	public int getLine(int offset) {
		return line;
	}

	@Override
	public Fragment part(int start, int end) {
		if (start >= end)
			return new NullFragment();
		
		if (start == 0 && end == 1) {
			return this;
		}
		
		throw new IndexOutOfBoundsException();
	}

	@Override
	public String toString() {
		return ""+data;
	}

	@Override
	public int read(int offset, char[] cbuf, int off, int len) {
		if (offset != 0)
			throw new IndexOutOfBoundsException();
		cbuf[off] = data;
		return 1;
	}

	@Override
	public boolean isSource() {
		return true;
	}

	@Override
	public Fragment sourcePart(int start, int end) {
		return part(start, end);
	}

	@Override
	public List<Fragment> sourceList(int start, int end) {
		return new LinkedList<Fragment>();
	}

}
