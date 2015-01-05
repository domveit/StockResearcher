package org.djv.stockresearcher.model;

import java.util.ArrayList;
import java.util.List;

public enum FinDataType {

	KEY_RATIOS			("K", "Key Ratios"),
	INCOME_STATEMENT	("I", "Income Statement"),
	BALANCE_SHEET		("B", "Balance Sheet"),
	CASH_FLOW_STATEMENT ("C", "Cash Flow Statement");
	
	private final String typeCode;
	private final String typeText;

	private FinDataType(String typeCode, String typeText) {
		this.typeCode = typeCode;
		this.typeText = typeText;
	}

	public String getTypeCode() {
		return typeCode;
	}

	public String getTypeText() {
		return typeText;
	}
	
	public static FinDataType getFromCode(String typeCode){
		for (FinDataType tt : values()){
			if (tt.getTypeCode().equals(typeCode)){
				return tt;
			}
		}
		throw new IllegalArgumentException ("Tran code " + typeCode + " does not exist.");
	}

	public static String getDisplayFromCode(String type) {
		return getFromCode(type).getTypeText();
	}
	
	public static String getCodeFromDisplay(String typeText){
		return getFromDisplay(typeText).getTypeCode();
	}
	
	public static FinDataType getFromDisplay(String typeText){
		for (FinDataType tt : values()){
			if (tt.getTypeText().equals(typeText)){
				return tt;
			}
		}
		throw new IllegalArgumentException ("Tran text " + typeText + " does not exist.");
	}
	
	public static String [] getTextOptions (){
		List<String> options = new ArrayList<String>();
		for (FinDataType tt : values()){
			options.add(tt.getTypeText());
		}
		return options.toArray(new String[0]);
	}
}
