package org.djv.stockresearcher.db;

import org.djv.stockresearcher.model.OptionTable;

public interface IOptionBroker {

	public abstract OptionTable getOptionTable(String symbol) throws Exception;

	public abstract void getOptionCallsForCurrentPeriod(OptionTable table)
			throws Exception;

}