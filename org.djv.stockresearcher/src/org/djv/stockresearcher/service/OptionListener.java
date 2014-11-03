package org.djv.stockresearcher.service;

import org.djv.stockresearcher.model.OptionTable;

public interface OptionListener {
	
	public static int TYPE_NEW = 1;
	public static int TYPE_UPDATE = 2;
	
	void notifyChanged(OptionTable ot, int type);

}
