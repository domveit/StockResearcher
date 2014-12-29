package org.djv.stockresearcher.db;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import org.djv.stockresearcher.model.BuffetAnalysis;
import org.djv.stockresearcher.model.FinDataPeriod;
import org.djv.stockresearcher.model.FinDataRow;
import org.djv.stockresearcher.model.FinDataTable;
import org.djv.stockresearcher.model.StockData;

public class BuffetologyService {
	
	public BuffetAnalysis buffetize(StockData sd) throws Exception {
		BuffetAnalysis analysis = new BuffetAnalysis();
		
		FinDataTable is = StockDataUtil.getIncomeStatement(sd);
		
		// calc average profit margin over 5 years

		List<FinDataRow> rows = is.getRows();
		List<FinDataPeriod> periods = is.getPeriods();
		
		Map<FinDataPeriod, BigDecimal> revData = null;
		Map<FinDataPeriod, BigDecimal> cogsData = null;
		Map<FinDataPeriod, BigDecimal> grossProfitData = null;
		Map<FinDataPeriod, BigDecimal> sgaData = null;
		Map<FinDataPeriod, BigDecimal> rndData = null;
		Map<FinDataPeriod, BigDecimal> daData = null;
		
		for (FinDataRow row: rows){
			if ("Revenue".equals(row.getName())){
				revData = is.getDataMap().get(row);
			}
			
			if ("Cost of revenue".equals(row.getName())){
				cogsData = is.getDataMap().get(row);
			}
			if ("Gross profit".equals(row.getName())){
				grossProfitData = is.getDataMap().get(row);
			}
			if ("Sales, General and administrative".equals(row.getName())){
				sgaData = is.getDataMap().get(row);
			}
			if ("Research and development".equals(row.getName())){
				rndData = is.getDataMap().get(row);
			}
			
			if ("Depreciation and amortization".equals(row.getName())){
				daData = is.getDataMap().get(row);
			}
		}
		
		if (grossProfitData == null && cogsData == null){
			grossProfitData = revData;
		}
		
		if (revData != null && grossProfitData != null){
			int nbrPeriods = 0;
			BigDecimal profitMarginBucket = BigDecimal.ZERO;
			
			for (FinDataPeriod period:periods){
				if ("TTM".equals(period.getName())){
					continue;
				}
				BigDecimal revThisYear = revData.get(period);
				BigDecimal grossProfitThisYear = grossProfitData.get(period);
				BigDecimal profitMarginThisYear = grossProfitThisYear.divide(revThisYear, 4, RoundingMode.HALF_UP);
				profitMarginBucket = profitMarginBucket.add(profitMarginThisYear);
				nbrPeriods++;
			}
			
			BigDecimal avgProfitMargin = profitMarginBucket.divide(BigDecimal.valueOf(nbrPeriods), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
	
			
			if (avgProfitMargin.doubleValue() < 30.0){
				analysis.add("Profit Margin under 30%", avgProfitMargin, -1);
			}
			
			if (avgProfitMargin.doubleValue() > 40.0){
				analysis.add("Profit Margin over 40%", avgProfitMargin, 1);
			}
			
			if (avgProfitMargin.doubleValue() > 60.0){
				analysis.add("Profit Margin over 60%", avgProfitMargin, 1);
			}
			
		}
		
		if (sgaData != null && grossProfitData != null){
			int nbrPeriods = 0;
			int nbrPeriodsOver90 = 0;
			BigDecimal sgaBucket = BigDecimal.ZERO;
			
			for (FinDataPeriod period:periods){
				if ("TTM".equals(period.getName())){
					continue;
				}
				BigDecimal sgaThisYear = sgaData.get(period);
				BigDecimal grossProfitThisYear = grossProfitData.get(period);
				BigDecimal sgaMarginThisYear = sgaThisYear.divide(grossProfitThisYear, 4, RoundingMode.HALF_UP);
				if (sgaMarginThisYear.doubleValue() > .9){
					nbrPeriodsOver90 ++;
				}
				sgaBucket = sgaBucket.add(sgaMarginThisYear);
				nbrPeriods++;
			}
			
			BigDecimal avgsga = sgaBucket.divide(BigDecimal.valueOf(nbrPeriods), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
	
			if (avgsga.doubleValue() < 80.0){
				analysis.add("SG&A less than 80% of gross profit", avgsga, 1);
			}
			if (avgsga.doubleValue() < 50.0){
				analysis.add("SG&A less than 50% of gross profit", avgsga, 1);
			}
			if (avgsga.doubleValue() < 30.0){
				analysis.add("SG&A less than 30% of gross profit", avgsga, 1);
			}
			if (nbrPeriodsOver90 != 0 ){
				analysis.add("Periods with SG&A cost over 90% of gross profit", BigDecimal.valueOf(nbrPeriodsOver90), 0 - nbrPeriodsOver90);
			}
			
		}
		
		if (rndData == null){
			analysis.add("No R&D", null, 1);
		} else {
			if (grossProfitData != null){
				int nbrPeriods = 0;
				BigDecimal rndBucket = BigDecimal.ZERO;
				
				for (FinDataPeriod period:periods){
					if ("TTM".equals(period.getName())){
						continue;
					}
					BigDecimal rndThisYear = rndData.get(period);
					BigDecimal grossProfitThisYear = grossProfitData.get(period);
					BigDecimal rndPctThisYear = rndThisYear.divide(grossProfitThisYear, 4, RoundingMode.HALF_UP);
					rndBucket = rndBucket.add(rndPctThisYear);
					nbrPeriods++;
				}
				
				BigDecimal avgrnd = rndBucket.divide(BigDecimal.valueOf(nbrPeriods), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
		
				if (avgrnd.doubleValue() < 10.0){
					analysis.add("R&D less than 10% of gross profit", avgrnd, 1);
				}
				if (avgrnd.doubleValue() > 25.0){
					analysis.add("R&D over 25% of gross profit", avgrnd, -1);
				}
			}
		}
		
		if (daData == null){
			analysis.add("No depreciation", null, 1);
		} else {
			if (grossProfitData != null){
				int nbrPeriods = 0;
				BigDecimal daBucket = BigDecimal.ZERO;
				
				for (FinDataPeriod period:periods){
					if ("TTM".equals(period.getName())){
						continue;
					}
					BigDecimal daThisYear = daData.get(period);
					BigDecimal grossProfitThisYear = grossProfitData.get(period);
					BigDecimal daPctThisYear = daThisYear.divide(grossProfitThisYear, 4, RoundingMode.HALF_UP);
					daBucket = daBucket.add(daPctThisYear);
					nbrPeriods++;
				}
				
				BigDecimal avgda = daBucket.divide(BigDecimal.valueOf(nbrPeriods), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
		
				if (avgda.doubleValue() < 10.0){
					analysis.add("Depreciation less than 10% of gross profit", avgda, 1);
				}
				
				if (avgda.doubleValue() > 25.0){
					analysis.add("Depreciation over 25% of gross profit", avgda, -1);
				}
			}
		}
		
		return analysis;
		
	}

}
