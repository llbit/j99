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
package se.llbit.j99.util;

import se.llbit.j99.fragment.Fragment;
import se.llbit.j99.fragment.Fragmenter;
import se.llbit.j99.fragment.LineFragment;
import se.llbit.j99.fragment.NullFragment;
import se.llbit.j99.parser.Symbol;
import se.llbit.j99.pp.*;
import se.llbit.j99.problem.CompileProblem;

import java.io.*;
import java.util.Collection;
import java.util.Iterator;

public class Preprocessor implements Fragmenter {

	private Collection<Fragment> out;
	private Iterator<Fragment> iterator;

	public Preprocessor(Fragmenter in, Collection<CompileProblem> problems,
			java.util.List<String> includeDirs, String basePath) {

		PPState state = new PPState();

		state.setProblemCollection(problems);
		state.setIncludeDirs(includeDirs);

		// predefined macros
		// TODO: these need to be attached to the root tree
		state.define(new Identifier("_WIN32_"),
				new ObjMacro(new Identifier("_WIN32_"), new List<Token>()));
		
		Token name = new Identifier("__FILE__");
		Token filename = new StringLit("\"herbaderb.c\"");
		name.setToken(new Symbol(new LineFragment("@j99", 0, 0, "__FILE__")));
		filename.setToken(new Symbol(new LineFragment("@j99", 0, 0, "\"herbaderb.c\"")));
		Macro fileMacro = new ObjMacro(name, new List<Token>().add(filename));
		state.define(name, fileMacro);

		DirectiveParser parser = new DirectiveParser(in, state);
		SourceFile root = parser.parse();
		PPVisitor visitor = new PPVisitor(state, basePath);
		root.accept(visitor);
		out = visitor.getResult();

		iterator = out.iterator();
	}

	@Override
	public Fragment nextFragment() throws IOException {
		if (iterator.hasNext())
			return iterator.next();
		return new NullFragment();
	}

	@Override
	public boolean ready() throws IOException {
		return iterator.hasNext();
	}

	@Override
	public void reset() throws IOException {
		iterator = out.iterator();
	}
}

