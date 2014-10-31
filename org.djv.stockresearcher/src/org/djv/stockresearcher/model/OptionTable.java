package org.djv.stockresearcher.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OptionTable {
	
	String symbol;
	OptionPeriod currentPeriod = null;
	List<OptionPeriod> periods = new ArrayList<OptionPeriod>();
	Map<OptionPeriod, List<Option>> callMap = new HashMap<OptionPeriod, List<Option>>();
	Map<OptionPeriod, List<Option>> putMap = new HashMap<OptionPeriod, List<Option>>();
	
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public Map<OptionPeriod, List<Option>> getCallMap() {
		return callMap;
	}
	public void setCallMap(Map<OptionPeriod, List<Option>> callMap) {
		this.callMap = callMap;
	}
	public Map<OptionPeriod, List<Option>> getPutMap() {
		return putMap;
	}
	public void setPutMap(Map<OptionPeriod, List<Option>> putMap) {
		this.putMap = putMap;
	}
	
	public OptionPeriod getCurrentPeriod() {
		return currentPeriod;
	}
	public void setCurrentPeriod(OptionPeriod currentPeriod) {
		this.currentPeriod = currentPeriod;
	}
	public List<OptionPeriod> getPeriods() {
		return periods;
	}
	public void setPeriods(List<OptionPeriod> periods) {
		this.periods = periods;
	}
	public List<Option> getCallsForCurrentPeriod(){
		return callMap.get(getCurrentPeriod());
	}
	public List<Option> getPutsForCurrentPeriod(){
		return putMap.get(getCurrentPeriod());
	}
	
	public void dump(){
		
		System.err.println("current period " + getCurrentPeriod());
		System.err.println("all periods...");
		for (OptionPeriod p : getPeriods()){
			System.err.println(p);
			System.err.println("calls...");
			List<Option> calls = getCallMap().get(p);
			if (calls != null){
				for (Option c: calls){
					System.err.println("    " + c);
				}
			}
			
			System.err.println("puts...");
			List<Option> puts = getPutMap().get(p);
			if (puts != null){
				for (Option c: puts){
					System.err.println("    " + c);
				}
			}
		}
	}
}
