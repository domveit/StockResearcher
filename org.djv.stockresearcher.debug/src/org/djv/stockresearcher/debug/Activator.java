package org.djv.stockresearcher.debug;

import org.eclipse.ui.internal.misc.Policy;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		
		Policy.DEBUG_SWT_GRAPHICS = true;
		Policy.DEBUG_SWT_DEBUG = true;
		System.err.println("set policy to true");
		
		
//	    DeviceData data = new DeviceData();
//	    data.tracking = true;
//	    Display display = new Display(data);
//	    Display defaultDisp = Display.getDefault();
//	    
//	    System.err.println("created display with tracking");
//	    if (display == defaultDisp){
//	    	System.err.println("disp = default");
//	    } else {
//	    	System.err.println("disp != default");
//	    }
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
