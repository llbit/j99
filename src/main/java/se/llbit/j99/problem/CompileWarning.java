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

import se.llbit.j99.fragment.Fragment;

@SuppressWarnings("serial")
public class CompileWarning extends CompileProblem {
	
	public CompileWarning(Fragment fragment, String message) {
		super(new FragmentMarker(fragment), message);
	}

	public CompileWarning(ErrorNode node, String message) {
		super(node, message);
	}
	
	public String getMessage() {
		return "(warning) "+super.getMessage();
	}
}
