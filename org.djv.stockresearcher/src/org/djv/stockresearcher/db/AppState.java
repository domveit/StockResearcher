package org.djv.stockresearcher.db;

import java.util.ArrayList;
import java.util.List;

import org.djv.stockresearcher.model.StockData;

public class AppState {
	
	private static AppState instance;
	
	public static AppState getInstance(){
		if (instance == null){
			instance = new AppState();
		}
		return instance;
	}

	private StockData selectedStock;
	
	public List<AppStateListener> listeners = new ArrayList<AppStateListener>();

	public StockData getSelectedStock() {
		return selectedStock;
	}

	public void setSelectedStock(StockData selectedStock) {
		this.selectedStock = selectedStock;
		notifyAllChanged();
	}

	private void notifyAllChanged(){
		for (AppStateListener l: listeners){
			l.notifyChanged(this);
		}
	}
	
	public void addListener(AppStateListener l){
		listeners.add(l);
	}
	
	public void removeListener(AppStateListener l){
		listeners.remove(l);
	}
	
}
