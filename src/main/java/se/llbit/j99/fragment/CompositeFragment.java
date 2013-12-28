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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CompositeFragment implements Fragment {
	
	private int length;
	private ArrayList<Fragment> parts;
	private ArrayList<Integer> offsets;
	private boolean isSource_cached;
	private boolean isSource_value;

	public CompositeFragment(Collection<Fragment> parts) {
		this.parts = new ArrayList<Fragment>(parts.size());
		offsets = new ArrayList<Integer>(parts.size());
		for (Fragment part : parts) {
			if (part.getLength() > 0) {
				this.parts.add(part);
				offsets.add(length);
				length += part.getLength();
			}
		}
	}
	
	@Override
	public int getLength() {
		return length;
	}

	@Override
	public int getColumn(int offset) {
		try {
			int index = partAt(offset);
			Fragment f = parts.get(index);
			return f.getColumn(offset - offsets.get(index));
		} catch (IndexOutOfBoundsException e) {
			return 0;
		}
	}

	@Override
	public int getLine(int offset) {
		try {
			int index = partAt(offset);
			Fragment f = parts.get(index);
			return f.getLine(offset - offsets.get(index));
		} catch (IndexOutOfBoundsException e) {
			return 0;
		}
	}
	
	@Override
	public String getFileName(int offset) {
		try {
			int index = partAt(offset);
			Fragment f = parts.get(index);
			return f.getFileName(offset - offsets.get(index));
		} catch (IndexOutOfBoundsException e) {
			return "";
		}
	}

	@Override
	public Fragment part(int start, int end) {
		if (start >= end)
			return new NullFragment();
		
		int i1 = partAt(start);
		int i2 = partAt(end-1);// end is exclusive
		
		if (i1 == i2) {
			Fragment f = parts.get(i1);
			int offset = offsets.get(i1);
			return f.part(start - offset, end - offset);
		} else {
			Collection<Fragment> list = new ArrayList<Fragment>();
			for (int i = i1; i <= i2; ++i) {
				Fragment f = parts.get(i);
				if (i == i1) {
					list.add(f.part(start - offsets.get(i1), f.getLength()));
				} else if (i == i2) {
					list.add(f.part(0, end - offsets.get(i2)));
				} else {
					list.add(f);
				}
			}
			return new CompositeFragment(list);
		}
	}

	@Override
	public char charAt(int offset) {
		if (offset < 0)
			throw new IndexOutOfBoundsException();
		if (offset >= length)
			throw new IndexOutOfBoundsException();
		
		int pos = 0;
		for (int i = 0; i < parts.size(); ++i) {
			Fragment f = parts.get(i);
			int len = f.getLength();
			if (offset < pos+len)
				return f.charAt(offset-pos);
			pos += len;
		}
		throw new RuntimeException("Composite fragment inconsistent state!");
	}
	
	private int partAt(int offset) {
		int pos = 0;
		for (int i = 0; i < parts.size(); ++i) {
			pos += parts.get(i).getLength();
			if (offset < pos)
				return i;
		}
		throw new IndexOutOfBoundsException();
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer(128);
		for (Fragment f : parts)
			buf.append(f.toString());
		return buf.toString();
	}

	@Override
	public int read(int offset, char[] cbuf, int off, int len) {
		if (offset < 0)
			throw new IndexOutOfBoundsException();
		if (offset >= length)
			throw new IndexOutOfBoundsException();
		
		int pos = 0;
		for (int i = 0; i < parts.size(); ++i) {
			Fragment f = parts.get(i);
			int len1 = f.getLength();
			if (offset < pos+len1)
				return f.read(offset-pos, cbuf, off, Math.min(len, len1));
			pos += len1;
		}
		throw new RuntimeException("Composite fragment inconsistent state!");
	}

	@Override
	public boolean isSource() {
		if (isSource_cached)
			return isSource_value;
		for (Fragment f : parts) {
			if (!f.isSource()) {
				isSource_cached = true;
				isSource_value = false;
				return isSource_value;
			}
		}
		isSource_cached = true;
		isSource_value = true;
		return isSource_value;
	}

	@Override
	public Fragment sourcePart(int start, int end) {
		if (start >= end)
			return new NullFragment();
		
		int i1 = partAt(start);
		int i2 = partAt(end-1);// end is exclusive
		
		if (i1 == i2) {
			Fragment f = parts.get(i1);
			int offset = offsets.get(i1);
			return f.sourcePart(start - offset, end - offset);
		} else {
			Collection<Fragment> list = new ArrayList<Fragment>();
			for (int i = i1; i <= i2; ++i) {
				Fragment f = parts.get(i);
				if (i == i1) {
					list.add(f.sourcePart(start - offsets.get(i1), f.getLength()));
				} else if (i == i2) {
					list.add(f.sourcePart(0, end - offsets.get(i2)));
				} else {
					list.add(f);
				}
			}
			return new CompositeFragment(list);
		}
	}

	@Override
	public List<Fragment> sourceList(int start, int end) {
		return part(start, end).sourceList(start, end);
	}

}
