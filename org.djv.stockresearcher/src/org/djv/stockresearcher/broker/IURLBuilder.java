package org.djv.stockresearcher.broker;

public interface IURLBuilder {
	
	public String buildURL(String exchange, String symbol);
}
