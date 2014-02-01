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
			Integer year = dd.getPayDateCal().get(Calendar.YEAR);
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
		Double div5yr = null;
		Double div10yr = null;
		
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
			
			if (y == currYYYY - 6){
				div5yr = currDiv.doubleValue();
			}
			
			if (y == currYYYY - 11){
				div10yr = currDiv.doubleValue();
			}
			
			DivYearData dydPrevYr = divYearData.get(y-1);
			if (dydPrevYr == null){
				 continue;
			}
			BigDecimal prevDiv = dydPrevYr.getNormalizedDiv();
			
			if (prevDiv.compareTo(currDiv) > 0 || currDiv.compareTo(BigDecimal.ZERO) == 0){
				stopStreak = true;
			}
			
			if (prevDiv.compareTo(currDiv) == 0 && !stopStreak && y >= currYYYY - 10){
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
			if (div5yr != null && div5yr > 0){
				double d = divNow / div5yr;
				double pow = Math.pow(d, (1.0 / 5.0));
				double gr5y = (pow - 1) * 100;
				stockData.setDg5(gr5y);
			}
			if (div10yr != null && div10yr > 0){
				double d = divNow / div10yr;
				double pow = Math.pow(d, (1.0 / 10.0));
				double gr10y = (pow - 1) * 100;
				stockData.setDg10(gr10y);
			} 
		}
				
		stockData.setStreak(streak);
		stockData.setSkipped(skipped);
	}

	private static void normalizeYearDivs(StockData sd) {
		Map<Integer, DivYearData> divYearData = sd.getDivYearData();
		Calendar c = Calendar.getInstance();
		int currYYYY = c.get(Calendar.YEAR);
		
		// look for early paid Q4 dividends in 2012 and move them to 2013
		{
		DivYearData dyd = divYearData.get(currYYYY);
		DivYearData dydMinus1 = divYearData.get(currYYYY - 1);
		DivYearData dydMinus2 = divYearData.get(currYYYY - 2);
		DivYearData dydMinus3 = divYearData.get(currYYYY - 3);
		
		if (dyd != null && dydMinus1 != null && dydMinus2!= null && dydMinus3 != null){
			if (dydMinus2.getDivDetail().size() == 4 && dydMinus3.getDivDetail().size() == 4 && dydMinus1.getDivDetail().size() == 5){
				DivData extraQ4Div = null;
				for (DivData dd : dydMinus1.getDivDetail()){
					int em =dd.getPayDateCal().get(Calendar.MONTH);
					if (em == Calendar.DECEMBER || em == Calendar.NOVEMBER){
						extraQ4Div = dd;
						break;
					}
				}
				DivData q1Div = null;
				for (DivData dd : dyd.getDivDetail()){
					int em =dd.getPayDateCal().get(Calendar.MONTH);
					if (em == Calendar.JANUARY || em == Calendar.FEBRUARY|| em == Calendar.MARCH){
						q1Div = dd;
						break;
					}
				}
				
				if (extraQ4Div != null && q1Div == null){
					dydMinus1.getDivDetail().remove(extraQ4Div);
					dydMinus1.setNormalizedDiv(dydMinus1.getNormalizedDiv().subtract(extraQ4Div.getNormalizedDivided()));
					dyd.getDivDetail().add(extraQ4Div);
					dyd.setNormalizedDiv(dyd.getNormalizedDiv().add(extraQ4Div.getNormalizedDivided()));
				}
				
			}
		}
		}
		
		for (Integer y = currYYYY - 1; y > 1960; y --){
			DivYearData dyd = divYearData.get(y);
			
			DivYearData nextdyd = divYearData.get(y + 1);
			DivYearData prevdyd = divYearData.get(y - 1);
			if (dyd == null){
				continue;
			}
			
			if (nextdyd.getYear() == currYYYY){
				nextdyd = divYearData.get(y - 2);
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
		
		int regularPayer = -1;
		
		DivYearData lastYear = divYearData.get(currYYYY -1);
		DivYearData lastYear2 = divYearData.get(currYYYY - 2);
		DivYearData lastYear3 = divYearData.get(currYYYY - 3);
		
		if (lastYear != null && lastYear2 != null && lastYear3 != null){
			if (lastYear.getDivDetail().size() == lastYear2.getDivDetail().size() 
					&& lastYear.getDiv().equals(lastYear2.getNormalizedDiv())
				&& lastYear2.getDivDetail().size() == lastYear3.getDivDetail().size()
				&& lastYear2.getDiv().equals(lastYear3.getNormalizedDiv())
					){
				regularPayer = lastYear.getDivDetail().size();
			}
		}

		if (sd.getStock().getPrice() != null && sd.getStock().getPrice().compareTo(BigDecimal.ZERO) > 0 ){
			if (regularPayer > -1){
				sd.setNormDividend(sd.getDivData().get(0).getDividend().multiply(new BigDecimal(regularPayer)));
				sd.setNormYield(sd.getNormDividend().multiply(new BigDecimal(100)).divide(sd.getStock().getPrice(), RoundingMode.HALF_UP));
			} else {
				if (lastYear != null ){
					sd.setNormDividend(lastYear.getNormalizedDiv());
					sd.setNormYield(lastYear.getNormalizedDiv().multiply(new BigDecimal(100)).divide(sd.getStock().getPrice(), RoundingMode.HALF_UP));
				} else {
					sd.setNormDividend(sd.getStock().getDividend());
					sd.setNormYield(sd.getStock().getYield());
				}
			}
		}
	}

	public static void calcRankings(StockData stockData) {
		int yr = 0;
		if (stockData.getNormYield() != null){
			double y = stockData.getNormYield().doubleValue();
			if (y > 0.0d){
				yr = 1;
			} 
			if (y >= 0.5d){
				yr = 2;
			} 
			if (y >= 1.0d){
				yr = 3;
			} 
			if (y >= 1.5d){
				yr = 4;
			} 
			if (y >= 2.0d){
				yr = 5;
			}
			if (y >= 2.5d){
				yr = 6;
			} 
			if (y >= 3.25d){
				yr = 7;
			} 
			if (y >= 4.0d){
				yr = 8;
			} 
			if (y >= 5.5d){
				yr = 9;
			} 
			if (y >= 7.0d){
				yr = 10;
			}
			if (y >= 9.0d){
				yr = 8;
			} 
			if (y >= 12.0d){
				yr = 6;
			} 
			if (y >= 15.0d){
				yr = 4;
			} 
			if (y >= 25.0d){
				yr = 2;
			} 
			if (y >= 50.0d){
				yr = 0;
			} 
		}
		
		int sr = 0;
		if (stockData.getStreak() >= 1){
			sr = 3;
		} 
		if (stockData.getStreak() >= 2 && stockData.getSkipped() == 0){
			sr = 4;
		} 		
		if ((stockData.getStreak() >= 3 && stockData.getSkipped() == 0) || (stockData.getStreak() >= 6 && stockData.getSkipped() == 1)){
			sr = 5;
		} 		
		if ((stockData.getStreak() >= 4 && stockData.getSkipped() == 0) || (stockData.getStreak() >= 8 && stockData.getSkipped() == 1)){
			sr = 6;
		} 		
		if ((stockData.getStreak() >= 6 && stockData.getSkipped() == 0) || (stockData.getStreak() >= 12 && stockData.getSkipped() == 1)){
			sr = 7;
		} 
		if ((stockData.getStreak() >= 8 && stockData.getSkipped() == 0) || (stockData.getStreak() >= 16 && stockData.getSkipped() == 1)){
			sr = 8;
		} 
		if ((stockData.getStreak() >= 10 && stockData.getSkipped() == 0) || (stockData.getStreak() >= 20 && stockData.getSkipped() == 1)){
			sr = 9;
		} 		
		if ((stockData.getStreak() >= 12 && stockData.getSkipped() == 0) || (stockData.getStreak() >= 24 && stockData.getSkipped() == 1)){
			sr = 10;
		} 		
		
		int gr = 0;
		if (stockData.getDg5() != null && stockData.getDg10() != null){
			if (stockData.getDg5() >= .5){
				gr = 1;
			} 
			if (stockData.getDg5() >= 1){
				gr = 2;
			} 
			if (stockData.getDg5() >= 2 && stockData.getDg10() >=0){
				gr = 3;
			} 
			if (stockData.getDg5() >= 3 && stockData.getDg10() >=0){
				gr = 4;
			} 
			if (stockData.getDg5() >= 4 && stockData.getDg10() >=0){
				gr = 5;
			} 
			if (stockData.getDg5() >= 6 && stockData.getDg10() >=0){
				gr = 6;
			} 
			if ((stockData.getDg5() >= 8 && stockData.getDg10() >=3) || stockData.getDg5() >= 10){
				gr = 7;
			} 
			if ((stockData.getDg5() >= 10 && stockData.getDg10() >=4) || stockData.getDg5() >= 13){
				gr = 8;
			} 
			if ((stockData.getDg5() >= 12 && stockData.getDg10() >=5) || stockData.getDg5() >= 15){
				gr = 9;
			} 
			if ((stockData.getDg5() >= 15 && stockData.getDg10() >=6) || stockData.getDg5() >= 20){
				gr = 10;
			} 
		}
		
		int fr = 0;
		if (stockData.getEps4() != null && stockData.getEps8() != null){
			if (stockData.getEps4() > 0){
				fr = 1;
			} 
			if (stockData.getEps4() >= 1.5){
				fr = 2;
			} 
			if (stockData.getEps4() >= 3){
				fr = 3;
			} 
			if (stockData.getEps4() >= 4 && stockData.getEps8() >=1){
				fr = 4;
			} 
			if (stockData.getEps4() >= 5 && stockData.getEps8() >=2){
				fr = 5;
			} 
			if (stockData.getEps4() >= 6 && stockData.getEps8() >=3){
				fr = 6;
			} 
			if (stockData.getEps4() >= 10 && stockData.getEps8() >=7){
				fr = 7;
			} 
			if (stockData.getEps4() >= 15 && stockData.getEps8() >=10){
				fr = 8;
			} 
			if (stockData.getEps4() >= 20 && stockData.getEps8() >=15){
				fr = 9;
			} 
			if (stockData.getEps4() >= 25 && stockData.getEps8() >=20){
				fr = 10;
			} 
		}
		
		BigDecimal yrHighDiff = null;
		if (stockData.getStock().getYearHigh() != null && stockData.getStock().getYearLow() != null){
			BigDecimal scale = stockData.getStock().getYearHigh().subtract(stockData.getStock().getYearLow());
			if (scale.compareTo(new BigDecimal(0)) != 0){
				BigDecimal rank = stockData.getStock().getPrice().subtract(stockData.getStock().getYearLow());
				yrHighDiff = rank.divide(scale, 4, RoundingMode.HALF_UP);
				yrHighDiff = new BigDecimal(10).subtract(yrHighDiff.multiply(new BigDecimal(10)));
			}
		}
		
		int vr = 5;
		stockData.setYrHighDiff(yrHighDiff);
		if (yrHighDiff != null){
			vr =yrHighDiff.setScale(0, RoundingMode.HALF_UP).intValue();
		}

		double overall = (yr + sr + gr + fr + vr);
		double nbrDiv = 5.0;
		if (yr == 1 || yr == 10){
			overall += (.5* yr);
			nbrDiv += 0.5;
		}
		if (sr == 1 || sr == 10){
			overall += (.5* sr);
			nbrDiv += 0.5;
		}
		if (gr == 1 || gr == 10){
			overall += (.5* gr);
			nbrDiv += 0.5;
		}
		
		if (fr == 1 || fr == 10){
			overall += (.5* fr);
			nbrDiv += 0.5;
		}
		
		if (yr == 2 || yr == 3|| yr == 8|| yr == 9){
			overall += (.25* yr);
			nbrDiv += 0.25;
		}
		
		if (sr == 2 || sr == 3|| sr == 8|| sr == 9){
			overall += (.25* sr);
			nbrDiv += 0.25;
		}
		
		if (gr == 2 || gr == 3|| gr == 8|| gr == 9){
			overall += (.25* gr);
			nbrDiv += 0.25;
		}
		
		if (fr == 2 || fr == 3|| fr == 8|| fr == 9){
			overall += (.25* fr);
			nbrDiv += 0.25;
		}
		
		double rank = overall / nbrDiv;
		stockData.setYieldRank(yr);
		stockData.setStalwartRank(sr);
		stockData.setGrowthRank(gr);
		stockData.setFinRank(fr);
		stockData.setValueRank(vr);
		stockData.setOverAllRank(rank);
		stockData.setRanksCalculated(true);
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
		if (stockData.getFinData() == null){
			return;
		}
		
		Calendar c = Calendar.getInstance();
		int currYYYY = c.get(Calendar.YEAR) + 1;
		
		Double revNow = null;

		int nbrTimes = 0;
		int year4 = 0;
		int year8 = 0;
		while (revNow == null && nbrTimes < 3){
			nbrTimes ++;
			currYYYY = currYYYY - 1;
			year4 = currYYYY - 4;
			year8 = currYYYY - 8;

			for (String s : stockData.getFinData().keySet()){
				BigDecimal rev = stockData.getFinData().get(s).getRevenue();
				if (s.contains(String.valueOf(currYYYY)) && rev != null){
					revNow = rev.doubleValue();
				}
			}
		}
		
		Double rev4yr = null;
		Double rev8yr = null;

		for (String s : stockData.getFinData().keySet()){
			BigDecimal rev = stockData.getFinData().get(s).getRevenue();
			
			if (s.contains(String.valueOf(year4)) && rev != null){
				rev4yr = rev.doubleValue();
			}
			
			if (s.contains(String.valueOf(year8)) && rev != null){
				rev8yr = rev.doubleValue();
			}
		}

		if (revNow != null){
			if (rev4yr != null && rev4yr > 0){
				double d = revNow / rev4yr;
				double pow = Math.pow(d, (1.0 / 4.0));
				double e4y = (pow - 1) * 100;
				stockData.setEps4(e4y);
			}
			if (rev8yr != null && rev8yr > 0){
				double d = revNow / rev8yr;
				double pow = Math.pow(d, (1.0 / 8.0));
				double e8y = (pow - 1) * 100;
				stockData.setEps8(e8y);
			} 
		}
		
	}


}
