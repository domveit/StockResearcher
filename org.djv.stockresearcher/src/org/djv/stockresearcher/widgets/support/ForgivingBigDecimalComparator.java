package org.djv.stockresearcher.widgets.support;

import java.math.BigDecimal;

import org.eclipse.swt.SWT;

class ForgivingBigDecimalComparator extends SortDirComparator {
		
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
				return BigDecimal.ZERO;
			}
		}
 }