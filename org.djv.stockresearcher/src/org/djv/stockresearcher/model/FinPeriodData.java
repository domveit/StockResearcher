package org.djv.stockresearcher.model;

public class FinPeriodData {

	String period;
	String revenue;
	String grossMargin;
	String operatingIncome;
	String operatingMargin;
	String netIncome;
	String earningsPerShare;
	String dividends;
	String payoutRatio;
	String shares;
	String bookValuePerShare;
	String operatingCashFlow;
	String capitalSpending;
	String freeCashFlow;
	String freeCashFlowPerShare;
	String workingCapital;
	
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public String getRevenue() {
		return revenue;
	}
	public void setRevenue(String revenue) {
		this.revenue = revenue;
	}
	public String getGrossMargin() {
		return grossMargin;
	}
	public void setGrossMargin(String grossMargin) {
		this.grossMargin = grossMargin;
	}
	public String getOperatingIncome() {
		return operatingIncome;
	}
	public void setOperatingIncome(String operatingIncome) {
		this.operatingIncome = operatingIncome;
	}
	public String getOperatingMargin() {
		return operatingMargin;
	}
	public void setOperatingMargin(String operatingMargin) {
		this.operatingMargin = operatingMargin;
	}
	public String getNetIncome() {
		return netIncome;
	}
	public void setNetIncome(String netIncome) {
		this.netIncome = netIncome;
	}
	public String getEarningsPerShare() {
		return earningsPerShare;
	}
	public void setEarningsPerShare(String earningsPerShare) {
		this.earningsPerShare = earningsPerShare;
	}
	public String getDividends() {
		return dividends;
	}
	public void setDividends(String dividends) {
		this.dividends = dividends;
	}
	public String getPayoutRatio() {
		return payoutRatio;
	}
	public void setPayoutRatio(String payoutRatio) {
		this.payoutRatio = payoutRatio;
	}
	public String getShares() {
		return shares;
	}
	public void setShares(String shares) {
		this.shares = shares;
	}
	public String getBookValuePerShare() {
		return bookValuePerShare;
	}
	public void setBookValuePerShare(String bookValuePerShare) {
		this.bookValuePerShare = bookValuePerShare;
	}
	public String getOperatingCashFlow() {
		return operatingCashFlow;
	}
	public void setOperatingCashFlow(String operatingCashFlow) {
		this.operatingCashFlow = operatingCashFlow;
	}
	public String getCapitalSpending() {
		return capitalSpending;
	}
	public void setCapitalSpending(String capitalSpending) {
		this.capitalSpending = capitalSpending;
	}
	public String getFreeCashFlow() {
		return freeCashFlow;
	}
	public void setFreeCashFlow(String freeCashFlow) {
		this.freeCashFlow = freeCashFlow;
	}
	public String getFreeCashFlowPerShare() {
		return freeCashFlowPerShare;
	}
	public void setFreeCashFlowPerShare(String freeCashFlowPerShare) {
		this.freeCashFlowPerShare = freeCashFlowPerShare;
	}
	public String getWorkingCapital() {
		return workingCapital;
	}
	public void setWorkingCapital(String workingCapital) {
		this.workingCapital = workingCapital;
	}
	
	public String toString(){
		String s = "period= " + period + "\n";
		s+= "revenue= " + revenue + "\n";
		s+= "grossMargin= " + grossMargin + "\n";
		s+= "operatingIncome= " + operatingIncome + "\n";
		s+= "operatingMargin= " + operatingMargin + "\n";
		s+= "netIncome= " + netIncome + "\n";
		s+= "earningsPerShare= " + earningsPerShare + "\n";
		s+= "dividends= " + dividends + "\n";
		s+= "payoutRatio= " + payoutRatio + "\n";
		s+= "shares= " + shares + "\n";
		s+= "bookValuePerShare= " + bookValuePerShare + "\n";
		s+= "operatingCashFlow= " + operatingCashFlow + "\n";
		s+= "capitalSpending= " + capitalSpending + "\n";
		s+= "freeCashFlow= " + freeCashFlow + "\n";
		s+= "freeCashFlowPerShare= " + freeCashFlowPerShare + "\n";
		s+= "workingCapital= " + workingCapital + "\n";
		
		return s;
	}
	
	
}


