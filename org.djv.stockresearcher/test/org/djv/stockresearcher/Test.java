package org.djv.stockresearcher;

import java.text.DecimalFormat;

public class Test {
	
	public static void main(String... args){
		int years = 20;
		double rate = .08;
		double start = 300000;
		double yearlycontrib = 0;
		
		for (int year = 0 ; year < years; year ++){
			start = start * (1 + rate) + yearlycontrib;
			System.err.println(year + " " + new DecimalFormat("0.00").format(start));
		}
	}

}
