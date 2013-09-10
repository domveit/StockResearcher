package org.djv.stockresearcher.parts;

import java.math.BigDecimal;
import java.util.Comparator;

import org.eclipse.swt.SWT;

class MarketCapComparator implements Comparator<String> {
		int sortDir;
		
		public MarketCapComparator(int sortDir) {
			super();
			this.sortDir = sortDir;
		}

		@Override
		public int compare(String s1, String s2) {
			if (sortDir == SWT.UP){
				return convert(s2).compareTo(convert(s1));
			} else {
				return convert(s1).compareTo(convert(s2));
			}
		}

		private BigDecimal convert(String s1) {
			if (s1.equals("N/A") || s1.trim().equals("")){
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