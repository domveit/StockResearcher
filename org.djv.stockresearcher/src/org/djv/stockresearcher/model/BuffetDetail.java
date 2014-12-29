package org.djv.stockresearcher.model;

import java.math.BigDecimal;

public class BuffetDetail {
	
	public String description;
	public BigDecimal value;
	public int score;
	
	public BuffetDetail(String description, BigDecimal value, int score) {
		super();
		this.description = description;
		this.value = value;
		this.score = score;
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
