package org.djv.stockresearcher.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BuffetAnalysis {
	
	public List<BuffetDetail> details = new ArrayList<BuffetDetail>();
	
	public void add(String desc, BigDecimal bd, boolean passOrFail, int value){
		details.add(new BuffetDetail(desc, bd, passOrFail, value));
	}
	
	public int getTotalScore(){
		int score = 0;
		for (BuffetDetail detail: details){
			score += detail.getScore();
		}
		
		return score;
	};
	
	public List<BuffetDetail> getDetails() {
		return details;
	}

	public String toString(){
		StringBuffer sb = new StringBuffer();
		for (BuffetDetail detail: details){
			sb.append(detail.getDescription() + " " + String.valueOf(detail.getValue() + " " + detail.getScore()) + "\n");
		}
		sb.append("*** totalScore = " + getTotalScore() + " ***");
		return sb.toString();
	}

}
