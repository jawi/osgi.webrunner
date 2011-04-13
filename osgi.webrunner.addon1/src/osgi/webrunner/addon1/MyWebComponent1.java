package osgi.webrunner.addon1;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

public class MyWebComponent1 extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public String getName() {
	return "comp1";
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	    throws ServletException, IOException {

	HttpSession session = req.getSession();

	int counter = 0;
	String counterVal = (String) session.getAttribute("counter1");
	if (counterVal != null) {
	    counter = Integer.parseInt(counterVal);
	}

	String param = req.getParameter("a");
	if (param != null && "inc".equals(param)) {
	    counter++;

	    session.setAttribute("counter1", Integer.toString(counter));
	}

	PrintWriter writer = resp.getWriter();
	writer.printf(
		"<p id='x'>%s speaking here! Click <a rel='ajax' href='?c=%s&a=inc'>here</a> to increase this counter: %d ...</p>",
		getName(), getName(), counter);
    }

}
