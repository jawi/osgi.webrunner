package osgi.webrunner.addon2;

import java.util.*;

import javax.servlet.*;

import org.osgi.framework.*;

public class Activator implements BundleActivator {

    private ServiceRegistration serviceReg;

    public void start(BundleContext context) throws Exception {
	MyWebComponent2 webComp = new MyWebComponent2();

	Properties props = new Properties();
	// the name of the webcomponent itself, so we can query on it...
	props.put("alias", "/".concat(webComp.getName()));
	props.put("name", webComp.getName());

	// register our webcomponent under two service (class)names, so we can
	// make distinction between our own webcomponents and "normal"
	// servlets...
	this.serviceReg = context.registerService(Servlet.class.getName(),
		webComp, props);
    }

    public void stop(BundleContext context) throws Exception {
	if (this.serviceReg != null)
	    this.serviceReg.unregister();
    }

}
