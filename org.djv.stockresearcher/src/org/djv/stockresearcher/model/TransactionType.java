package org.djv.stockresearcher.model;

import java.util.ArrayList;
import java.util.List;
import static org.djv.stockresearcher.model.ValidatorType.*;

public enum TransactionType {

	//													symbol		shares		price		comm		prem
	NONE				("", "", 						NO, 		NO, 		NO, 		NO, 		NO),
	BUY					("B", "Buy", 					REQUIRED, 	REQUIRED, 	REQUIRED, 	OPTIONAL, 	NO),
	SELL				("S", "Sell", 					REQUIRED, 	REQUIRED, 	REQUIRED, 	OPTIONAL, 	NO),
	CASH_WITHDRAWAL		("W", "Cash Withdrawal", 		NO, 		NO, 		REQUIRED, 	NO,			NO),
	CASH_DEPOSIT		("D", "Cash Deposit", 			NO, 		NO, 		REQUIRED, 	NO, 		NO),
	DIVIDEND			("V", "Cash Dividend", 			REQUIRED, 	NO, 		REQUIRED, 	NO, 		NO),
	DIVIDEND_REINVEST	("R", "Dividend Reinvest", 		REQUIRED, 	REQUIRED, 	REQUIRED, 	OPTIONAL, 	NO),
	OPTION_SELL			("P", "Option Sell",	 		REQUIRED, 	REQUIRED,	REQUIRED,	OPTIONAL,	NO),
	CALL_ASSIGN			("E", "Call Assigned (Sell)", 	REQUIRED, 	REQUIRED, 	REQUIRED, 	OPTIONAL, 	OPTIONAL),
	PUT_ASSIGN			("F", "Put Assigned (Buy)", 	REQUIRED, 	REQUIRED, 	REQUIRED, 	OPTIONAL, 	OPTIONAL);
	
	private final String typeCode;
	private final String typeText;
	private ValidatorType symbolRequired;
	private ValidatorType sharesRequired;
	private ValidatorType priceRequired;
	private ValidatorType commissionRequired;
	private ValidatorType premiumRequired;

	private TransactionType(String typeCode, String typeText,
			ValidatorType symbolRequired, ValidatorType sharesRequired,
			ValidatorType priceRequired, ValidatorType commissionRequired,
			ValidatorType premiumRequired) {
		this.typeCode = typeCode;
		this.typeText = typeText;
		this.symbolRequired = symbolRequired;
		this.sharesRequired = sharesRequired;
		this.priceRequired = priceRequired;
		this.commissionRequired = commissionRequired;
		this.premiumRequired = premiumRequired;
	}

	public ValidatorType getSymbolRequired() {
		return symbolRequired;
	}

	public void setSymbolRequired(ValidatorType symbolRequired) {
		this.symbolRequired = symbolRequired;
	}

	public ValidatorType getSharesRequired() {
		return sharesRequired;
	}

	public void setSharesRequired(ValidatorType sharesRequired) {
		this.sharesRequired = sharesRequired;
	}

	public ValidatorType getPriceRequired() {
		return priceRequired;
	}

	public void setPriceRequired(ValidatorType priceRequired) {
		this.priceRequired = priceRequired;
	}

	public ValidatorType getCommissionRequired() {
		return commissionRequired;
	}

	public void setCommissionRequired(ValidatorType commissionRequired) {
		this.commissionRequired = commissionRequired;
	}

	public ValidatorType getPremiumRequired() {
		return premiumRequired;
	}

	public void setPremiumRequired(ValidatorType premiumRequired) {
		this.premiumRequired = premiumRequired;
	}

	public String getTypeCode() {
		return typeCode;
	}

	public String getTypeText() {
		return typeText;
	}
	
	public static TransactionType getFromCode(String typeCode){
		for (TransactionType tt : values()){
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
	
	public static TransactionType getFromDisplay(String typeText){
		for (TransactionType tt : values()){
			if (tt.getTypeText().equals(typeText)){
				return tt;
			}
		}
		throw new IllegalArgumentException ("Tran text " + typeText + " does not exist.");
	}
	
	public static String [] getTextOptions (){
		List<String> options = new ArrayList<String>();
		for (TransactionType tt : values()){
			options.add(tt.getTypeText());
		}
		return options.toArray(new String[0]);
	}
}
