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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class LineFragmenter implements Fragmenter {

	private String lineDelimiter = "\n";
	private int delimiterLength = 1;
	private boolean delimiterDecided = false;
	
	private int line;
	
	private LookaheadReader in;
	private String fileName;

	public LineFragmenter(String fileName, Reader in) {
		this.in = new LookaheadReader(in, 2);
		this.fileName = fileName;
	}

	public LineFragmenter(String fileName, InputStream in) {
		this(fileName, new InputStreamReader(in));
	}

	@Override
	public boolean ready() throws IOException {
		return in.ready();
	}
	
	@Override
	public Fragment nextFragment() throws IOException {
		StringBuffer buf = new StringBuffer(128);
		while (in.ready()) {
			if (hasLineDelimiter()) {
				in.consume(delimiterLength);
				break;
			}
			buf.append((char) in.pop());
		}
		
		return new LineFragment(fileName, line++, 0, buf.toString());
	}

	private boolean hasLineDelimiter() throws IOException {
		if (delimiterDecided) {
			for (int i = 0; i < delimiterLength; ++i) {
				if (in.peek(i) != lineDelimiter.charAt(i))
					return false;
			}
			return true;
		} else {
			if (in.peek(0) == '\n') {
				delimiterDecided = true;
			} else if (in.peek(0) == '\r' && in.peek(1) == '\n') {
				lineDelimiter = "\r\n";
				delimiterLength = 2;
				delimiterDecided = true;
			} else if (in.peek(0) == '\r') {
				lineDelimiter = "\r";
				delimiterDecided = true;
			}
			return delimiterDecided;
		}
	}

	@Override
	public void reset() throws IOException {
		in.reset();
	}

}
