/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package net.tirasa.wadl2html;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.cocoon.pipeline.CachingPipeline;
import org.apache.cocoon.pipeline.Pipeline;
import org.apache.cocoon.sax.SAXPipelineComponent;
import org.apache.cocoon.sax.component.XMLGenerator;
import org.apache.cocoon.sax.component.XMLSerializer;
import org.apache.cocoon.sax.component.XSLTTransformer;

public class WADLServlet extends HttpServlet {

    private static final long serialVersionUID = -6737005675471095560L;

    private static final Pattern SCHEMA_PATTERN = Pattern.compile("/schema_(.*)_(.*)\\.html");

    private void staticResource(final String requestURI, final HttpServletResponse response)
            throws ServletException, IOException {

        URLConnection connection = null;
        try {
            connection = getServletContext().getResource(requestURI).openConnection();
            InputStream inputStream = connection.getInputStream();

            byte[] data = new byte[1024];
            while (true) {
                int bytesRead = inputStream.read(data, 0, data.length);

                if (bytesRead == -1) {
                    break;
                }

                response.getOutputStream().write(data, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new ServletException("Cannot read from '" + requestURI + "'", e);
        } finally {
            if (connection != null && connection.getDoInput()) {
                InputStream inputStream = null;
                try {
                    inputStream = connection.getInputStream();
                } catch (IOException e) {
                    getServletContext().log("Can't close input stream from " + connection.getURL(), e);
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            }
        }
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {

        final String requestURI = request.getRequestURI().substring(
                request.getRequestURI().indexOf(request.getServletPath()) + request.getServletPath().length());
        final Matcher schemaMatcher = SCHEMA_PATTERN.matcher(requestURI);

        final Pipeline<SAXPipelineComponent> pipeline = new CachingPipeline<SAXPipelineComponent>();
        pipeline.addComponent(new XMLGenerator(getClass().getResource("/application.wadl")));
        if ("/".equals(requestURI)) {
            final XSLTTransformer xslt = new XSLTTransformer(getClass().getResource("/index.xsl"));

            final Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("contextPath", request.getContextPath());
            xslt.setParameters(parameters);

            pipeline.addComponent(xslt);
        } else if (schemaMatcher.matches()) {
            final XSLTTransformer xslt = new XSLTTransformer(getClass().getResource("/schema.xsl"));

            final Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("contextPath", request.getContextPath());
            parameters.put("schema-position", schemaMatcher.group(1));
            parameters.put("schema-prefix", schemaMatcher.group(2));
            xslt.setParameters(parameters);

            pipeline.addComponent(xslt);
        } else {
            staticResource(requestURI, response);
            return;
        }

        pipeline.addComponent(XMLSerializer.createHTML4Serializer());
        pipeline.setup(response.getOutputStream());
        try {
            pipeline.execute();
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

}
