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
import java.io.*;

public class LookaheadReader extends FilterReader {

	public LookaheadReader(Reader in, int nlook) {
		super(in);
		lookahead = nlook;
	}

	public LookaheadReader(InputStream in, int nlook) {
		this(new InputStreamReader(in), nlook);
	}

	private static final int BUFF_SIZE = 1024;
	private int lookahead = 3;
	private char[] buffer = new char[BUFF_SIZE];
	private int pos = 0;
	private int length = 0;

	/**
	 * Skip some input.
	 */
	public void consume(int num) {
		pos += num;
	}

	/**
	 * Look ahead in the input stream.
	 */
	public int peek(int index) throws IOException {
		refill();
		if ((pos+index) < length)
			return buffer[pos+index];
		else
			return -1;
	}

	/**
	 * Pop next character in the stream.
	 */
	public int pop() throws IOException {
		refill();
		if (pos < length)
			return buffer[pos++];
		else
			return -1;
	}

	/**
	 * If the input buffer is empty, refill it.
	 */
	private void refill() throws IOException {
		if (length - pos <= lookahead) {
			if (length-pos > 0) {
				System.arraycopy(buffer, pos, buffer, 0, length-pos);
				length = length-pos;
				pos = 0;
			} else {
				length = 0;
				pos = 0;
			}
			int i = super.read(buffer, length, BUFF_SIZE-length);
			length += (i != -1) ? i : 0;
		}
	}

	public int read() throws java.io.IOException {
		return pop();
	}

	public int read(char cbuf[], int off, int len) throws IOException {
		if (!ready())
			return -1;
		len += off;

		for (int i=off; i<len; i++) {
			int c = read();
			if (c < 0) return i-off;
			else cbuf[i] = (char) c;
		}
		return len-off;
	}

	public boolean ready() throws IOException {
		return pos < length || super.ready();
	}

}

