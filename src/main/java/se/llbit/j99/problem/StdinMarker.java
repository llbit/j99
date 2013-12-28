/*
 * Copyright 2010 Jesper �qvist <jesper@llbit.se>
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

public class StdinMarker implements ErrorNode {

	private int column;
	private int length;

	public StdinMarker(int column) {
		this.column = column;
	}
	
	public StdinMarker(int column, int length) {
		this.column = column;
		this.length = length;
	}
	
	@Override
	public int getBeginColumn() {
		return column;
	}

	@Override
	public int getBeginLine() {
		return 0;
	}

	@Override
	public int getEndColumn() {
		return column+length+1;
	}

	@Override
	public int getEndLine() {
		return 0;
	}

	@Override
	public String getFileName() {
		return "stdin";
	}

	@Override
	public String getExtraMsg() {
		return "";
	}

}
