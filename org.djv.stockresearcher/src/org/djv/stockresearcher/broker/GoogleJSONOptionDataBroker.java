package org.djv.stockresearcher.broker;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.djv.stockresearcher.db.YahooFinanceUtil;
import org.djv.stockresearcher.model.Option;
import org.djv.stockresearcher.model.OptionPeriod;
import org.djv.stockresearcher.model.OptionTable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GoogleJSONOptionDataBroker implements IOptionDataBroker {

	@Override
	public OptionTable getOptionTable(String symbol) throws Exception {
		OptionTable table = new OptionTable();
		table.setSymbol(symbol);
		String urlString = "http://www.google.com/finance/option_chain?q=" + symbol+ "&output=json";
		System.err.println(urlString);
		BufferedReader br = YahooFinanceUtil.getYahooCSVNice(urlString);
		
		try {
			JsonParser parser = new JsonParser();
			JsonObject json = parser.parse(br).getAsJsonObject();
			JsonElement jsonElement = json.get("expiry");
			if (jsonElement == null){
				return table;
			}
			JsonObject expiry = jsonElement.getAsJsonObject();
			int y = expiry.get("y").getAsInt();
			int m = expiry.get("m").getAsInt();
			int d = expiry.get("d").getAsInt();
			OptionPeriod currPeriod = new OptionPeriod(y, m ,d);
			table.setCurrentPeriod(currPeriod);
			
			JsonArray expirations = json.get("expirations").getAsJsonArray();
			
			for (JsonElement ce: expirations){
				JsonObject exp = ce.getAsJsonObject();
				int yi = exp.get("y").getAsInt();
				int mi = exp.get("m").getAsInt();
				int di = exp.get("d").getAsInt();
				OptionPeriod periodi = new OptionPeriod(yi, mi ,di);
				table.getPeriods().add(periodi);
			}
			parseOptionJSON(table, json);
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			br.close();
		}
		return table;
	}

	public void parseOptionJSON(OptionTable table, JsonObject json )
			throws IOException {
		OptionPeriod currPeriod = table.getCurrentPeriod();
		JsonElement putsEle = json.get("puts");
		if (putsEle != null){
			JsonArray puts = putsEle.getAsJsonArray();
			List<Option> putList = buildOptionList(currPeriod, puts, "P");
			table.getPutMap().put(currPeriod, putList);
		}
		
		JsonElement callsEle = json.get("calls");
		if (callsEle != null){
			JsonArray calls = callsEle.getAsJsonArray();		
			List<Option> callList = buildOptionList(currPeriod, calls, "C");
			table.getCallMap().put(currPeriod, callList);
		}
	}

	public List<Option> buildOptionList(OptionPeriod currPeriod, JsonArray puts, String type) {
		List<Option> optionList = new ArrayList<Option>();
		for (JsonElement ce: puts){
			JsonObject put = ce.getAsJsonObject();
			String s = put.get("s").getAsString();
			BigDecimal p = convertBd(put.get("p").getAsString());
			BigDecimal b = convertBd(put.get("b").getAsString());
			BigDecimal a = convertBd(put.get("a").getAsString());
			BigDecimal strike = convertBd(put.get("strike").getAsString());
			
			Option o = new Option();
			o.setType(type);
			o.setExpiration(currPeriod.getDate());
			o.setStrike(strike);
			o.setSymbol(s);
			o.setAsk(a);
			o.setBid(b);
			o.setLast(p);
			optionList.add(o);
		}
		Collections.sort(optionList, new Comparator<Option>(){
			@Override
			public int compare(Option arg0, Option arg1) {
				int comp = arg0.getExpiration().compareTo(arg1.getExpiration());
				if (comp == 0){
					comp = arg0.getStrike().compareTo(arg1.getStrike());
				}
				return comp;
			}
		});
		return optionList;
	}
	
	@Override
	public void getOptionCallsForCurrentPeriod(OptionTable table) throws Exception {
		OptionPeriod per = table.getCurrentPeriod();
		String urlString = "http://www.google.com/finance/option_chain?q="+table.getSymbol()+"&expd="+per.getD()+"&expm=" +per.getM()+ "&expy="+per.getY()+"&output=json";
		System.err.println(urlString);
		BufferedReader br = YahooFinanceUtil.getYahooCSVNice(urlString);
		
		try {
			JsonParser parser = new JsonParser();
			JsonObject json = parser.parse(br).getAsJsonObject();
			parseOptionJSON(table, json);
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			br.close();
		}
	}
	
	public BigDecimal convertBd(String s) {
		if (s == null){
			return null;
		}
		s = s.replace(",", "");
		try{
			return new BigDecimal(s);
		} catch (Exception e) {
			return null;
		}
	}
	
}
