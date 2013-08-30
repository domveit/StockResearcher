package org.djv.stockresearcher.db;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.djv.stockresearcher.model.DivData;
import org.djv.stockresearcher.model.DivYearData;
import org.djv.stockresearcher.model.StockData;

public class StockDataUtil {
	
	public static void crunchDividends(StockData stockData) {
		normalizeDivs(stockData);
		buildDivYearData(stockData);
		normalizeYearDivs(stockData);
		calcDivStats(stockData);
		calcRankings(stockData);
	}
	
	public static void buildDivYearData(StockData stockData) {
		List<DivData> ddl = stockData.getDivData();
		
		Map<Integer, DivYearData> divYearData = new TreeMap<Integer, DivYearData>();

		Calendar c = Calendar.getInstance();
		int currYYYY = c.get(Calendar.YEAR);
		
		for (Integer y = currYYYY; y > 1960; y --){
			DivYearData dyd = new DivYearData();
			dyd.setYear(y);
			divYearData.put(y, dyd);
		}
		
		int lastYear = currYYYY;
		for (int i = 0; i < ddl.size() ; i ++){
			DivData dd = ddl.get(i);
			Integer year = dd.getDate().get(Calendar.YEAR);
			lastYear = year;
			DivYearData dyd = divYearData.get(year);
			dyd.setDiv(dyd.getDiv().add(dd.getDividend()));
			dyd.setNormalizedDiv(dyd.getNormalizedDiv().add(dd.getNormalizedDivided()));
			dyd.getDivDetail().add(dd);
		}
		
		for (Integer y = lastYear - 1; y > 1960; y --){
			divYearData.remove(y);
		}
		stockData.setDivYearData(divYearData);
	}

	public static void calcDivStats(StockData stockData) {
		Map<Integer, DivYearData> divYearData = stockData.getDivYearData();
		Calendar c = Calendar.getInstance();
		int currYYYY = c.get(Calendar.YEAR);
		boolean stopStreak = false;
		int streak = 0;
		int skipped = 0;
		
		Double divNow = null;
		Double div4yr = null;
		Double div8yr = null;
		
		for (Integer y = currYYYY; y > 1960; y --){
			if (y == currYYYY){
				continue;
			}
			
			DivYearData dyd = divYearData.get(y);
			if (dyd == null){
				 continue;
			}
			BigDecimal currDiv = dyd.getNormalizedDiv();
			
			if (y == currYYYY - 1){
				divNow = currDiv.doubleValue();
			}
			
			if (y == currYYYY - 5){
				div4yr = currDiv.doubleValue();
			}
			
			if (y == currYYYY - 9){
				div8yr = currDiv.doubleValue();
			}
			
			DivYearData dydPrevYr = divYearData.get(y-1);
			if (dydPrevYr == null){
				 continue;
			}
			BigDecimal prevDiv = dydPrevYr.getNormalizedDiv();
			
			if (prevDiv.compareTo(currDiv) > 0){
				stopStreak = true;
			}
			
			if (prevDiv.compareTo(currDiv) == 0 && !stopStreak){
				skipped ++;
			}
			
			BigDecimal pctRaise;
			if (currDiv.compareTo(BigDecimal.ZERO) == 0){
				pctRaise = BigDecimal.ZERO;
			} else {
				pctRaise =  (currDiv.subtract(prevDiv)).multiply(new BigDecimal(100)).divide(currDiv, 2, RoundingMode.UP);
			}
			dyd.setPctIncreaseOverPreviousYear(pctRaise);
			if (!stopStreak){
				streak++;
			}
		}
		
		if (divNow != null){
			if (div4yr != null && div4yr > 0){
				double d = divNow / div4yr;
				double pow = Math.pow(d, .2d);
				double gr4y = (pow - 1) * 100;
				stockData.setDg4(gr4y);
			}
			if (div8yr != null && div8yr > 0){
				double d = divNow / div8yr;
				double pow = Math.pow(d, .1d);
				double gr8y = (pow - 1) * 100;
				stockData.setDg8(gr8y);
			} 
		}
				
		stockData.setStreak(streak);
		stockData.setSkipped(skipped);
	}

	private static void normalizeYearDivs(StockData sd) {
		Map<Integer, DivYearData> divYearData = sd.getDivYearData();
		Calendar c = Calendar.getInstance();
		int currYYYY = c.get(Calendar.YEAR);
		for (Integer y = currYYYY - 1; y > 1960; y --){
			
			DivYearData dyd = divYearData.get(y);
			DivYearData nextdyd = divYearData.get(y + 1);
			DivYearData prevdyd = divYearData.get(y - 1);
			if (dyd == null){
				continue;
			}
			int normNbrPayments = dyd.getDivDetail().size();
			if (prevdyd == null && nextdyd == null){
				continue;
			} else if (prevdyd != null && nextdyd == null){
				normNbrPayments = prevdyd.getDivDetail().size();
			} else if (prevdyd == null && nextdyd != null){
				normNbrPayments = nextdyd.getDivDetail().size();
			}  else {
				if (prevdyd.getDivDetail().size() !=  nextdyd.getDivDetail().size()) {
					continue;
				}
				normNbrPayments = prevdyd.getDivDetail().size();
			}
			
			if(dyd.getDivDetail().size() > normNbrPayments){
				DivData maxdd = null;
				for (DivData dd : dyd.getDivDetail()){
					if (maxdd == null){
						maxdd = dd;
					}
					maxdd = (dd.getDividend().compareTo(maxdd.getDividend()) > 0 ) ? dd : maxdd; 
				}
				if (maxdd != null ){
					maxdd.setNormalizedDivided(new BigDecimal("0.00"));
					dyd.setNormalizedDiv(new BigDecimal("0.00"));
					for (DivData dd : dyd.getDivDetail()){
						dyd.setNormalizedDiv(dyd.getNormalizedDiv().add(dd.getNormalizedDivided()));
					}
				}
			}
		}
		DivYearData lastYear = divYearData.get(currYYYY - 1);
		if (lastYear != null && !"N/A".equals(sd.getPrice())){
			sd.setNormDividend(lastYear.getNormalizedDiv().toString());
			sd.setNormYield(lastYear.getNormalizedDiv().multiply(new BigDecimal(100)).divide(new BigDecimal(sd.getPrice()), RoundingMode.HALF_UP).doubleValue());
		} else {
			sd.setNormDividend(sd.getDividend());
			sd.setNormYield(sd.getYield());
		}
	}

	public static void calcRankings(StockData stockData) {
		int yr = 0;
		if (stockData.getNormYield() != null){
			if (stockData.getNormYield() >= 1.0d){
				yr = 1;
			} 
			if (stockData.getNormYield() >= 2.0d){
				yr = 2;
			} 
			if (stockData.getNormYield() >= 3.0d){
				yr = 3;
			} 
			if (stockData.getNormYield() >= 3.5d){
				yr = 4;
			} 
			if (stockData.getNormYield() >= 4.0d){
				yr = 5;
			}
			if (stockData.getNormYield() >= 4.5d){
				yr = 6;
			} 
			if (stockData.getNormYield() >= 5.0d){
				yr = 7;
			} 
			if (stockData.getNormYield() >= 6.0d){
				yr = 8;
			} 
			if (stockData.getNormYield() >= 7.5d){
				yr = 9;
			}
			if (stockData.getNormYield() >= 9.0d){
				yr = 10;
			}
			if (stockData.getNormYield() >= 15.0d){
				yr = 7;
			} 
			if (stockData.getNormYield() >= 25.0d){
				yr = 4;
			} 
			if (stockData.getNormYield() >= 50.0d){
				yr = 0;
			} 
		}
		
		int sr = 0;
		if (stockData.getStreak() >= 1){
			sr = 1;
		} 
		if (stockData.getStreak() >= 2){
			sr = 2;
		} 		
		if (stockData.getStreak() >= 3){
			sr = 3;
		} 		
		if (stockData.getStreak() >= 4){
			sr = 4;
		} 		
		if (stockData.getStreak() >= 5){
			sr = 5;
		} 
		if (stockData.getStreak() >= 6 && stockData.getSkipped() <= 1){
			sr = 6;
		} 
		if (stockData.getStreak() >= 8 && stockData.getSkipped() <= 1){
			sr = 7;
		} 		
		if (stockData.getStreak() >= 10 && stockData.getSkipped() <= 1){
			sr = 8;
		} 		
		if (stockData.getStreak() >= 12 && stockData.getSkipped() <= 1){
			sr = 9;
		} 		
		if (stockData.getStreak() >= 15 && stockData.getSkipped() <= 1){
			sr = 10;
		} 
		
		int gr = 0;
		if (stockData.getDg4() != null && stockData.getDg8() != null){
			if (stockData.getDg4() >= 2){
				gr = 1;
			} 
			if (stockData.getDg4() >= 3){
				gr = 2;
			} 
			if (stockData.getDg4() >= 4){
				gr = 3;
			} 
			if (stockData.getDg4() >= 5){
				gr = 4;
			} 
			if (stockData.getDg4() >= 5 && stockData.getDg8() >=3){
				gr = 5;
			} 
			if (stockData.getDg4() >= 6 && stockData.getDg8() >=4){
				gr = 6;
			} 
			if (stockData.getDg4() >= 8 && stockData.getDg8() >=5){
				gr = 7;
			} 
			if (stockData.getDg4() >= 10 && stockData.getDg8() >=6){
				gr = 8;
			} 
			if (stockData.getDg4() >= 12 && stockData.getDg8() >=8){
				gr = 9;
			} 
			if (stockData.getDg4() >= 14 && stockData.getDg8() >=10){
				gr = 10;
			} 
		}
		
		double overall = (yr + sr + gr) / 3.0d;
		stockData.setYieldRank(yr);
		stockData.setStalwartRank(sr);
		stockData.setGrowthRank(gr);
		stockData.setOverAllRank(overall);
	}

	private static void normalizeDivs(StockData sd) {
		List<DivData> ddl = sd.getDivData();
		double stdDev = getStdDev(sd);
		sd.setStdDev(stdDev);
		sd.setWildness(getWildness(sd));
		for (int i = 0; i < ddl.size(); i ++){
			DivData dd = ddl.get(i);
			DivData prevdd = null;
			if (i > 0){
				prevdd = ddl.get(i-1);
			}
			
			DivData nextdd = null;
			if (i < ddl.size() - 1){
				nextdd = ddl.get(i+1);
			}
			BigDecimal curr = dd.getDividend(); 
			BigDecimal avg = null;
			if (prevdd != null && nextdd != null){
				avg = prevdd.getDividend().add(nextdd.getDividend()).divide(new BigDecimal(2));
			} else if (prevdd != null && nextdd == null){
				avg = prevdd.getDividend() ;
			} else if (prevdd == null && nextdd != null){
				avg = nextdd.getDividend();
			} else {
				avg = curr;
			}
			
			double numStdDevs = (curr.doubleValue() - avg.doubleValue()) / stdDev;
			if (numStdDevs > 2){
				dd.setNormalizedDivided(avg);
			} else {
				dd.setNormalizedDivided(dd.getDividend());
			}
		}
	}
	
	static double getMean(StockData sd) {
		double sum = 0.0;
		for (DivData dd : sd.getDivData()) {
			sum += dd.getDividend().doubleValue();
		}
		return sum / sd.getDivData().size();
	}

	static double getVariance(StockData sd) {
		double mean = getMean(sd);
		double temp = 0;
		for (DivData dd : sd.getDivData()) {
			double a = dd.getDividend().doubleValue();
			temp += (mean - a) * (mean - a);
		}
		return temp / sd.getDivData().size();
	}

	static double getStdDev(StockData sd) {
		if (sd.getDivData().size() == 0){
			return 0d;
		}
		return Math.sqrt(getVariance(sd));
	}
	
	static double getWildness(StockData sd) {
		if (sd.getDivData().size() == 0){
			return 0d;
		}
		double mean = getMean(sd);
		double temp = 0;
		for (DivData dd : sd.getDivData()) {
			double a = dd.getDividend().doubleValue();
			temp += (mean - a) * (mean - a);
		}
		double variance =  temp / sd.getDivData().size();
		return  (Math.sqrt(variance) / mean) * 100;
	}

	public static void crunchFinancials(StockData stockData) {
		
	}


}
