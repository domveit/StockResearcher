package org.djv.stockresearcher.widgets.support;

public enum StockTableColumn {
	
	
//	String[] titles = {"Stock", "Name", "MCap", "Price", "Yr Range", "Div", "Yield", "PE", "PEG", "Strk", "Skip", "dg5", "dg10", "rg4", "rg8", "Rank", "Exchange", "Industry", "Sector", "Value score", "chowder", "OYT Price", "OYT upside"};

//String yrRange = ((sd.getStock().getYearLow() == null) ? "???" :  new DecimalFormat("0.00").format(sd.getStock().getYearLow()))
//		 + "-" + 
//		 ((sd.getStock().getYearHigh() == null) ? "???" :  new DecimalFormat("0.00").format(sd.getStock().getYearHigh()));
//item.setText (4, yrRange);
//

//item.setText (7, (sd.getStock().getPe() == null) ? "N/A" :  new DecimalFormat("0.00").format(sd.getStock().getPe()));
//item.setText (8, (sd.getStock().getPeg() == null) ? "N/A" : new DecimalFormat("0.00").format(sd.getStock().getPeg()));
//item.setText (9, String.valueOf(sd.getStreak()));
//item.setText (10,  String.valueOf(sd.getSkipped()));
//item.setText (11, (sd.getDg5() == null) ? "N/A" : new DecimalFormat("0.00").format(sd.getDg5()) + "%");
//item.setText (12, (sd.getDg10() == null) ? "N/A" : new DecimalFormat("0.00").format(sd.getDg10()) + "%");
//
//item.setText (13, (sd.getEps4() == null) ? "N/A" : new DecimalFormat("0.00").format(sd.getEps4()) + "%");
//item.setText (14, (sd.getEps8() == null) ? "N/A" : new DecimalFormat("0.00").format(sd.getEps8()) + "%");
//
//item.setText (15, new DecimalFormat("0.00").format(sd.getOverAllRank()));
//
//
//item.setText (19, (sd.getYrHighDiff() == null) ? "" : new DecimalFormat("0.00").format(sd.getYrHighDiff()) + "%");
//
//BigDecimal chowder = null;
//if (sd.getNormYield() != null){
//	if (sd.getDg5() != null){
//		chowder = sd.getNormYield().add(new BigDecimal(sd.getDg5()));
//	} 
//} else if (sd.getStock().getYield()!= null){
//	if (sd.getDg5() != null){
//		chowder = sd.getStock().getYield().add(new BigDecimal(sd.getDg5()));
//	} 
//} 
//item.setText (20, (chowder == null) ? "" : new DecimalFormat("0.00").format(chowder) + "%");
//item.setText (21, (sd.getStock().getOneYrTargetPrice() == null) ? "" : new DecimalFormat("0.00").format(sd.getStock().getOneYrTargetPrice()));
//item.setText (22, (sd.getOytUpside() == null) ? "" : new DecimalFormat("0.00").format(sd.getOytUpside()) + "%");
	
	//if (sd.getNormDividend() != null){
//	item.setText (5,  String.valueOf(sd.getNormDividend()));
//} else {
//	item.setText (5, (sd.getStock().getDividend() == null) ? "N/A" :  new DecimalFormat("0.00").format(sd.getStock().getDividend()));
//}
//
//if (sd.getNormYield() != null){
//	item.setText (6, new DecimalFormat("0.00").format(sd.getNormYield()));
//} else {
//	item.setText (6, (sd.getStock().getYield() == null) ? "N/A" :  new DecimalFormat("0.00").format(sd.getStock().getYield()));
//}
//
	
//  NAME  		("description",	"source", 				DecimalFormat, 	null disp, 	percent?, 	"color source", Comparator.class),
	WATCHED			("", 			"watched", 				null, 			"???", 		false,		null, 			StringComparator.class),
	STOCK			("Stock", 		"stock.symbol", 		null, 			"???", 		false, 		null, 			StringComparator.class),
	NAME			("Name", 		"stockIndustry.name", 	null, 			"???", 		false, 		null, 			StringComparator.class),
	MARKET_CAP		("MktCap", 		"stock.marketCap", 		null, 			"N/A", 		false, 		null, 			MarketCapComparator.class),
	PRICE			("Price", 		"stock.price", 			null, 			"N/A", 		false, 		null,			ForgivingBigDecimalComparator.class),
	EXCHANGE		("Exchange", 	"stock.exchange", 		null, 			"???", 		false, 		null, 			StringComparator.class),
	INDUSTRY		("Industry", 	"sectorIndustry.industryName",null, 	"???", 		false, 		null, 			StringComparator.class),
	SECTOR			("Sector", 		"sectorIndustry.sectorName",null, 		"???", 		false, 		null,	  		StringComparator.class),
	YIELD			("Yield", 		"stock.yield", 			"0.00",			"N/A", 		true, 		"yieldRank",	ForgivingBigDecimalComparator.class),
	NORM_YIELD		("NYield", 		"normYield",	 		"0.00", 		"N/A", 		true, 		"yieldRank", 	ForgivingBigDecimalComparator.class),
	DIVIDEND		("Div", 		"stock.dividend",		"0.0000", 		"N/A", 		false, 		"yieldRank", 	ForgivingBigDecimalComparator.class),
	NORM_DIVIDEND	("NDiv", 		"normDividend",			"0.0000", 		"N/A", 		false, 		"yieldRank",	ForgivingBigDecimalComparator.class),
	YIELD_RANK		("YRank", 		"yieldRank",			null, 			"N/A", 		false, 		"yieldRank",	ForgivingBigDecimalComparator.class),
	;
	
	private final String description;
	private String source;
	private String decimalFormat;
	private String nullDisplay;
	private boolean isPercentile;
	private String colorSource;
	private Class<? extends SortDirComparator> comparatorClass;
	
	private StockTableColumn(String description, String source,
			String decimalFormat, String nullDisplay, boolean isPercentile,
			String colorSource, Class<? extends SortDirComparator> comparatorClass) {
		this.description = description;
		this.source = source;
		this.decimalFormat = decimalFormat;
		this.nullDisplay = nullDisplay;
		this.isPercentile = isPercentile;
		this.colorSource = colorSource;
		this.comparatorClass = comparatorClass;
	}

	public String getDescription() {
		return description;
	}

	public Class<? extends SortDirComparator> getComparatorClass() {
		return comparatorClass;
	}

	public void setComparatorClass(Class<? extends SortDirComparator> comparatorClass) {
		this.comparatorClass = comparatorClass;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDecimalFormat() {
		return decimalFormat;
	}

	public void setDecimalFormat(String decimalFormat) {
		this.decimalFormat = decimalFormat;
	}

	public String getNullDisplay() {
		return nullDisplay;
	}

	public void setNullDisplay(String nullDisplay) {
		this.nullDisplay = nullDisplay;
	}

	public boolean isPercentile() {
		return isPercentile;
	}

	public void setPercentile(boolean isPercentile) {
		this.isPercentile = isPercentile;
	}

	public String getColorSource() {
		return colorSource;
	}

	public void setColorSource(String colorSource) {
		this.colorSource = colorSource;
	}

}
