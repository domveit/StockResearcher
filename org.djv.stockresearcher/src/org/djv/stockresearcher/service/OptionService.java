package org.djv.stockresearcher.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.djv.stockresearcher.broker.GoogleJSONOptionDataBroker;
import org.djv.stockresearcher.broker.IOptionDataBroker;
import org.djv.stockresearcher.model.OptionTable;

public class OptionService {
	
	private static OptionService instance = new OptionService();
	
	ExecutorService pool1 = null;
	
	private IOptionDataBroker optionBroker = new GoogleJSONOptionDataBroker();
	
	List<OptionListener> optionListeners = new ArrayList<OptionListener>();
	
	public void addListener(OptionListener l){
		optionListeners.add(l);
	}
	
	public void notifyAllOptionListeners(OptionTable ot, int type) {
		for (final OptionListener l : optionListeners){
			l.notifyChanged(ot, type);
		}
	}
	
	public static OptionService getInstance(){
		return instance;
	}
	
	public void getOptionTable(final String symbol) {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					OptionTable ot = optionBroker.getOptionTable(symbol);
					notifyAllOptionListeners(ot, OptionListener.TYPE_NEW);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		pooledExecution(r);
	}
	
	public void getOptionCallsForCurrentPeriod(final OptionTable table){
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					optionBroker.getOptionCallsForCurrentPeriod(table);
					notifyAllOptionListeners(table, OptionListener.TYPE_UPDATE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		pooledExecution(r);
	}

	
	public void pooledExecution(Runnable runnable){
		if (pool1 != null){
			pool1.shutdownNow();
			try {
				pool1.awaitTermination(30, TimeUnit.MINUTES);
				pool1 = null;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		pool1 = Executors.newFixedThreadPool(1);
		pool1.submit(runnable);
		Thread t = new Thread(new Runnable(){
			@Override
			public void run() {
				pool1.shutdown();
				try {
					pool1.awaitTermination(30, TimeUnit.MINUTES);
					pool1 = null;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
	}

}
