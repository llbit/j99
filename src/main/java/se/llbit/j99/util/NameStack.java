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

public class NameStack {
	public static final int IDENTIFIER = 0;
	public static final int TYPEDEF = 1;

	protected int size = 0;
	protected int capacity = 20;
	protected NamedItem[] data = new NamedItem[20];
	NameStack next;


	public NameStack pushLevel() {
		NameStack ns = new NameStack();
		ns.next = this;
		return ns;
	}

	public NameStack popLevel() {
		NameStack ns = next;
		next = null;
		return ns;
	}

	public void define(String name, int type) {
		NamedItem item = localLookup(name);
		if (item != null) {
			item.type = type;
			return;
		}
		if (size == capacity) {
			capacity *= 2;
			NamedItem[] old = data;
			data = new NamedItem[capacity];
			System.arraycopy(old, 0, data, 0, size);
		}
		data[size++] = new NamedItem(name, type);
	}

	public NamedItem lookup(String name) {
		NamedItem item = localLookup(name);
		if (item == null && next != null)
			item = next.lookup(name);
		return item;
	}

	public NamedItem localLookup(String name) {
		for (int i = 0; i < size; ++i) {
			if (data[i].name.equals(name))
				return data[i];
		}
		return null;
	}
}
