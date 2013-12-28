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


import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;

public class FragmentReader extends Reader {

	private int offset;
	private Fragment in;

	public FragmentReader(Fragment f) {
		in = f;
	}
	
	@Override
	public void close() throws IOException {
	}

	@Override
	public int read(char cbuf[], int off, int len) throws IOException {
		if (!ready())
			return -1;
		
		int count = in.read(offset, cbuf, off, len);
		offset += count;
		return count;
	}

	@Override
	public int read() throws IOException {
		if (!ready())
			return -1;
		return in.charAt(offset++);
	}

	@Override
	public boolean ready() throws IOException {
		return offset < in.getLength();
	}

	@Override
	public void reset() throws IOException {
		offset = 0;
	}

	/**
 	 * Insert newlines after each fragment.
 	 */
	public static CompositeFragment compose(Fragmenter in) throws IOException {
		ArrayList<Fragment> parts = new ArrayList<Fragment>();
		while (in.ready()) {
			parts.add(in.nextFragment());
			parts.add(new CharFragment('\n'));
		}
		return new CompositeFragment(parts);
	}
	
	/**
 	 * Insert newlines after each fragment.
 	 */
	public static CompositeFragment compose(Collection<Fragment> in) throws IOException {
		ArrayList<Fragment> parts = new ArrayList<Fragment>();
		for (Fragment f : in) {
			parts.add(f);
			parts.add(new CharFragment('\n'));
		}
		return new CompositeFragment(parts);
	}

}
