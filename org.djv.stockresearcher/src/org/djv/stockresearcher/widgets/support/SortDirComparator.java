package org.djv.stockresearcher.widgets.support;

import java.util.Comparator;

public abstract class SortDirComparator implements Comparator<String>{
	
	int sortDir;

	public int getSortDir() {
		return sortDir;
	}

	public void setSortDir(int sortDir) {
		this.sortDir = sortDir;
	}
	
	
}
