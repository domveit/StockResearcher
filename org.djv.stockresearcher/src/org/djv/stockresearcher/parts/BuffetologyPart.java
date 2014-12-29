 
package org.djv.stockresearcher.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.djv.stockresearcher.db.AppState;
import org.djv.stockresearcher.db.AppStateListener;
import org.djv.stockresearcher.db.BuffetologyService;
import org.djv.stockresearcher.model.BuffetAnalysis;
import org.djv.stockresearcher.model.BuffetDetail;
import org.djv.stockresearcher.model.StockData;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class BuffetologyPart implements AppStateListener {
	
	@Inject
	public BuffetologyPart() {
	}
	
	private Label totalScore;
	private String[] titles = {"Stat", "Value", "Score"};
	private Table table;
	
	private Composite parent;
	
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		this.parent = parent;
		
		totalScore = new Label(parent, SWT.NONE);
		totalScore.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		
		FontData[] fontData = totalScore.getFont().getFontData();
		for(int i = 0; i < fontData.length; ++i)
		    fontData[i].setHeight(20);

		final Font newFont = new Font(Display.getDefault(), fontData);
		totalScore.setFont(newFont);

		// Since you created the font, you must dispose it
		totalScore.addDisposeListener(new DisposeListener() {
		    public void widgetDisposed(DisposeEvent e) {
		        newFont.dispose();
		    }
		});
		
		table = new Table(parent, SWT.BORDER |SWT.V_SCROLL);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		GridData data2 = new GridData(SWT.FILL, SWT.FILL, false, true);
		table.setLayoutData(data2);
		
		for (int i=0; i < titles.length; i++) {
			TableColumn column = new TableColumn (table, SWT.NONE);
			column.setText (titles [i]);
		}
		AppState.getInstance().addListener(this);
		Display.getDefault().asyncExec(new Runnable(){
			@Override
			public void run() {
				createTable();
			}
		});
	}
	
	private void createTable() {
		final StockData sd = AppState.getInstance().getSelectedStock();
		if (sd == null){
			return;
		}
		
		Thread t = new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					final BuffetAnalysis ba = new BuffetologyService().buffetize(sd);
					
					Display.getDefault().asyncExec(new Runnable(){
						@Override
						public void run() {
							buffetize(ba);
							for (int i = 0; i < titles.length; i++) {
								table.getColumn(i).pack();
							}
							parent.layout(true, true);
						}
					});
				} catch (Exception e){
					e.printStackTrace();
				}
			}
			
		});
		t.start();
	}
	
	private void buffetize(BuffetAnalysis ba) {
		table.removeAll();
		
		for (BuffetDetail detail: ba.getDetails()){
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(0, detail.getDescription());
			if (detail.getValue() == null){
				item.setText(1, "");
			} else {
				item.setText(1, String.valueOf(detail.getValue()));
			}
			item.setText(2, String.valueOf(detail.getScore()));
		}
		
		totalScore.setText(String.valueOf(ba.getTotalScore()));
	}
	
	@Focus
	public void onFocus() {
	}

	@Override
	public void notifyChanged(AppState appState) {
		createTable();
	}
}