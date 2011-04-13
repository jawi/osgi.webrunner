package osgi.webrunner.main;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.osgi.framework.*;

public class MainServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private BundleContext context;

    public MainServlet(BundleContext context) {
	this.context = context;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	    throws ServletException, IOException {

	resp.setContentType("text/html");

	PrintWriter writer = resp.getWriter();

	String delegateCompName = req.getParameter("c");
	// look whether we're asked to process the request for a single
	// component...
	if (delegateCompName != null && delegateCompName.matches("\\w+")) {
	    Servlet component = getWebComponent(delegateCompName);
	    if (component != null) {
		component.service(req, resp);
	    } else {
		writer.printf("<p>Component '%s' not found!</p>",
			delegateCompName);
	    }
	} else {
	    Collection<String> componentNames = getWebComponentNames();

	    writer.print("<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"utf-8\"/><title>OSGi web runner</title><style>body { font-family: sans-serif; font-size: 10pt; } .iframe { border: solid 1px #ddd; margin: 0.7em; padding: 0.3em; width: 40em; height: 10em; float: left; }</style></head><body>");
	    writer.print("<h1>OSGi web runner</h1>");
	    writer.print("<p>This small example shows how to dynamically add/remove servlets.</p>");
	    writer.printf("<p>Found <strong>%d</strong> components!</p>",
		    componentNames.size());

	    for (String componentName : componentNames) {
		writer.printf(
			"<div class='iframe' id='if%s' data-src='?c=%s'></div>",
			componentName, componentName);
	    }

	    writer.println("<script src='https://ajax.googleapis.com/ajax/libs/jquery/1.5.2/jquery.min.js'></script>"
		    + "<script>function r(id){$('a[rel=ajax][href!=\\'#\\']').each(function(){var h=this.href; this.href='#'; this.onclick=function(){ $.ajax({url:h,success:function(data){$('#'+id).replaceWith(data);r(id);}});}});}"
		    + "$(document).ready(function(){$('.iframe').each(function(){$(this).load($(this).data('src'),function(){var id=$(this).children(':first-child').attr('id');if(id){r(id)}})});});</script></body></html>");
	}
    }

    /**
     * Returns a web component by its registered name.
     * 
     * @param name
     *            the name of the component to retrieve.
     * @return a servlet, can be <code>null</code>.
     */
    private Servlet getWebComponent(String name) {

	Servlet result = null;
	ServiceReference[] refs = null;

	try {
	    String filter = String.format("(&(%s=%s)(name=%s))",
		    Constants.OBJECTCLASS, Servlet.class.getName(), name);

	    refs = this.context.getServiceReferences(null, filter);
	} catch (InvalidSyntaxException e) {
	    e.printStackTrace();
	}

	if (refs != null && refs.length == 1) {
	    result = (Servlet) this.context.getService(refs[0]);
	}

	return result;

    }

    /**
     * Returns the names of all registered web components.
     * 
     * @return a collection of web component names, never <code>null</code>.
     */
    private Collection<String> getWebComponentNames() {
	List<String> result = new ArrayList<String>();

	ServiceReference[] refs = null;

	try {
	    // This will find all servlets that are *also* registered with a
	    // 'name' service attribute...
	    String filter = String.format("(&(%s=%s)(name=*))",
		    Constants.OBJECTCLASS, Servlet.class.getName());

	    refs = this.context.getServiceReferences(null, filter);
	    if (refs != null) {
		Arrays.sort(refs, new ServiceReferenceComparator());
	    }
	} catch (InvalidSyntaxException e) {
	    e.printStackTrace();
	}

	if (refs != null) {
	    for (ServiceReference ref : refs) {
		String name = (String) ref.getProperty("name");
		assert name != null : "Internal error: name service attribute not set?!";
		result.add(name);
	    }
	}

	return result;
    }

    // INNER TYPES

    /**
     * Simple comparator implementation that sorts on the service ID of two
     * given service references.
     */
    static class ServiceReferenceComparator implements
	    Comparator<ServiceReference> {
	@Override
	public int compare(ServiceReference o1, ServiceReference o2) {
	    Long id1 = (Long) o1.getProperty(Constants.SERVICE_ID);
	    Long id2 = (Long) o2.getProperty(Constants.SERVICE_ID);
	    return id1.compareTo(id2);
	}
    }
}
