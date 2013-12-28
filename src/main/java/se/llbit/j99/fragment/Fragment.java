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

public interface Fragment {

	public String getFileName(int offset);
	public int getLine(int offset);
	public int getColumn(int offset);

	public int getLength();
	public char charAt(int offset);
	public int read(int offset, char[] cbuf, int off, int len);
	
	/**
	 * Returns the source fragment which this fragment has
	 * been macro expanded from.
	 * 
	 * @param start inclusive
	 * @param end exclusive
	 * @return
	 */
	public Fragment sourcePart(int start, int end);

	/**
 	 * Returns a list of source fragments that have been expanded into
 	 * the result fragment.
 	 *
 	 * @param start inclusive
 	 * @param end exclusive
 	 * @return
 	 */
	public java.util.List<Fragment> sourceList(int start, int end);
	
	/**
	 * Returns false if the fragment has been macro expanded.
	 * 
	 * @return
	 */
	public boolean isSource();

	/**
	 * Returns a sub-fragment representing the characters in the
	 * interval [start, end)
	 * 
	 * @param start inclusive
	 * @param end exclusive
	 * @return
	 */
	public Fragment part(int start, int end);

}
