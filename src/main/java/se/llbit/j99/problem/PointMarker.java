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

public class PointMarker implements ErrorNode {
	
	String fn;
	int line;
	int column;

	public PointMarker(String fn, int line, int column) {
		this.fn = fn;
		this.line = line;
		this.column = column;
	}
	
	@Override
	public int getBeginColumn() {
		return column;
	}

	@Override
	public int getBeginLine() {
		return line;
	}

	@Override
	public int getEndColumn() {
		return column+1;
	}

	@Override
	public int getEndLine() {
		return line;
	}

	@Override
	public String getFileName() {
		return fn;
	}

	@Override
	public String getExtraMsg() {
		return "";
	}

}
