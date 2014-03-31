package org.djv.stockresearcher.widgets.support;

public enum StockTableColumn {
	
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
	
//  NAME  		("description",	"source", 				DecimalFormat, 	null disp, 	percent?, 	"color source", Comparator.class),
	WATCHED			("", 			"watched", 				null, 			"???", 		false,		null, 			StringComparator.class),
	STOCK			("Stock", 		"stock.symbol", 		null, 			"???", 		false, 		null, 			StringComparator.class),
	NAME			("Name", 		"stockIndustry.name", 	null, 			"???", 		false, 		null, 			StringComparator.class),
	MARKET_CAP		("MktCap", 		"stock.marketCap", 		null, 			"N/A", 		false, 		null, 			MarketCapComparator.class),
	PRICE			("Price", 		"stock.price", 			null, 			"N/A", 		false, 		null,			ForgivingBigDecimalComparator.class),
	EXCHANGE		("Exchange", 	"stock.exchange", 		null, 			"???", 		false, 		null, 			StringComparator.class),
	INDUSTRY		("Industry", 	"sectorIndustry.industryName",null, 	"???", 		false, 		null, 			StringComparator.class),
	SECTOR			("Sector", 		"sectorIndustry.sectorName",null, 		"???", 		false, 		null,	  		StringComparator.class),
	PE				("PE", 			"stock.pe",				"0.00", 		"N/A", 		false, 		null,			ForgivingBigDecimalComparator.class),
	PEG				("PEG", 		"stock.peg",			"0.00", 		"N/A", 		false, 		null,			ForgivingBigDecimalComparator.class),
	
	YIELD			("Yield", 		"stock.yield", 			"0.00",			"N/A", 		true, 		"yieldRank",	ForgivingBigDecimalComparator.class),
	NORM_YIELD		("NYield", 		"normYield",	 		"0.00", 		"N/A", 		true, 		"yieldRank", 	ForgivingBigDecimalComparator.class),
	DIVIDEND		("Div", 		"stock.dividend",		"0.0000", 		"N/A", 		false, 		"yieldRank", 	ForgivingBigDecimalComparator.class),
	NORM_DIVIDEND	("NDiv", 		"normDividend",			"0.0000", 		"N/A", 		false, 		"yieldRank",	ForgivingBigDecimalComparator.class),
	YIELD_RANK		("YRank", 		"yieldRank",			null, 			"N/A", 		false, 		"yieldRank",	ForgivingBigDecimalComparator.class),
	
	YRHIGH			("Yr High",		"stock.yearHigh",	 	"0.00", 		"N/A", 		false, 		"valueRank", 	ForgivingBigDecimalComparator.class),
	YRLOW			("Yr Low", 		"stock.yearLow",		"0.00", 		"N/A", 		false, 		"valueRank", 	ForgivingBigDecimalComparator.class),
	YR_TARGET_PRICE ("Yr Tgt Price","stock.oneYrTargetPrice","0.00", 		"N/A", 		false, 		"valueRank", 	ForgivingBigDecimalComparator.class),
	YR_UPSIDE		("Yr Upside", 	"oytUpside",			"0.00", 		"N/A", 		true, 		"valueRank", 	ForgivingBigDecimalComparator.class),
	YRRANK			("Yr Rank", 	"yrHighDiff",			"0.00", 		"N/A", 		true, 		"valueRank", 	ForgivingBigDecimalComparator.class),
	VALUE_RANK		("VRank", 		"valueRank",			null, 			"N/A", 		false, 		"valueRank",	ForgivingBigDecimalComparator.class),
	
	STREAK			("Streak",		"streak",			 	null, 			"N/A", 		false, 		"stalwartRank", ForgivingBigDecimalComparator.class),
	SKIPPED			("Skipped", 	"skipped",				null, 			"N/A", 		false, 		"stalwartRank", ForgivingBigDecimalComparator.class),
	STALWART_RANK	("SRank", 		"stalwartRank",			null, 			"N/A", 		false, 		"stalwartRank",	ForgivingBigDecimalComparator.class),
	
	DG5 			("dg5",			"dg5",					"0.00", 		"N/A", 		true, 		"growthRank", 	ForgivingBigDecimalComparator.class),
	DG10			("dg10", 		"dg10",					"0.00", 		"N/A", 		true, 		"growthRank", 	ForgivingBigDecimalComparator.class),
	GROWTH_RANK		("GRank", 		"growthRank",			null, 			"N/A", 		false, 		"growthRank",	ForgivingBigDecimalComparator.class),
	
	RG5				("rg5",		 	"rg5",					"0.00", 		"N/A", 		true, 		"finRank", 		ForgivingBigDecimalComparator.class),
	RG10			("rg10", 		"rg10",					"0.00", 		"N/A", 		true, 		"finRank",		ForgivingBigDecimalComparator.class),
	FIN_RANK		("FRank", 		"finRank",				null, 			"N/A", 		false, 		"finRank",		ForgivingBigDecimalComparator.class),
	
	CHOWDER			("Chowder", 	"chowder",				"0.00", 		"N/A", 		true, 		null,			ForgivingBigDecimalComparator.class),

	OVERALL_RANK	("RANK", 		"overAllRank",			"0.00", 		"N/A", 		false, 		"overAllRank",		ForgivingBigDecimalComparator.class),

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
