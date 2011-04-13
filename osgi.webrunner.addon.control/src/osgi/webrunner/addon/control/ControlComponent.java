package osgi.webrunner.addon.control;


import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.osgi.framework.*;


public class ControlComponent extends HttpServlet
{
  // CONSTANTS

  private static final long serialVersionUID = 1L;

  // VARIABLES

  private BundleContext context;

  /**
   * Creates a new ControlComponent instance.
   * 
   * @param context
   *          the OSGi bundle context to use.
   */
  public ControlComponent( BundleContext context )
  {
    this.context = context;
  }

  // METHODS

  public String getName()
  {
    return "control";
  }

  @Override
  protected void doGet( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
  {
    try
    {
      handleActions( req );
    }
    catch ( BundleException e )
    {
      e.printStackTrace();
    }

    renderControlForm( req, resp );
  }

  private void handleActions( HttpServletRequest req ) throws BundleException
  {
    String actionID = req.getParameter( "actionGUID" );

    HttpSession session = req.getSession();
    String actionUUID = ( String )session.getAttribute( "control-guid" );
    if ( actionUUID != null && actionUUID.equals( actionID ) )
    {
      // Action expected...
      session.setAttribute( "control-guid", null );

      long bundleID = Long.parseLong( req.getParameter( "bundle-id" ) );

      Bundle bundle = context.getBundle( bundleID );
      if ( bundle != null )
      {
        String action = req.getParameter( "action" );

        if ( "Stop".equals( action ) )
        {
          bundle.stop();
        }
        else if ( "Start".equals( action ) )
        {
          bundle.start();
        }
      }
    }
  }

  private void renderControlForm( HttpServletRequest req, HttpServletResponse resp ) throws IOException
  {
    HttpSession session = req.getSession();

    String uuid = UUID.randomUUID().toString();
    session.setAttribute( "control-guid", uuid );

    PrintWriter writer = resp.getWriter();

    for ( Bundle bundle : context.getBundles() )
    {
      if ( !isStarted( bundle ) && !isStopped( bundle ) )
        continue;
      
      String name = bundle.getSymbolicName();
      if ( !name.startsWith( "osgi.webrunner." ) || name.equals( "osgi.webrunner.addon.control" ) )
        continue;

      String id = "ctrl-".concat( name );
      String nextState = isStarted( bundle ) ? "Stop" : "Start";

      writer.printf( "<form id='%s' method='get' action=''>", id );
      writer.printf( "<label for='%s'>%s %s</label> ", "action", nextState, name );
      writer.printf( "<input type='submit' name='%s' value='%s' />", "action", nextState );
      writer.printf( "<input type='hidden' name='%s' value='%d' />", "bundle-id", bundle.getBundleId() );
      writer.printf( "<input type='hidden' name='%s' value='%s' /><br/>", "actionGUID", uuid );
      writer.print( "</form>" );
    }
  }

  private boolean isStarted( Bundle bundle )
  {
    return ( bundle.getState() & Bundle.ACTIVE ) != 0;
  }

  private boolean isStopped( Bundle bundle )
  {
    return ( bundle.getState() & Bundle.RESOLVED ) != 0;
  }
}

/* EOF */
