package osgi.webrunner.addon1;

import java.util.*;

import javax.servlet.*;

import org.osgi.framework.*;

public class Activator implements BundleActivator {

    private ServiceRegistration serviceReg;

    public void start(BundleContext context) throws Exception {
	MyWebComponent1 webComp = new MyWebComponent1();

	Properties props = new Properties();
	// the webroot alias of the webcomponent itself, so we can query on
	// it & access it directly...
	props.put("alias", "/".concat(webComp.getName()));
	props.put("name", webComp.getName());

	this.serviceReg = context.registerService(Servlet.class.getName(),
		webComp, props);
    }

    public void stop(BundleContext context) throws Exception {
	if (this.serviceReg != null)
	    this.serviceReg.unregister();
    }

}
