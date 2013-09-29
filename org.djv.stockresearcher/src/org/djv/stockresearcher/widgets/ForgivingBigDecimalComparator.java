package org.djv.stockresearcher.widgets;

import java.math.BigDecimal;
import java.util.Comparator;

import org.eclipse.swt.SWT;

class ForgivingBigDecimalComparator implements Comparator<String> {
		int sortDir;
		
		public ForgivingBigDecimalComparator(int sortDir) {
			super();
			this.sortDir = sortDir;
		}

		@Override
		public int compare(String s1, String s2)  {
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
			try {
				return new BigDecimal(s1.replace("%", " ").trim());
			} catch (Exception e){
//				System.err.println("could not convert " + s1 );
				return BigDecimal.ZERO;
			}
		}
 }