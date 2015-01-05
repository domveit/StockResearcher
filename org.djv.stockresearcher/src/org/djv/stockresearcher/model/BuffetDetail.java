package org.djv.stockresearcher.model;

import java.math.BigDecimal;

public class BuffetDetail {
	
	public String description;
	public BigDecimal value;
	public boolean pass;
	public int score;
	
	public BuffetDetail(String description, BigDecimal value, boolean pass,
			int score) {
		super();
		this.description = description;
		this.value = value;
		this.pass = pass;
		this.score = score;
	}
	public boolean isPass() {
		return pass;
	}
	public void setPass(boolean pass) {
		this.pass = pass;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public BigDecimal getValue() {
		return value;
	}
	public void setValue(BigDecimal value) {
		this.value = value;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	
	

}
