/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.jdom.*;
import org.jdom.output.XMLOutputter;

/** 
 * 
 */   
public class MetaOpenSearchServlet extends HttpServlet
{
  MetaOpenSearch meta;

  int timeout;
  int hitsPerPage;
  int hitsPerSite;

  public void init( ServletConfig config )
    throws ServletException 
  {
    this.timeout     = ServletHelper.getInitParameter( config, "timeout",      0,  0 );
    this.hitsPerPage = ServletHelper.getInitParameter( config, "hitsPerPage", 10,  1 );
    this.hitsPerSite = ServletHelper.getInitParameter( config, "hitsPerSite",  1,  0 );

    String rossFile = ServletHelper.getInitParameter( config, "ross", false );
    try
      {
        this.meta = new MetaOpenSearch( rossFile, timeout );
      }
    catch ( IOException ioe )
      {
        throw new ServletException( ioe );
      }
    
  }

  public void destroy( )
  {
    
  }

  public void doGet( HttpServletRequest request, HttpServletResponse response )
    throws ServletException, IOException 
  {
    long responseTime = System.nanoTime( );
    
    QueryParameters p = (QueryParameters) request.getAttribute( OpenSearchHelper.PARAMS_KEY );
    if ( p == null )
      {
        p = getQueryParameters( request );
      }
        
    Document doc = meta.query( p );

    (new XMLOutputter()).output( doc, response.getOutputStream( ) );
  }

  public QueryParameters getQueryParameters( HttpServletRequest request )
  {
    QueryParameters p = new QueryParameters( );
    
    p.query      = ServletHelper.getParam( request, "q", "" );
    p.start      = ServletHelper.getParam( request, "p", 0 );
    p.hitsPerPage= ServletHelper.getParam( request, "n", this.hitsPerPage );
    p.hitsPerSite= ServletHelper.getParam( request, "h", this.hitsPerSite );
    p.sites      = ServletHelper.getParam( request, "s", QueryParameters.EMPTY_STRINGS );
    p.indexNames = ServletHelper.getParam( request, "i", QueryParameters.ALL_INDEXES );
    p.collections= ServletHelper.getParam( request, "c", QueryParameters.EMPTY_STRINGS );
    p.types      = ServletHelper.getParam( request, "t", QueryParameters.EMPTY_STRINGS );
    
    return p;
  }

}