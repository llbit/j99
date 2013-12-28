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

public class NullFragment implements Fragment {

	@Override
	public char charAt(int offset) {
		throw new IndexOutOfBoundsException();
	}

	@Override
	public int getColumn(int offset) {
		return 0;
	}

	@Override
	public int getLength() {
		return 0;
	}

	@Override
	public int getLine(int offset) {
		return 0;
	}

	@Override
	public Fragment part(int start, int end) {
		return this;
	}

	@Override
	public String getFileName(int offset) {
		return "";
	}

	@Override
	public int read(int offset, char[] cbuf, int off, int len) {
		throw new IndexOutOfBoundsException();
	}

	@Override
	public boolean isSource() {
		return true;
	}

	@Override
	public Fragment sourcePart(int start, int end) {
		return this;
	}

	public String toString() {
		return "";
	}

	@Override
	public List<Fragment> sourceList(int start, int end) {
		return new LinkedList<Fragment>();
	}

}
