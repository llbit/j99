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
package se.llbit.j99.problem;

import java.util.List;

import se.llbit.j99.fragment.Fragment;

public class FragmentMarker implements ErrorNode {

	private Fragment fragment;

	public FragmentMarker(Fragment fragment) {
		this.fragment = fragment;
	}
	
	public FragmentMarker(se.llbit.j99.parser.Symbol symbol) {
		this.fragment = symbol.getFragment();
	}

	@Override
	public int getBeginColumn() {
		return fragment.getColumn(0);
	}

	@Override
	public int getBeginLine() {
		return fragment.getLine(0);
	}

	@Override
	public int getEndColumn() {
		return fragment.getColumn(fragment.getLength()-1);
	}

	@Override
	public int getEndLine() {
		return fragment.getLine(fragment.getLength()-1);
	}

	@Override
	public String getFileName() {
		return fragment.getFileName(0);
	}

	@Override
	public String getExtraMsg() {
		if (fragment.isSource())
			return "";
		List<Fragment> sources = fragment.sourceList(0, fragment.getLength());
		StringBuffer buf = new StringBuffer();
		for (Fragment source : sources) {
			buf.append("\n    expanded from "+source+" in "+
				source.getFileName(0)+":"+source.getLine(0)+":"+source.getColumn(0));
		}
		return buf.toString();
	}

}
