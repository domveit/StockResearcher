
import java.text.DecimalFormat;

import org.junit.Test;

public class Tester {
	
	@Test
	public void test1() throws Exception {
		double start = 200000;
		
		double rate = 1.085;
		double badrate = 1.00;
		double lev = .60;
		
		double curr = start;
		double borrowMore = curr * (lev);
		double levAmt = borrowMore;
		double currWithLev = curr + borrowMore;
		double levnav = curr;
		
		for (int i = 0 ; i <= 15; i ++){
			double userate = rate;
			if (i == 7){
				userate = badrate;
			}
			
			System.err.println(i + " " 
					+ new DecimalFormat("0.00").format(curr) + " " 
					+ new DecimalFormat("0.00").format(levAmt) + " " 
					+ new DecimalFormat("0.00").format(currWithLev) + " " 
					+ new DecimalFormat("0.00").format(levnav) + " " 
					+ new DecimalFormat("0.00").format(borrowMore) + " " 
					);
			currWithLev = currWithLev * userate;
			curr = curr * userate;
			levnav = currWithLev - levAmt;
			borrowMore = levnav * (lev) - levAmt;
			levAmt += borrowMore;
			currWithLev+= borrowMore;
		}
	}

}
