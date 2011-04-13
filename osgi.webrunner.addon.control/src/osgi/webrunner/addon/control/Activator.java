package osgi.webrunner.addon.control;


import java.util.*;

import javax.servlet.*;

import org.osgi.framework.*;


public class Activator implements BundleActivator
{
  // VARIABLES

  private ServiceRegistration serviceReg;

  // METHODS

  public void start( BundleContext context ) throws Exception
  {
    ControlComponent webComp = new ControlComponent( context );

    Properties props = new Properties();
    // the webroot alias of the webcomponent itself, so we can query on
    // it & access it directly...
    props.put( "alias", "/".concat( webComp.getName() ) );
    props.put( "name", webComp.getName() );

    this.serviceReg = context.registerService( Servlet.class.getName(), webComp, props );
  }

  public void stop( BundleContext context ) throws Exception
  {
    if ( this.serviceReg != null )
      this.serviceReg.unregister();
  }
}

/* EOF */
