/*!
 * Copyright 2002 - 2015 Webdetails, a Pentaho company. All rights reserved.
 *
 * This software was developed by Webdetails and is provided under the terms
 * of the Mozilla Public License, Version 2.0, or any later version. You may not use
 * this file except in compliance with the license. If you need a copy of the license,
 * please go to http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. Please refer to
 * the license for the specific language governing your rights and limitations.
 */

package pt.webdetails.cdf.dd.model.inst.writer.cdfrunjs.amd;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pt.webdetails.cdf.dd.model.core.KnownThingKind;
import pt.webdetails.cdf.dd.model.core.Thing;
import pt.webdetails.cdf.dd.model.core.UnsupportedThingException;
import pt.webdetails.cdf.dd.model.core.writer.IThingWriter;
import pt.webdetails.cdf.dd.model.core.writer.IThingWriterFactory;
import pt.webdetails.cdf.dd.model.inst.CodeComponent;
import pt.webdetails.cdf.dd.model.inst.Dashboard;
import pt.webdetails.cdf.dd.model.inst.DataSourceComponent;
import pt.webdetails.cdf.dd.model.inst.GenericComponent;
import pt.webdetails.cdf.dd.model.inst.ParameterComponent;
import pt.webdetails.cdf.dd.model.inst.WidgetComponent;
import pt.webdetails.cdf.dd.model.inst.writer.cdfrunjs.components.CdfRunJsCodeComponentWriter;
import pt.webdetails.cdf.dd.model.inst.writer.cdfrunjs.components.amd.CdfRunJsDataSourceComponentWriter;
import pt.webdetails.cdf.dd.model.inst.writer.cdfrunjs.components.amd.CdfRunJsDateParameterComponentWriter;
import pt.webdetails.cdf.dd.model.inst.writer.cdfrunjs.components.amd.CdfRunJsExpressionParameterComponentWriter;
import pt.webdetails.cdf.dd.model.inst.writer.cdfrunjs.components.amd.CdfRunJsGenericComponentWriter;
import pt.webdetails.cdf.dd.model.inst.writer.cdfrunjs.components.amd.CdfRunJsParameterComponentWriter;
import pt.webdetails.cdf.dd.model.inst.writer.cdfrunjs.dashboard.amd.CdfRunJsDashboardModuleWriter;
import pt.webdetails.cdf.dd.model.inst.writer.cdfrunjs.dashboard.amd.CdfRunJsDashboardWriter;
import pt.webdetails.cdf.dd.model.inst.writer.cdfrunjs.properties.CdfRunJsGenericPropertyBindingWriter;
import pt.webdetails.cdf.dd.structure.DashboardWcdfDescriptor;
import pt.webdetails.cdf.dd.structure.DashboardWcdfDescriptor.DashboardRendererType;

public class CdfRunJsThingWriterFactory implements IThingWriterFactory {
  protected static final Log logger = LogFactory.getLog( CdfRunJsThingWriterFactory.class );

  /**
   * @param dashboard the dashboard
   * @return an instance of a dashboard writer of the same render type as the provided dashboard
   */
  public CdfRunJsDashboardWriter getDashboardWriter( Dashboard dashboard ) {
    DashboardWcdfDescriptor wcdf = dashboard.getWcdf();
    DashboardRendererType rendererType = wcdf.getParsedRendererType();
    return new CdfRunJsDashboardWriter( rendererType );
  }

  public CdfRunJsDashboardModuleWriter getDashboardModuleWriter( Dashboard dashboard ) {
    DashboardWcdfDescriptor wcdf = dashboard.getWcdf();
    DashboardRendererType rendererType = wcdf.getParsedRendererType();
    return new CdfRunJsDashboardModuleWriter( rendererType );
  }

  public IThingWriter getWriter( Thing t ) throws UnsupportedThingException {
    if ( t == null ) {
      throw new IllegalArgumentException( "t" );
    }

    String kind = t.getKind();

    if ( KnownThingKind.Component.equals( kind ) ) {
      Class compClass = t.getClass();

      if ( GenericComponent.class.isAssignableFrom( compClass ) ) {
        if ( WidgetComponent.class.isAssignableFrom( compClass ) ) {
          logger.error( "Widget component is no longer supported" );
          throw new UnsupportedThingException( kind , t.getId() );
        }

        return new CdfRunJsGenericComponentWriter();
      }

      if ( ParameterComponent.class.isAssignableFrom( compClass ) ) {
        ParameterComponent paramComp = (ParameterComponent) t;
        String typeName = paramComp.getMeta().getName().toLowerCase();
        if ( typeName.equals( "parameter" ) || typeName.equals( "olapparameter" ) ) {
          return new CdfRunJsParameterComponentWriter();
        }
        if ( typeName.equals( "dateparameter" ) ) {
          return new CdfRunJsDateParameterComponentWriter();
        }
        if ( typeName.equals( "javascriptparameter" ) ) {
          return new CdfRunJsExpressionParameterComponentWriter();
        }
      }

      if ( CodeComponent.class.isAssignableFrom( compClass ) ) {
        return new CdfRunJsCodeComponentWriter();
      }

      if ( DataSourceComponent.class.isAssignableFrom( compClass ) ) {
        return new CdfRunJsDataSourceComponentWriter();
      }
    } else if ( KnownThingKind.PropertyBinding.equals( kind ) ) {
      return new CdfRunJsGenericPropertyBindingWriter();
    } else if ( KnownThingKind.Dashboard.equals( kind ) ) { // shouldn't get here anymore
      return getDashboardWriter( ( (Dashboard) t ) );
    }

    throw new UnsupportedThingException( kind, t.getId() );
  }
}
