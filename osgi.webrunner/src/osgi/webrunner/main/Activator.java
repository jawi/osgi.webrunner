package osgi.webrunner.main;

import java.util.*;

import javax.servlet.*;

import org.osgi.framework.*;

public class Activator implements BundleActivator {
    private ServiceRegistration serviceReg;

    @Override
    public void start(BundleContext context) throws Exception {
	// Register the main servlet / container, which does something with the
	// individual web components...
	this.serviceReg = registerMainServlet(context);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
	if (this.serviceReg != null) {
	    this.serviceReg.unregister();
	}
    }

    private ServiceRegistration registerMainServlet(BundleContext context) {
	Properties props = new Properties();
	// the webroot where this servlet will be listening...
	props.put("alias", "/");

	return context.registerService(Servlet.class.getName(),
		new MainServlet(context), props);
    }
}
