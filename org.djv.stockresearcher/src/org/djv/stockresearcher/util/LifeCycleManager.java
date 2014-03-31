package org.djv.stockresearcher.util;

import org.djv.stockresearcher.widgets.StockIndustryDialog;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

public class LifeCycleManager {

	  @PostContextCreate
	  void postContextCreate(IApplicationContext appContext, Display display) {
		  try{
	    final Shell shell = new Shell(SWT.TOOL | SWT.NO_TRIM);
	    StockIndustryDialog dialog = new StockIndustryDialog(shell);

	    // close the static splash screen
	    appContext.applicationRunning();

	    // position the shell
	    setLocation(display, shell);

	    if (dialog.open() != Window.OK) {
	      // close the application
	      System.exit(-1);
	    }
		  } catch (Exception e){
			  e.printStackTrace();
		  }
	  }

	  private void setLocation(Display display, Shell shell) {
	    Monitor monitor = display.getPrimaryMonitor();
	    Rectangle monitorRect = monitor.getBounds();
	    Rectangle shellRect = shell.getBounds();
	    int x = monitorRect.x + (monitorRect.width - shellRect.width) / 2;
	    int y = monitorRect.y + (monitorRect.height - shellRect.height) / 2;
	    shell.setLocation(x, y);
	  }
}
