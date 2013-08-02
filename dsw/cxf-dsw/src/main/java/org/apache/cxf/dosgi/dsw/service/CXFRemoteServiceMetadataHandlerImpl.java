/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.cxf.dosgi.dsw.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.cxf.dosgi.dsw.ClientContext;
import org.apache.cxf.dosgi.dsw.RemoteServiceMetadataHandler;
import org.apache.cxf.dosgi.dsw.handlers.OSGiRemoteServiceInvocationHandler;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class CXFRemoteServiceMetadataHandlerImpl implements CXFRemoteServiceMetadataHandler {
    private final BundleContext bundleContext;

    public CXFRemoteServiceMetadataHandlerImpl(BundleContext ctx) {
        bundleContext = ctx;
    }

    public String[] getServiceVariableNames(long id) {
        System.out.println("### getServiceVariableNames" + id);
        RemoteServiceMetadataHandler handler = getHandler(id);
        if (handler == null)
            return new String [] {};
        return handler.listServiceVariableNames(getClientContext());
    }

    public String getServiceVariable(long id, String name) {
        System.out.println("### getServiceVariable" + id + ":" + name);
        RemoteServiceMetadataHandler handler = getHandler(id);
        if (handler == null)
            return null;
        return handler.getServiceVariable(getClientContext(), name);
    }

    public Map<String, String> getServiceVariables(long id, String... filter) {
        RemoteServiceMetadataHandler handler = getHandler(id);
        if (handler == null)
            return Collections.emptyMap();

        Map<String, String> m = new HashMap<String, String>();
        for (String var : handler.listServiceVariableNames(getClientContext())) {
            try {
                m.put(var, handler.getServiceVariable(getClientContext(), var));
            } catch (Throwable th) {
                m.put(var, th.getMessage());
            }
        }
        return m;
    }

    private RemoteServiceMetadataHandler getHandler(long id) {
        try {
            ServiceReference[] refs = bundleContext.getServiceReferences(null, "(" + Constants.SERVICE_ID + "=" + id + ")");
            if (refs == null || refs.length < 1)
                return null;

            Object handler = refs[0].getProperty("service.exported.handler");
            if (handler instanceof RemoteServiceMetadataHandler) {
                System.out.println("### Found handler for service: " + id);
                return (RemoteServiceMetadataHandler) handler;
            }
            return null;
        } catch (InvalidSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private ClientContext getClientContext() {
        String clientIP = OSGiRemoteServiceInvocationHandler.ipAddress.get();
        if (clientIP == null)
            throw new IllegalArgumentException("Unable to obtain client IP");

        return new OSGiRemoteServiceInvocationHandler.CXFClientInfo(clientIP);
    }
}
