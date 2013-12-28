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
import java.util.Collection;

public class LineSplicer implements Fragmenter {

	private Fragmenter in;

	public LineSplicer(Fragmenter f) {
		in = f;
	}

	@Override
	public Fragment nextFragment() throws IOException {
		Fragment f = in.nextFragment();
		if (f.getLength() == 0)
			return f;
		if (f.charAt(f.getLength()-1) == '\\') {
			Collection<Fragment> parts = new ArrayList<Fragment>();
			parts.add(f.part(0, f.getLength()-1));
			while (true) {
				f = in.nextFragment();
				if (f.charAt(f.getLength()-1) == '\\') {
					parts.add(f.part(0, f.getLength()-1));
				} else {
					parts.add(f);
					break;
				}
			}
			return new CompositeFragment(parts);
		} else {
			return f;
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

