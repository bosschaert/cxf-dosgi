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

import java.util.Dictionary;
import java.util.Map;

import org.apache.cxf.dosgi.dsw.RemoteServiceMetadataProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.remoteserviceadmin.RemoteConstants;
import org.osgi.util.tracker.ServiceTracker;

public class ServiceMetadataHandler implements RemoteServiceMetadataProvider {
    private final long endpointServiceID;
    private final ServiceTracker handlerTracker;

    public ServiceMetadataHandler(BundleContext ctx, Dictionary<String, Object> serviceProps) {
        try {
            endpointServiceID = (Long) serviceProps.get(RemoteConstants.ENDPOINT_SERVICE_ID);

            String filter = "(&(objectClass=" + CXFRemoteServiceMetadataHandler.class.getName() +
                    ")(" + RemoteConstants.ENDPOINT_FRAMEWORK_UUID + "=" +
                    serviceProps.get(RemoteConstants.ENDPOINT_FRAMEWORK_UUID) + "))";
            handlerTracker = new ServiceTracker(ctx, ctx.createFilter(filter), null);
            handlerTracker.open();
        } catch (InvalidSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    // TODO add close method...
    public String[] listVariablesNames() {
        Object svc = handlerTracker.getService();
        if (svc instanceof CXFRemoteServiceMetadataHandler) {
            return ((CXFRemoteServiceMetadataHandler) svc).getServiceVariableNames(endpointServiceID);
        }
        return null;
    }

    public String getVariable(String name) {
        Object svc = handlerTracker.getService();
        if (svc instanceof CXFRemoteServiceMetadataHandler) {
            return ((CXFRemoteServiceMetadataHandler) svc).getServiceVariable(endpointServiceID, name);
        }
        return null;
    }

    public Map<String, String> getVariables(String... filters) {
        Object svc = handlerTracker.getService();
        if (svc instanceof CXFRemoteServiceMetadataHandler) {
            return ((CXFRemoteServiceMetadataHandler) svc).getServiceVariables(endpointServiceID, filters);
        }
        return null;
    }
}
