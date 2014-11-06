package org.djv.stockresearcher.broker;

import org.djv.stockresearcher.model.OptionTable;

public interface IOptionDataBroker {

	public abstract OptionTable getOptionTable(String symbol) throws Exception;

	public abstract void getOptionCallsForCurrentPeriod(OptionTable table)
			throws Exception;

}