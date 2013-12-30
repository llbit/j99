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
import java.util.ArrayList;
import java.util.Collection;

import se.llbit.j99.problem.CompileError;
import se.llbit.j99.problem.CompileProblem;
import se.llbit.j99.problem.FragmentMarker;

public class CommentFragmenter implements Fragmenter {

	private final Fragmenter in;
	private final ArrayList<CompileProblem> problems;

	public CommentFragmenter(Fragmenter in, ArrayList<CompileProblem> problems) {
		this.in = in;
		this.problems = problems;
	}

	@Override
	public Fragment nextFragment() throws IOException {
		Fragment f = in.nextFragment();

		int start = lineCommentStart(f);
		if (start >= 0) {
			// It's a line comment, just chop off the rest of the line
			Collection<Fragment> parts = new ArrayList<Fragment>();
			parts.add(f.part(0, start));
			parts.add(new CharFragment(' '));
			return new CompositeFragment(parts);
		} else {
			start = commentStart(f);
			if (start >= 0){
				return parseComment(f, start);
			} else {
				// no comment in this fragment
				return f;
			}
		}
	}

	private Fragment parseComment(Fragment f, int start) throws IOException {
		Collection<Fragment> parts = new ArrayList<Fragment>();
		Collection<Fragment> commentParts = new ArrayList<Fragment>();

		parts.add(f.part(0, start));
		commentParts.add(f.part(start, f.getLength()));
		Fragment next = f.part(start+2, f.getLength());
		while (true) {

			int end = commentEnd(next);

			if (end >= 0) {
				commentParts.add(next.part(0, end+2));
				parts.add(new ReplacedFragment(
							new CompositeFragment(commentParts),
							new CharFragment(' ')));
				parts.add(next.part(end+2, next.getLength()));
				return new CompositeFragment(parts);
			} else {
				commentParts.add(next);
			}

			if (!in.ready()) {
				problems.add(new CompileError(
						new FragmentMarker(new CompositeFragment(commentParts)),
						"comment does not end"));
				return new NullFragment();
			}

			next = in.nextFragment();
		}
	}


	@Override
	public boolean ready() throws IOException {
		return in.ready();
	}

	private int lineCommentStart(Fragment f) {
		for (int i = 0; i < f.getLength()-1; ++i) {
			char ch = f.charAt(i);
			if (ch == '/') {
				char ch2 = f.charAt(i+1);
				if (ch2 == '/') {
					return i;
				} else if (ch2 == '*') {
					return -1;
				}
			} else if (ch == '\'') {
				i = skipCharLiteral(f, i+1);
			} else if (ch == '"') {
				i = skipStringLiteral(f, i+1);
			}
		}
		return -1;
	}

	private int commentStart(Fragment f) {
		for (int i = 0; i < f.getLength()-1; ++i) {
			char ch = f.charAt(i);
			if (ch == '/') {
				if (f.charAt(i+1) == '*') {
					return i;
				}
			} else if (ch == '\'') {
				i = skipCharLiteral(f, i+1);
			} else if (ch == '"') {
				i = skipStringLiteral(f, i+1);
			}
		}
		return -1;
	}

	private int commentEnd(Fragment f) {
		for (int i = 0; i < f.getLength()-1; ++i) {
			if (f.charAt(i) == '*' && f.charAt(i+1) == '/')
				return i;
		}
		return -1;
	}

	private int skipCharLiteral(Fragment f, int i) {
		for (; i < f.getLength()-1; ++i) {
			char ch = f.charAt(i);
			if (ch == '"') {
				break;
			} else if (ch == '\\') {
				i += 1;
			}
		}
		return i+1;
	}

	private int skipStringLiteral(Fragment f, int i) {
		for (; i < f.getLength()-1; ++i) {
			char ch = f.charAt(i);
			if (ch == '"') {
				break;
			} else if (ch == '\\') {
				i += 1;
			}
		}
		return i+1;
	}

	@Override
	public void reset() throws IOException {
		in.reset();
	}
}

