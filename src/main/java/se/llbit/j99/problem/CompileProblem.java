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

import java.io.PrintStream;
import java.util.Collection;

@SuppressWarnings("serial")
public abstract class CompileProblem extends Exception {
	private final ErrorNode node;
	private final String message;

	public CompileProblem(ErrorNode node, String message) {
		this.node = node;
		this.message = message;
	}

	/**
 	 * This should be overridden by problem kinds which are not recoverable.
 	 */
	public boolean isCritical() {
		return false;
	}

	/**
 	 * Find out if a set of problems is critical, i.e. if at least one of
 	 * the problems is critical the whole set is critical.
 	 */
	public static final boolean isCritical(Collection<CompileProblem> problems) {
		for (CompileProblem problem : problems) {
			if (problem.isCritical()) {
				return true;
			}
		}
		return false;
	}

	public static final void reportProblems(Collection<CompileProblem> problems, PrintStream out) {
		for (CompileProblem problem : problems) {
			out.println(problem.getFullMessage());
		}
		out.flush();
	}

	public String getFileName() {
		return node.getFileName();
	}

	public int getEndColumn() {
		return node.getEndColumn();
	}

	public int getEndLine() {
		return node.getEndLine();
	}

	public int getStartColumn() {
		return node.getBeginColumn();
	}

	public int getStartLine() {
		return node.getBeginLine();
	}

	@Override
	public String getMessage() {
		return message;
	}

	public final String getFullMessage() {
		return getFileName()+":"+getStartLine()+":"+getStartColumn()+": "+getMessage()+node.getExtraMsg();
	}

	@Override
	public String toString() {
		return message+node.getExtraMsg();
	}
}
