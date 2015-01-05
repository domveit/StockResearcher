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
		
		FinDataTable is = StockDB.getInstance().getIncomeStatement(sd);
		FinDataTable bs = StockDB.getInstance().getBalanceSheet(sd);
		
		List<FinDataPeriod> periods = is.getPeriods();
		
		FinDataPeriod lastPeriod = null;
		FinDataPeriod secondLastPeriod = null;
		
		for (FinDataPeriod period : bs.getPeriods()){
			if ("TTM".equals(period.getName())){
				continue;
			}
			if (lastPeriod == null){
				lastPeriod = period;
				continue;
			} else {
				if (period.getYear() > lastPeriod.getYear()){
					secondLastPeriod = lastPeriod;
					lastPeriod = period;
				}
			}
		}
		
		Map<FinDataPeriod, BigDecimal> revData = null;
		Map<FinDataPeriod, BigDecimal> cogsData = null;
		Map<FinDataPeriod, BigDecimal> grossProfitData = null;
		Map<FinDataPeriod, BigDecimal> sgaData = null;
		Map<FinDataPeriod, BigDecimal> rndData = null;
		Map<FinDataPeriod, BigDecimal> daData = null;
		Map<FinDataPeriod, BigDecimal> intData = null;
		Map<FinDataPeriod, BigDecimal> ebitData = null;
		Map<FinDataPeriod, BigDecimal> netIncomeData = null;
		Map<FinDataPeriod, BigDecimal> sharesData = null;
		
		boolean foundWASO = false;
		
		for (FinDataRow row: is.getRows()){
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
			if ("Interest Expense".equals(row.getName())){
				intData = is.getDataMap().get(row);
			}
			if ("Income before taxes".equals(row.getName())){
				ebitData = is.getDataMap().get(row);
			}
			if ("Income before income taxes".equals(row.getName())){
				ebitData = is.getDataMap().get(row);
			}
			if ("Net income available to common shareholders".equals(row.getName())){
				netIncomeData = is.getDataMap().get(row);
			}
			if ("Weighted average shares outstanding".equals(row.getName())){
				foundWASO = true;
			}
			
			if (foundWASO && "Basic".equals(row.getName())){
				sharesData = is.getDataMap().get(row);
			}
		}
		
		if (grossProfitData == null && cogsData == null){
			grossProfitData = revData;
		}
		
		if (ebitData != null){
			for (FinDataPeriod period:periods){
				if ("TTM".equals(period.getName())){
					BigDecimal ebit = ebitData.get(period);
					if (ebit == null){
						ebit = BigDecimal.ZERO;
					}
					
					ebit = ebit.multiply(BigDecimal.valueOf(1000000));
					String marketCapStr = sd.getStock().getMarketCap();
					BigDecimal marketCap = convertMarketCap(marketCapStr);
					if (marketCap != null && marketCap.compareTo(BigDecimal.ZERO) > 0){
						BigDecimal ebitYield = ebit.multiply(BigDecimal.valueOf(100)).divide(marketCap, 2, RoundingMode.HALF_UP);
	
						if (ebitYield.doubleValue() >= 6.0){
							analysis.add("Earnings yield over 6%", ebitYield, true, 0);
						} else {
							analysis.add("Earnings yield over 6%", ebitYield, false, -1);
						}
						
						if (ebitYield.doubleValue() >= 12.0){
							analysis.add("Earnings yield over 12%", ebitYield, true, 1);
						} else {
							analysis.add("Earnings yield over 12%", ebitYield, false, 0);
						}
						
						if (ebitYield.doubleValue() >= 18.0){
							analysis.add("Earnings yield over 18%", ebitYield, true, 1);
						} else {
							analysis.add("Earnings yield over 18%", ebitYield, false, 0);
						}
						
						if (ebitYield.doubleValue() >= 24.0){
							analysis.add("Earnings yield over 24%", ebitYield, true, 1);
						} else {
							analysis.add("Earnings yield over 24%", ebitYield, false, 0);
						}
					}
				}
			}
			
		}
		
		if (revData != null && grossProfitData != null){
			int nbrPeriods = 0;
			BigDecimal profitMarginBucket = BigDecimal.ZERO;
			
			for (FinDataPeriod period:periods){
				if ("TTM".equals(period.getName())){
					continue;
				}
				BigDecimal revThisYear = revData.get(period);
				if (revThisYear == null){
					continue;
				}
				BigDecimal grossProfitThisYear = grossProfitData.get(period);
				if (grossProfitThisYear == null){
					grossProfitThisYear = BigDecimal.ZERO;
				}
				if (revThisYear != null && revThisYear.compareTo(BigDecimal.ZERO) > 0){
					BigDecimal profitMarginThisYear = grossProfitThisYear.divide(revThisYear, 2, RoundingMode.HALF_UP);
					profitMarginBucket = profitMarginBucket.add(profitMarginThisYear);
					nbrPeriods++;
				}
			}
			if (nbrPeriods > 0){
				BigDecimal avgProfitMargin = profitMarginBucket.divide(BigDecimal.valueOf(nbrPeriods), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
				
				if (avgProfitMargin.doubleValue() >= 30.0){
					analysis.add("Profit Margin over 30%", avgProfitMargin, true, 0);
				} else {
					analysis.add("Profit Margin over 30%", avgProfitMargin, false, -1);
				}
				
				if (avgProfitMargin.doubleValue() >= 40.0){
					analysis.add("Profit Margin over 40%", avgProfitMargin, true, 1);
				} else {
					analysis.add("Profit Margin over 40%", avgProfitMargin, false, 0);
				}
				
				if (avgProfitMargin.doubleValue() >= 50.0){
					analysis.add("Profit Margin over 50%", avgProfitMargin, true, 1);
				} else {
					analysis.add("Profit Margin over 50%", avgProfitMargin, false, 0);
				}
				
				if (avgProfitMargin.doubleValue() >= 60.0){
					analysis.add("Profit Margin over 60%", avgProfitMargin, true, 1);
				} else {
					analysis.add("Profit Margin over 60%", avgProfitMargin, false, 0);
				}
			}
		}
		
		Map<FinDataPeriod, BigDecimal> inventoryData = null;
		
		for (FinDataRow row: bs.getRows()){
			if ("Inventories".equals(row.getName())){
				inventoryData = bs.getDataMap().get(row);
			}
		}
		
		if (inventoryData != null && cogsData != null){
			BigDecimal inventory1 = inventoryData.get(lastPeriod);
			BigDecimal inventory2 = inventoryData.get(secondLastPeriod);
			
			if (inventory1 != null && inventory2 != null){
			
				BigDecimal avgInventory = inventory1.add(inventory2).divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
				
				BigDecimal cogs = cogsData.get(lastPeriod);
				if (avgInventory != null && avgInventory.compareTo(BigDecimal.ZERO) > 0 && cogs != null){
					BigDecimal inventoryTurnover = cogs.divide(avgInventory, 2, RoundingMode.HALF_UP);
					if (inventoryTurnover.doubleValue() > 5.0){
						analysis.add("Inventory turnover greater than 5", inventoryTurnover, true, 0);
					} else {
						analysis.add("Inventory turnover greater than 5", inventoryTurnover, false, -1);
					}
					
					if (inventoryTurnover.doubleValue() > 10.0){
						analysis.add("Inventory turnover more than 10", inventoryTurnover, true, 1);
					} else {
						analysis.add("Inventory turnover more than 10", inventoryTurnover, false, 0);
					}
					
					if (inventoryTurnover.doubleValue() > 20.0){
						analysis.add("Inventory turnover more than 20", inventoryTurnover, true, 1);
					} else {
						analysis.add("Inventory turnover more than 20", inventoryTurnover, false, 0);
					}
					
					if (inventoryTurnover.doubleValue() > 40.0){
						analysis.add("Inventory turnover more than 40", inventoryTurnover, true, 1);
					} else {
						analysis.add("Inventory turnover more than 40", inventoryTurnover, false, 0);
					}
				}
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
				if (sgaThisYear == null){
					continue;
				}
				BigDecimal grossProfitThisYear = grossProfitData.get(period);
				if (grossProfitThisYear == null){
					grossProfitThisYear = BigDecimal.ZERO;
				}
				BigDecimal sgaMarginThisYear;
				
				if (grossProfitThisYear == null || grossProfitThisYear.compareTo(BigDecimal.ZERO) <= 0){
					sgaMarginThisYear = BigDecimal.valueOf(1);
				} else {
					sgaMarginThisYear = sgaThisYear.divide(grossProfitThisYear, 2, RoundingMode.HALF_UP);
				}
				sgaBucket = sgaBucket.add(sgaMarginThisYear);
				
				if (sgaMarginThisYear.doubleValue() > .9){
					nbrPeriodsOver90 ++;
				}
				
				nbrPeriods++;
			}
			if (nbrPeriods > 0){
				BigDecimal avgsga = sgaBucket.divide(BigDecimal.valueOf(nbrPeriods), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
		
				if (avgsga.doubleValue() < 80.0){
					analysis.add("SG&A less than 80% of gross profit", avgsga, true, 0);
				} else {
					analysis.add("SG&A less than 80% of gross profit", avgsga, false, -1);
				}
				if (avgsga.doubleValue() < 60.0){
					analysis.add("SG&A less than 60% of gross profit", avgsga, true, 1);
				} else {
					analysis.add("SG&A less than 60% of gross profit", avgsga, false, 0);
				}
				if (avgsga.doubleValue() < 40.0){
					analysis.add("SG&A less than 40% of gross profit", avgsga, true, 1);
				} else {
					analysis.add("SG&A less than 40% of gross profit", avgsga, false, 0);
				}
				if (avgsga.doubleValue() < 30.0){
					analysis.add("SG&A less than 30% of gross profit", avgsga, true, 1);
				} else {
					analysis.add("SG&A less than 30% of gross profit", avgsga, false, 0);
				}
				if (nbrPeriodsOver90 != 0 ){
					analysis.add("Periods with SG&A over 90% of gross profit", BigDecimal.valueOf(nbrPeriodsOver90), false, 0 - nbrPeriodsOver90);
				}
			}
		}
		
		if (rndData == null){
			analysis.add("No R&D", null, true, 1);
		} else {
			if (grossProfitData != null && revData != null){
				int nbrPeriods = 0;
				BigDecimal rndBucket = BigDecimal.ZERO;
				
				for (FinDataPeriod period:periods){
					if ("TTM".equals(period.getName())){
						continue;
					}
					BigDecimal rndThisYear = rndData.get(period);
					if (rndThisYear == null){
						BigDecimal revThisYear = revData.get(period);
						if (revThisYear == null){
							continue;
						} else {
							rndThisYear = BigDecimal.ZERO;
						}
					}
					BigDecimal grossProfitThisYear = grossProfitData.get(period);
					if (grossProfitThisYear == null){
						grossProfitThisYear = BigDecimal.ZERO;
					}
					
					if (grossProfitThisYear == null || grossProfitThisYear.compareTo(BigDecimal.ZERO) <= 0){
						rndBucket = rndBucket.add(BigDecimal.valueOf(1));
					} else {
						BigDecimal rndPctThisYear = rndThisYear.divide(grossProfitThisYear, 2, RoundingMode.HALF_UP);
						rndBucket = rndBucket.add(rndPctThisYear);
					}

					nbrPeriods++;
				}
				if (nbrPeriods > 0){
					BigDecimal avgrnd = rndBucket.divide(BigDecimal.valueOf(nbrPeriods), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
			
					if (avgrnd.doubleValue() <= 10.0){
						analysis.add("R&D less than 10% of gross profit", avgrnd, true, 1);
					}
					if (avgrnd.doubleValue() > 10.0 && avgrnd.doubleValue() <= 25.0 ){
						analysis.add("R&D between 10% and 25% of gross profit", avgrnd, true, 0);
					}
					if (avgrnd.doubleValue() > 25.0){
						analysis.add("R&D over 25% of gross profit", avgrnd, false, -1);
					}
				}
			}
		}
		
		if (daData == null){
			analysis.add("No depreciation", null, true, 1);
		} else {
			if (grossProfitData != null){
				int nbrPeriods = 0;
				BigDecimal daBucket = BigDecimal.ZERO;
				
				for (FinDataPeriod period:periods){
					if ("TTM".equals(period.getName())){
						continue;
					}
					BigDecimal daThisYear = daData.get(period);
					
					if (daThisYear == null){
						BigDecimal revThisYear = revData.get(period);
						if (revThisYear == null){
							continue;
						} else {
							daThisYear = BigDecimal.ZERO;
						}
					}
					
					BigDecimal grossProfitThisYear = grossProfitData.get(period);
					if (grossProfitThisYear == null){
						grossProfitThisYear = BigDecimal.ZERO;
					}
					if (grossProfitThisYear == null || grossProfitThisYear.compareTo(BigDecimal.ZERO) <= 0){
						daBucket = daBucket.add(BigDecimal.valueOf(1));
					} else {
						BigDecimal daPctThisYear = daThisYear.divide(grossProfitThisYear, 2, RoundingMode.HALF_UP);
						daBucket = daBucket.add(daPctThisYear);
					}

					nbrPeriods++;
				}
				if (nbrPeriods > 0){
					BigDecimal avgda = daBucket.divide(BigDecimal.valueOf(nbrPeriods), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
			
					if (avgda.doubleValue() < 10.0){
						analysis.add("Depreciation less than 10% of gross profit", avgda, true, 1);
					}
					
					if (avgda.doubleValue() > 10.0 && avgda.doubleValue() <= 25.0 ){
						analysis.add("Depreciation between 10% and 25% of gross profit", avgda, true, 0);
					}
					
					if (avgda.doubleValue() > 25.0){
						analysis.add("Depreciation over 25% of gross profit", avgda, false, -1);
					}
				}
			}
		}
		
		if (intData == null){
			analysis.add("No Interest expense", null, true, 1);
		} else {
			if (intData != null && revData != null && grossProfitData != null){
				int nbrPeriods = 0;
				BigDecimal intBucket = BigDecimal.ZERO;
				
				for (FinDataPeriod period:periods){
					if ("TTM".equals(period.getName())){
						continue;
					}
					BigDecimal intThisYear = intData.get(period);
					
					if (intThisYear == null){
						BigDecimal revThisYear = revData.get(period);
						if (revThisYear == null){
							continue;
						} else {
							intThisYear = BigDecimal.ZERO;
						}
					}
					
					BigDecimal grossProfitThisYear = grossProfitData.get(period);
					if (grossProfitThisYear == null){
						grossProfitThisYear = BigDecimal.ZERO;
					}
					if (grossProfitThisYear == null || grossProfitThisYear.compareTo(BigDecimal.ZERO) <= 0){
						intBucket = intBucket.add(BigDecimal.valueOf(1));
					} else {
						BigDecimal intPctThisYear = intThisYear.divide(grossProfitThisYear, 2, RoundingMode.HALF_UP);
						intBucket = intBucket.add(intPctThisYear);
					}
					nbrPeriods++;
				}
				if (nbrPeriods > 0){
					BigDecimal avgint = intBucket.divide(BigDecimal.valueOf(nbrPeriods), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
			
					if (avgint.doubleValue() < 15.0){
						analysis.add("Interest expense less than 15% of gross profit", avgint, true, 1);
					}
					
					if (avgint.doubleValue() > 15.0 && avgint.doubleValue() <= 30.0 ){
						analysis.add("Interest expense between 15% and 30% of gross profit", avgint, true, 0);
					}
					
					if (avgint.doubleValue() > 30.0){
						analysis.add("Interest expense over 30% of gross profit", avgint, false, -1);
					}
				}
			}
		}
		
		if (netIncomeData == null) {
			
		} else if (revData != null) {
				int firstYear = 9999;
				int lastYear = 0;
				BigDecimal firstIncome = null;
				BigDecimal lastIncome = null;
				
				BigDecimal incomeRevBucket = BigDecimal.ZERO;
				
				int nbrPeriods = 0;
				
				for (FinDataPeriod period:periods){
					if ("TTM".equals(period.getName())){
						continue;
					}
					
					BigDecimal incomeThisYear = netIncomeData.get(period);
					BigDecimal revThisYear = revData.get(period);
					
					if (incomeThisYear == null){
						if (revThisYear == null){
							continue;
						} else {
							incomeThisYear = BigDecimal.ZERO;
						}
					}
					if (revThisYear != null && revThisYear.compareTo(BigDecimal.ZERO) > 0){
						BigDecimal incomeRevRatioThisYear = incomeThisYear.multiply(BigDecimal.valueOf(100)).divide(revThisYear, 2, RoundingMode.HALF_UP);
						incomeRevBucket = incomeRevBucket.add(incomeRevRatioThisYear);
						nbrPeriods ++;
					}
					
					if (period.getYear() < firstYear && incomeThisYear != null){
						firstYear = period.getYear();
						firstIncome = incomeThisYear;
					}
					
					if (period.getYear() > lastYear && incomeThisYear != null){
						lastYear = period.getYear();
						lastIncome = netIncomeData.get(period);
					}
					
				}
				
				if (firstYear != 9999 && lastYear != 0 && firstYear != lastYear && firstIncome != null && firstIncome.compareTo(BigDecimal.ZERO) > 0){
					BigDecimal d = lastIncome.divide(firstIncome, 2, RoundingMode.HALF_UP);
					
					double incomeGrowth = 0.0d;
					if (d.compareTo(BigDecimal.ZERO) > 0){
						int nbrYears = lastYear - firstYear;
						double pow = Math.pow(Math.abs(d.doubleValue()), (1.0d / nbrYears));
						incomeGrowth = (pow - 1) * 100;
					}
					
					BigDecimal incomeGrowthBD = BigDecimal.valueOf(incomeGrowth).divide(BigDecimal.ONE, 2, RoundingMode.HALF_UP);
					
					if (incomeGrowth > 4.0){
						analysis.add("Income growth more than 4% YOY", incomeGrowthBD, true, 0);
					} else {
						analysis.add("Income growth more than 4% YOY", incomeGrowthBD, false, -1);
					}
					
					if (incomeGrowth > 8.0){
						analysis.add("Income growth more than 8% YOY", incomeGrowthBD, true, 1);
					} else {
						analysis.add("Income growth more than 8% YOY", incomeGrowthBD, false, 0);
					}
					
					if (incomeGrowth > 12.0){
						analysis.add("Income growth more than 12% YOY", incomeGrowthBD, true, 1);
					} else {
						analysis.add("Income growth more than 12% YOY", incomeGrowthBD, false, 0);
					}
					
					if (incomeGrowth > 16.0){
						analysis.add("Income growth more than 16% YOY", incomeGrowthBD, true, 1);
					} else {
						analysis.add("Income growth more than 16% YOY", incomeGrowthBD, false, 0);
					}
				}
				if (nbrPeriods > 0){
					BigDecimal avgIncomeRevRatio = incomeRevBucket.divide(BigDecimal.valueOf(nbrPeriods), 2, RoundingMode.HALF_UP);
					
					if (avgIncomeRevRatio.doubleValue() > 10.0){
						analysis.add("Income more than 10% of revenues", avgIncomeRevRatio, true, 0);
					} else {
						analysis.add("Income more than 10% of revenues", avgIncomeRevRatio, false, -1);
					}
					
					if (avgIncomeRevRatio.doubleValue() > 20.0){
						analysis.add("Income more than 20% of revenues", avgIncomeRevRatio, true, 1);
					} else {
						analysis.add("Income more than 20% of revenues", avgIncomeRevRatio, false, 0);
					}
					
					if (avgIncomeRevRatio.doubleValue() > 30.0){
						analysis.add("Income more than 30% of revenues", avgIncomeRevRatio, true, 1);
					} else {
						analysis.add("Income more than 30% of revenues", avgIncomeRevRatio, false, 0);
					}
				}
		}
		
		if (sharesData == null) {
			
		} else {
				int firstYear = 9999;
				int lastYear = 0;
				BigDecimal firstShares = null;
				BigDecimal lastShares = null;
				
				int nbrPeriods = 0;
				
				for (FinDataPeriod period:periods){
					if ("TTM".equals(period.getName())){
						continue;
					}
					
					BigDecimal sharesThisYear = sharesData.get(period);
					
					if (sharesThisYear == null){
						continue;
					}
					
					if (period.getYear() < firstYear){
						firstYear = period.getYear();
						firstShares = sharesThisYear;
					}
					
					if (period.getYear() > lastYear){
						lastYear = period.getYear();
						lastShares = sharesThisYear;
					}
					nbrPeriods ++;
				}
				
				if (firstYear != 9999 && lastYear != 0 && firstYear != lastYear && firstShares != null && firstShares.compareTo(BigDecimal.ZERO) > 0){
					BigDecimal d = lastShares.divide(firstShares, 2, RoundingMode.HALF_UP);
					
					double sharesGrowth = 0.0d;
					if (d.compareTo(BigDecimal.ZERO) > 0){
						int nbrYears = lastYear - firstYear;
						double pow = Math.pow(Math.abs(d.doubleValue()), (1.0d / nbrYears));
						sharesGrowth = (pow - 1) * 100;
					}
					
					BigDecimal sharesGrowthBD = BigDecimal.valueOf(sharesGrowth).divide(BigDecimal.ONE, 2, RoundingMode.HALF_UP);
					
					if (sharesGrowth <= 0.0){
						analysis.add("No growth of shares outstanding", sharesGrowthBD, true, 0);
					} else {
						analysis.add("No growth of shares outstanding", sharesGrowthBD, false, -1);
					}
					
					if (sharesGrowth < 0.0){
						analysis.add("Shares buyback over 0%", sharesGrowthBD, true, 1);
					} else {
						analysis.add("Shares buyback over 0%",  sharesGrowthBD, false, 0);
					}
					
					if (sharesGrowth < -2.0){
						analysis.add("Shares buyback over 2%", sharesGrowthBD, true, 1);
					} else {
						analysis.add("Shares buyback over 2%",  sharesGrowthBD, false, 0);
					}
					
					if (sharesGrowth < -4.0){
						analysis.add("Shares buyback over 4%", sharesGrowthBD, true, 1);
					} else {
						analysis.add("Shares buyback over 4%",  sharesGrowthBD, false, 0);
					}
				}
		}
		return analysis;
	}
	
	private BigDecimal convertMarketCap(String s1) {
		if (s1 == null || s1.equals("N/A") || s1.trim().equals("")){
			return new BigDecimal(0);
		}
		String firstPart = s1.substring(0, s1.length() - 1);
		String lastPart = s1.substring(s1.length() - 1);
		BigDecimal bd = new BigDecimal(firstPart);
		switch(lastPart){
			case "M":
				bd = bd.multiply(new BigDecimal(1000000));	
				break;
			case "B":
				bd = bd.multiply(new BigDecimal(1000000000));	
				break;
			case "K":
				bd = bd.multiply(new BigDecimal(1000));	
				break;
		}
		return bd;
	}

}
