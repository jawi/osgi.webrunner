package osgi.webrunner.addon2;


import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;


public class MyWebComponent2 extends HttpServlet
{
  // CONSTANTS

  private static final long serialVersionUID = 1L;

  // METHODS
  
  public String getName()
  {
    return "comp2";
  }

  @Override
  protected void doGet( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
  {

    HttpSession session = req.getSession();

    int counter = 0;
    String counterVal = ( String )session.getAttribute( "counter2" );
    if ( counterVal != null )
    {
      counter = Integer.parseInt( counterVal );
    }

    String param = req.getParameter( "a" );
    if ( param != null && "dec".equals( param ) )
    {
      counter--;

      session.setAttribute( "counter2", Integer.toString( counter ) );
    }

    PrintWriter writer = resp.getWriter();
    writer
        .printf(
            "<p id='y'>%s speaking here! Click <a rel='ajax' href='?c=%s&a=dec'>here</a> to decrease this counter: %d ...</p>",
            getName(), getName(), counter );
  }

}
