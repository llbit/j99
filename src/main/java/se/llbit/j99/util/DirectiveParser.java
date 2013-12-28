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

import se.llbit.j99.fragment.CharFragment;
import se.llbit.j99.fragment.Fragment;
import se.llbit.j99.fragment.Fragmenter;
import se.llbit.j99.fragment.LineFragment;
import se.llbit.j99.parser.Symbol;
import se.llbit.j99.pp.Block;
import se.llbit.j99.pp.Cond;
import se.llbit.j99.pp.ConditionalDirective;
import se.llbit.j99.pp.Directive;
import se.llbit.j99.pp.ElseCond;
import se.llbit.j99.pp.ElsePart;
import se.llbit.j99.pp.EmptyLine;
import se.llbit.j99.pp.Identifier;
import se.llbit.j99.pp.IfCond;
import se.llbit.j99.pp.IfDefCond;
import se.llbit.j99.pp.IfNDefCond;
import se.llbit.j99.pp.List;
import se.llbit.j99.pp.ObjMacro;
import se.llbit.j99.pp.PPNumber;
import se.llbit.j99.pp.PPState;
import se.llbit.j99.pp.SourceFile;
import se.llbit.j99.pp.TextLine;
import se.llbit.j99.pp.Token;
import se.llbit.j99.pp.Whitespace;
import se.llbit.j99.problem.CompileError;
import se.llbit.j99.problem.FragmentMarker;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Stack;

public class DirectiveParser {

	Stack<Directive> queue = new Stack<Directive>();
	Fragmenter in;
	PPState state;
	private LineParser parser;
	private int lineNum=1;

	public DirectiveParser(Fragmenter in, PPState state) {
		this.in = in;
		this.state = state;
		parser = new LineParser(state);
	}

	/**
 	 * Used for grabbing fragments from the fragment stream without IOExceptions.
 	 */
	private boolean notEmpty() {
		try {
			if (!queue.isEmpty())
				return true;
			if (!in.ready())
				return false;
			fill();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	private Directive pop() {
		if (queue.isEmpty()) {
			try {
				fill();
			} catch (IOException e) {
				return new EmptyLine();
			}
		}
		return queue.pop();
	}

	private Directive peek() {
		if (queue.isEmpty()) {
			try {
				fill();
			} catch (IOException e) {
				return new EmptyLine();
			}
		}
		return queue.peek();
	}

	/**
 	 * Grab the next directive from the input fragment stream
 	 * and put it in the queue.
 	 */
	private void fill() throws IOException {
		Fragment f = in.nextFragment();

		try {
			Token name = new Identifier("__LINE__");
			Token number = new PPNumber(""+lineNum);
			name.setToken(new Symbol(new LineFragment("@j99", lineNum, 0, "__LINE__")));
			number.setToken(new Symbol(new LineFragment("@j99", lineNum, 0, ""+lineNum)));
			state.define(name, new ObjMacro(name, new List<Token>().add(number)));
			Directive d = parser.parse(f);
			queue.add(d);
			lineNum++;

		} catch (IOException e) {
			/*
 			 * This should not happen since we are using a
 			 * FragmentReader, which should never throw an IOException
 			 * if used correctly.
 			 */
			throw new RuntimeException("should not come here");
		}
	}

	public SourceFile parse() {

		List<Directive> directives = parseDirectives();
		return new SourceFile(directives);// attach directives to a root
	}

	private List<Directive> parseDirectives() {
		List<Directive> parsed = new List<Directive>();
		while (notEmpty()) {
			parsed.add(parseDirective());
		}
		return parsed;
	}

	private Directive parseDirective() {
		if (peek().isConditional()) {
			return parseIf();
		} else if (peek().isTextLine()) {
			Collection<TextLine> lines = new LinkedList<TextLine>();
			lines.add((TextLine) pop());
			while (notEmpty()) {
				if (!peek().isTextLine())
					break;
				else
					lines.add((TextLine) pop());
			}
			TextLine merged = new TextLine(new List<Token>());
			boolean first = true;
			for (TextLine line : lines) {
				if (!first) {
					// preserve newlines as whitespace
					Whitespace newLine = new Whitespace("\n");
					newLine.setFragment(new CharFragment('\n'));
					merged.addToken(newLine);
				}
				first = false;
				while (line.getNumToken() > 0) {
					Token token = line.getToken(0);
					line.getTokenList().removeChild(0);
					merged.addToken(token);
				}
			}
			return merged;
		} else {
			return pop();
		}
	}

	private Directive parseIf() {
		Directive cond = pop();
		Block thenBlock = new Block();
		
		Cond elseCond = null;// silence Java warning
		Block elseBlock = null;// silence Java warning
		
		List<ElsePart> elseParts = new List<ElsePart>();
		
		boolean elsePart = false;

		while (notEmpty()) {
			if (peek().isEndIf()) {
				if (elsePart) {
					elseParts.add(new ElsePart(elseCond, elseBlock));
				}
				Cond ifcond;
				if (cond.isIf())
					ifcond = new IfCond(cond.tailTokens());
				else if (cond.isIfDef())
					ifcond = new IfDefCond(cond.tailTokens());
				else
					ifcond = new IfNDefCond(cond.tailTokens());

				return new ConditionalDirective(ifcond,
						thenBlock, elseParts, pop());
			} else if (peek().isElse() || peek().isElIf()) {
				if (elsePart) {
					elseParts.add(new ElsePart(elseCond, elseBlock));
				}
				if (peek().isElse())
					elseCond = new ElseCond(pop().tailTokens());
				else
					elseCond = new IfCond(pop().tailTokens());
				elseBlock = new Block();
				elsePart = true;
			} else {
				if (elsePart)
					elseBlock.addDirective(parseDirective());
				else
					thenBlock.addDirective(parseDirective());
			}
		}
		state.problem(new CompileError(new FragmentMarker(cond.getFragment()),
					"unterminated conditional directive"));
		return new EmptyLine();
	}
}
