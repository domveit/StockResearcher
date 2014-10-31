package org.djv.stockresearcher.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Position {
	
	StockData sd;
	BigDecimal shares = new BigDecimal("0.00");
	BigDecimal cost = new BigDecimal("0.00");
	BigDecimal basis = new BigDecimal("0.00");
	BigDecimal basisPerShare = new BigDecimal("0.00");
	BigDecimal value = new BigDecimal("0.00");
	
	List<Lot> lotList = new ArrayList<Lot>();
	
	public StockData getSd() {
		return sd;
	}
	public BigDecimal getValue() {
		return value;
	}
	public void setValue(BigDecimal value) {
		this.value = value;
	}
	public void setSd(StockData sd) {
		this.sd = sd;
	}
	public BigDecimal getShares() {
		return shares;
	}
	public void setShares(BigDecimal shares) {
		this.shares = shares;
	}
	public BigDecimal getCost() {
		return cost;
	}
	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}
	public BigDecimal getBasis() {
		return basis;
	}
	public void setBasis(BigDecimal basis) {
		this.basis = basis;
	}
	public List<Lot> getLotList() {
		return lotList;
	}
	public void setLotList(List<Lot> lotList) {
		this.lotList = lotList;
	}
	public BigDecimal getBasisPerShare() {
		return basisPerShare;
	}
	public void setBasisPerShare(BigDecimal basisPerShare) {
		this.basisPerShare = basisPerShare;
	}
	

}
