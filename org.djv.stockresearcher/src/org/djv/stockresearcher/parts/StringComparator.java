package org.djv.stockresearcher.parts;

import java.util.Comparator;

import org.eclipse.swt.SWT;

class StringComparator implements Comparator<String> {
		int sortDir;
		
		public StringComparator(int sortDir) {
			super();
			this.sortDir = sortDir;
		}

		@Override
		public int compare(String s1, String s2)  {
			if (sortDir == SWT.UP){
				return s2.compareTo(s1);
			} else {
				return s1.compareTo(s2);
			}
		}

 }