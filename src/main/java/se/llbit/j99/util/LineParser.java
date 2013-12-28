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

import java.io.IOException;

import se.llbit.j99.fragment.Fragment;
import se.llbit.j99.parser.Symbol;
import se.llbit.j99.pp.And;
import se.llbit.j99.pp.AndEqual;
import se.llbit.j99.pp.AnyDirective;
import se.llbit.j99.pp.Arrow;
import se.llbit.j99.pp.Asterisk;
import se.llbit.j99.pp.AsteriskEqual;
import se.llbit.j99.pp.Caret;
import se.llbit.j99.pp.CaretEqual;
import se.llbit.j99.pp.CharConst;
import se.llbit.j99.pp.Colon;
import se.llbit.j99.pp.Comma;
import se.llbit.j99.pp.Directive;
import se.llbit.j99.pp.Dot;
import se.llbit.j99.pp.DoubleAnd;
import se.llbit.j99.pp.DoubleEqual;
import se.llbit.j99.pp.DoubleGreater;
import se.llbit.j99.pp.DoubleGreaterEqual;
import se.llbit.j99.pp.DoubleHash;
import se.llbit.j99.pp.DoubleLess;
import se.llbit.j99.pp.DoubleLessEqual;
import se.llbit.j99.pp.DoubleMinus;
import se.llbit.j99.pp.DoublePipe;
import se.llbit.j99.pp.DoublePlus;
import se.llbit.j99.pp.Ellipsis;
import se.llbit.j99.pp.EmptyLine;
import se.llbit.j99.pp.Equal;
import se.llbit.j99.pp.Exclamation;
import se.llbit.j99.pp.ExclamationEqual;
import se.llbit.j99.pp.Greater;
import se.llbit.j99.pp.GreaterEqual;
import se.llbit.j99.pp.Hash;
import se.llbit.j99.pp.Identifier;
import se.llbit.j99.pp.LBrace;
import se.llbit.j99.pp.LBracket;
import se.llbit.j99.pp.LParen;
import se.llbit.j99.pp.Less;
import se.llbit.j99.pp.LessEqual;
import se.llbit.j99.pp.List;
import se.llbit.j99.pp.Minus;
import se.llbit.j99.pp.MinusEqual;
import se.llbit.j99.pp.OtherChar;
import se.llbit.j99.pp.PPNumber;
import se.llbit.j99.pp.PPState;
import se.llbit.j99.pp.Percent;
import se.llbit.j99.pp.PercentEqual;
import se.llbit.j99.pp.Pipe;
import se.llbit.j99.pp.PipeEqual;
import se.llbit.j99.pp.Placeholder;
import se.llbit.j99.pp.Plus;
import se.llbit.j99.pp.PlusEqual;
import se.llbit.j99.pp.Question;
import se.llbit.j99.pp.RBrace;
import se.llbit.j99.pp.RBracket;
import se.llbit.j99.pp.RParen;
import se.llbit.j99.pp.Semicolon;
import se.llbit.j99.pp.Slash;
import se.llbit.j99.pp.SlashEqual;
import se.llbit.j99.pp.StringLit;
import se.llbit.j99.pp.TextLine;
import se.llbit.j99.pp.Tilde;
import se.llbit.j99.pp.Token;
import se.llbit.j99.pp.Whitespace;
import se.llbit.j99.pp.WideCharConst;
import se.llbit.j99.pp.WideStringLit;
import se.llbit.j99.problem.CompileError;
import se.llbit.j99.sp.PPScanner;

public class LineParser {

	public static class Terminals {
		public static final short EOF = 0;
		public static final short WHITESPACE = 1;
		public static final short HASH = 2;
		public static final short IDENTIFIER = 3;
		public static final short PPNUMBER = 4;
		public static final short STRING_LIT = 5;
		public static final short WIDE_STRING_LIT = 6;
		public static final short CHARACTER_CONST = 7;
		public static final short WIDE_CHARACTER_CONST = 8;
		public static final short OTHER_CHAR = 9;
		public static final short LBRACKET = 10;
		public static final short RBRACKET = 11;
		public static final short LPAREN = 12;
		public static final short RPAREN = 13;
		public static final short LBRACE = 14;
		public static final short RBRACE = 15;
		public static final short DOT = 16;
		public static final short ARROW = 17;
		public static final short DOUBLEPLUS = 18;
		public static final short DOUBLEMINUS = 19;
		public static final short AND = 20;
		public static final short ASTERISK = 21;
		public static final short PLUS = 22;
		public static final short MINUS = 23;
		public static final short TILDE = 24;
		public static final short EXCLAMATION = 25;
		public static final short SLASH = 26;
		public static final short PERCENT = 27;
		public static final short DOUBLELESS = 28;
		public static final short DOUBLEGREATER = 29;
		public static final short LESS = 30;
		public static final short GREATER = 31;
		public static final short LESSEQUAL = 32;
		public static final short GREATEREQUAL = 33;
		public static final short DOUBLEEQUAL = 34;
		public static final short EXCLAMATIONEQUAL = 35;
		public static final short CARET = 36;
		public static final short PIPE = 37;
		public static final short DOUBLEAND = 38;
		public static final short DOUBLEPIPE = 39;
		public static final short QUESTION = 40;
		public static final short COLON = 41;
		public static final short SEMICOLON = 42;
		public static final short ELLIPSIS = 43;
		public static final short EQUAL = 44;
		public static final short ASTERISKEQUAL = 45;
		public static final short SLASHEQUAL = 46;
		public static final short PERCENTEQUAL = 47;
		public static final short PLUSEQUAL = 48;
		public static final short MINUSEQUAL = 49;
		public static final short DOUBLELESSEQUAL = 50;
		public static final short DOUBLEGREATEREQUAL = 51;
		public static final short ANDEQUAL = 52;
		public static final short CARETEQUAL = 53;
		public static final short PIPEEQUAL = 54;
		public static final short COMMA = 55;
		public static final short DOUBLEHASH = 56;

		static public final String[] NAMES = {
			"EOF",
			"WHITESPACE",
			"HASH",
			"IDENTIFIER",
			"PPNUMBER",
			"STRING_LIT",
			"WIDE_STRING_LIT",
			"CHARACTER_CONST",
			"WIDE_CHARACTER_CONST",
			"OTHER_CHAR",
			"LBRACKET",
			"RBRACKET",
			"LPAREN",
			"RPAREN",
			"LBRACE",
			"RBRACE",
			"DOT",
			"ARROW",
			"DOUBLEPLUS",
			"DOUBLEMINUS",
			"AND",
			"ASTERISK",
			"PLUS",
			"MINUS",
			"TILDE",
			"EXCLAMATION",
			"SLASH",
			"PERCENT",
			"DOUBLELESS",
			"DOUBLEGREATER",
			"LESS",
			"GREATER",
			"LESSEQUAL",
			"GREATEREQUAL",
			"DOUBLEEQUAL",
			"EXCLAMATIONEQUAL",
			"CARET",
			"PIPE",
			"DOUBLEAND",
			"DOUBLEPIPE",
			"QUESTION",
			"COLON",
			"SEMICOLON",
			"ELLIPSIS",
			"EQUAL",
			"ASTERISKEQUAL",
			"SLASHEQUAL",
			"PERCENTEQUAL",
			"PLUSEQUAL",
			"MINUSEQUAL",
			"DOUBLELESSEQUAL",
			"DOUBLEGREATEREQUAL",
			"ANDEQUAL",
			"CARETEQUAL",
			"PIPEEQUAL",
			"COMMA",
			"DOUBLEHASH"
		};
	}

	private final PPState state;

	public LineParser(PPState state) {
		this.state = state;
	}

	public Directive parse(Fragment in) throws IOException {
		PPScanner scanner = new PPScanner(in);

		try {
			Token ws = null;// assign null to silence java warning
			boolean haveWS = false;
			while (true) {
				Symbol sym = scanner.nextSymbol();

				if (!haveWS) {
					switch (sym.id) {
					case Terminals.EOF:
						return new EmptyLine();
					case Terminals.WHITESPACE:
						ws = getToken(sym);
						haveWS = true;
						break;
					case Terminals.HASH:
						return parseDirective(scanner, sym);
					default:
						return parseTextLine(scanner,
								new List<Token>().add(getToken(sym)));
					}
				} else {

					switch (sym.id) {
					case Terminals.EOF:
						return new TextLine(new List<Token>().add(ws));
					case Terminals.HASH:
						return parseDirective(scanner, sym);
					default:
						return parseTextLine(scanner,
								new List<Token>().add(ws).add(getToken(sym)));
					}
				}
			}
		} catch (CompileError e) {
			state.problem(e);
			return new EmptyLine();
		}
	}

	public Token parseToken(Fragment in) throws IOException {
		PPScanner scanner = new PPScanner(in);

		try {
			Symbol sym = scanner.nextSymbol();

			if (sym.id == Terminals.EOF) {
				state.problem(new CompileError(in,
							"not a valid preprocessing token: '"+in.toString()+"'"));
				return new Placeholder();
			} else {
				Token token = getToken(sym);

				if (scanner.nextSymbol().id != Terminals.EOF) {
					state.problem(new CompileError(in,
								"not a valid preprocessing token: '"+in.toString()+"'"));
					return new Placeholder();
				} else {
					return token;
				}
			}

		} catch (CompileError e) {
			state.problem(new CompileError(in,
						"not a valid preprocessing token: '"+in.toString()+"'"));
			return new Placeholder();
		}
	}

	private Token collectRange(Token token, Symbol sym) {
		token.setFirstToken(sym);
		token.setLastToken(sym);
		return token;
	}

	private Token getToken(Symbol sym) {
		switch (sym.id) {
			case Terminals.WHITESPACE:
				return collectRange(new Whitespace((String) sym.value), sym);
			case Terminals.HASH:
				return collectRange(new Hash(), sym);
			case Terminals.IDENTIFIER:
				return collectRange(new Identifier((String) sym.value), sym);
			case Terminals.PPNUMBER:
				return collectRange(new PPNumber((String) sym.value), sym);
			case Terminals.STRING_LIT:
				return collectRange(new StringLit((String) sym.value), sym);
			case Terminals.WIDE_STRING_LIT:
				return collectRange(new WideStringLit((String) sym.value), sym);
			case Terminals.CHARACTER_CONST:
				return collectRange(new CharConst((String) sym.value), sym);
			case Terminals.WIDE_CHARACTER_CONST:
				return collectRange(new WideCharConst((String) sym.value), sym);
			case Terminals.OTHER_CHAR:
				return collectRange(new OtherChar((String) sym.value), sym);
			case Terminals.LBRACKET:
				return collectRange(new LBracket(), sym);
			case Terminals.RBRACKET:
				return collectRange(new RBracket(), sym);
			case Terminals.LPAREN:
				return collectRange(new LParen(), sym);
			case Terminals.RPAREN:
				return collectRange(new RParen(), sym);
			case Terminals.LBRACE:
				return collectRange(new LBrace(), sym);
			case Terminals.RBRACE:
				return collectRange(new RBrace(), sym);
			case Terminals.DOT:
				return collectRange(new Dot(), sym);
			case Terminals.ARROW:
				return collectRange(new Arrow(), sym);
			case Terminals.DOUBLEPLUS:
				return collectRange(new DoublePlus(), sym);
			case Terminals.DOUBLEMINUS:
				return collectRange(new DoubleMinus(), sym);
			case Terminals.AND:
				return collectRange(new And(), sym);
			case Terminals.ASTERISK:
				return collectRange(new Asterisk(), sym);
			case Terminals.PLUS:
				return collectRange(new Plus(), sym);
			case Terminals.MINUS:
				return collectRange(new Minus(), sym);
			case Terminals.TILDE:
				return collectRange(new Tilde(), sym);
			case Terminals.EXCLAMATION:
				return collectRange(new Exclamation(), sym);
			case Terminals.SLASH:
				return collectRange(new Slash(), sym);
			case Terminals.PERCENT:
				return collectRange(new Percent(), sym);
			case Terminals.DOUBLELESS:
				return collectRange(new DoubleLess(), sym);
			case Terminals.DOUBLEGREATER:
				return collectRange(new DoubleGreater(), sym);
			case Terminals.LESS:
				return collectRange(new Less(), sym);
			case Terminals.GREATER:
				return collectRange(new Greater(), sym);
			case Terminals.LESSEQUAL:
				return collectRange(new LessEqual(), sym);
			case Terminals.GREATEREQUAL:
				return collectRange(new GreaterEqual(), sym);
			case Terminals.DOUBLEEQUAL:
				return collectRange(new DoubleEqual(), sym);
			case Terminals.EXCLAMATIONEQUAL:
				return collectRange(new ExclamationEqual(), sym);
			case Terminals.CARET:
				return collectRange(new Caret(), sym);
			case Terminals.PIPE:
				return collectRange(new Pipe(), sym);
			case Terminals.DOUBLEAND:
				return collectRange(new DoubleAnd(), sym);
			case Terminals.DOUBLEPIPE:
				return collectRange(new DoublePipe(), sym);
			case Terminals.QUESTION:
				return collectRange(new Question(), sym);
			case Terminals.COLON:
				return collectRange(new Colon(), sym);
			case Terminals.SEMICOLON:
				return collectRange(new Semicolon(), sym);
			case Terminals.ELLIPSIS:
				return collectRange(new Ellipsis(), sym);
			case Terminals.EQUAL:
				return collectRange(new Equal(), sym);
			case Terminals.ASTERISKEQUAL:
				return collectRange(new AsteriskEqual(), sym);
			case Terminals.SLASHEQUAL:
				return collectRange(new SlashEqual(), sym);
			case Terminals.PERCENTEQUAL:
				return collectRange(new PercentEqual(), sym);
			case Terminals.PLUSEQUAL:
				return collectRange(new PlusEqual(), sym);
			case Terminals.MINUSEQUAL:
				return collectRange(new MinusEqual(), sym);
			case Terminals.DOUBLELESSEQUAL:
				return collectRange(new DoubleLessEqual(), sym);
			case Terminals.DOUBLEGREATEREQUAL:
				return collectRange(new DoubleGreaterEqual(), sym);
			case Terminals.ANDEQUAL:
				return collectRange(new AndEqual(), sym);
			case Terminals.CARETEQUAL:
				return collectRange(new CaretEqual(), sym);
			case Terminals.PIPEEQUAL:
				return collectRange(new PipeEqual(), sym);
			case Terminals.COMMA:
				return collectRange(new Comma(), sym);
			case Terminals.DOUBLEHASH:
				return collectRange(new DoubleHash(), sym);
			default:
				throw new IllegalStateException("unexpected terminal type: "+
						Terminals.NAMES[sym.id]);
		}
	}

	private AnyDirective parseDirective(PPScanner scanner, Symbol hash)
		throws CompileError, IOException
	{
		AnyDirective d = new AnyDirective();
		d.setFirstToken(hash);
		while (true) {
			Symbol sym = scanner.nextSymbol();

			switch (sym.id) {
				case Terminals.EOF:
					return d;
				default:
					d.addToken(getToken(sym));
			}
		}
	}

	private Directive parseTextLine(PPScanner scanner, List<Token> tokens)
		throws CompileError, IOException
	{
		TextLine d = new TextLine(tokens);
		while (true) {
			Symbol sym = scanner.nextSymbol();

			switch (sym.id) {
				case Terminals.EOF:
					return d;
				default:
					d.addToken(getToken(sym));
			}
		}
	}

}
