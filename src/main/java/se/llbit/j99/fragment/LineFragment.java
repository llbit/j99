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

public class LineFragment implements Fragment {
	
	private int line;
	private int column;
	private int length;
	private String fileName;
	private String data;
	
	public LineFragment(String fileName, int line, int column, String data) {
		this.fileName = fileName;
		this.line = line;
		this.column = column;
		this.data = data;
		length = data.length();
	}
	
	@Override
	public char charAt(int offset) {
		if (offset < 0)
			throw new IndexOutOfBoundsException();
		if (offset >= length)
			throw new IndexOutOfBoundsException();
		return data.charAt(offset);
	}

	@Override
	public int getLine(int offset) {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	@Override
	public int getColumn(int offset) {
		return column+offset;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	@Override
	public int getLength() {
		return length;
	}

	@Override
	public Fragment part(int start, int end) {
		if (start >= end)
			return new NullFragment();
		
		LineFragment part = new LineFragment(
				fileName,
				line,
				column+start,
				data.substring(start, end));
		return part;
	}

	@Override
	public String getFileName(int offset) {
		return fileName;
	}

	@Override
	public String toString() {
		return data;
	}

	@Override
	public int read(int offset, char[] cbuf, int off, int len) {
		int len1 = Math.min(len, length);
		System.arraycopy(data.toCharArray(), offset, cbuf, off, len1);
		return len1;
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
