/*
 * Copyright 2010-2013 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.j99.parser;

import se.llbit.j99.fragment.Fragment;

public class Symbol {

	int length = 0;
	private final int offset;
	private final Fragment fragment;

	public final short id;
	public final Object value;

	public Symbol(short id, Fragment fragment, int offset, int length, Object value) {
		this.id = id;
		this.value = value;
		this.fragment = fragment;
		this.offset = offset;
		this.length = length;
	}

	public Symbol(short id, Fragment fragment, int offset, int length) {
		this(id, fragment, offset, length, null);
	}

	public Symbol(Fragment fragment) {
		this((short) -1, fragment, 0, fragment.getLength(), null);
	}

	public Fragment getSourceFragment() {
		return fragment;
	}
	public Fragment getFragment() {
		return fragment.part(offset, offset+length);
	}
	public String getFileName() {
		return fragment.getFileName(offset);
	}
	public int getOffset() {
		return offset;
	}
	public int getLength() {
		return length;
	}
	public int getBeginColumn() {
		return fragment.getColumn(offset);
	}
	public int getBeginLine() {
		return fragment.getLine(offset);
	}
	public int getEndColumn() {
		return fragment.getColumn(offset+length-1);
	}
	public int getEndLine() {
		return fragment.getLine(offset+length-1);
	}

	@Override
	public String toString() {
		return getFragment().toString();
	}
}
