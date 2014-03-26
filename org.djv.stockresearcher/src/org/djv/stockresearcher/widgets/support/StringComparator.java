package org.djv.stockresearcher.widgets.support;

import org.eclipse.swt.SWT;

class StringComparator extends SortDirComparator {
	
		@Override
		public int compare(String s1, String s2)  {
			if (sortDir == SWT.UP){
				return s2.compareTo(s1);
			} else {
				return s1.compareTo(s2);
			}
		}

 }