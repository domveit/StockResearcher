import java.io.BufferedReader;

import org.djv.stockresearcher.db.YahooFinanceUtil;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class JSONTest {
	
	@Test
	public void test1() throws Exception {
		String YQLquery = "select * from yahoo.finance.quotes where symbol in (\"YHOO\",\"AAPL\",\"GOOG\",\"MSFT\")";
		BufferedReader br = YahooFinanceUtil.getYQLJson(YQLquery);
		 JsonParser parser = new JsonParser();
		 JsonObject json = parser.parse(br).getAsJsonObject();
		 JsonObject query = json.get("query").getAsJsonObject();
		 JsonObject results = query.get("results").getAsJsonObject();
		 JsonArray quote = results.get("quote").getAsJsonArray();
		 for (JsonElement ce: quote){
			 JsonObject c = ce.getAsJsonObject();
			 String symbol = c.get("symbol").getAsString();
			 String ask = c.get("Ask").getAsString();
			 System.err.println(symbol + "  " + ask);
		 }
		 br.close();
	}
}
