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

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.cxf.dosgi.dsw.RemoteServiceMetadata;
import org.apache.cxf.dosgi.dsw.RemoteServiceMetadataProvider;
import org.osgi.framework.ServiceReference;
import org.osgi.service.remoteserviceadmin.RemoteConstants;

public class RemoteServiceMetadataProviderImpl implements RemoteServiceMetadataProvider {
    ConcurrentMap<String, List<MetadataHolder>> metadata = new ConcurrentHashMap<String, List<MetadataHolder>>();

    public void addMetadata(String endpointID, String endpointFrameworkUUID, RemoteServiceMetadata rsm) {
        List<MetadataHolder> l = new CopyOnWriteArrayList<MetadataHolder>();
        List<MetadataHolder> l2 = metadata.putIfAbsent(endpointID, l);
        if (l2 != null)
            l = l2;

        l.add(new MetadataHolder(endpointFrameworkUUID, rsm));
    }

    public RemoteServiceMetadata getMetadata(ServiceReference sref) {
        return getMetadata((String) sref.getProperty(RemoteConstants.ENDPOINT_ID),
                (String) sref.getProperty(RemoteConstants.ENDPOINT_FRAMEWORK_UUID));
    }

    public RemoteServiceMetadata getMetadata(String endpointID) {
        return getMetadata(endpointID, null);
    }

    public RemoteServiceMetadata getMetadata(String endpointID, String endpointFrameworkUUID) {
        List<MetadataHolder> list = metadata.get(endpointID);
        if (list == null || list.size() == 0)
            return null;

        if (endpointFrameworkUUID == null)
            return list.get(0).metadata;

        for (MetadataHolder mh : list) {
            if (endpointFrameworkUUID.equals(mh.frameworkUUID)) {
                return mh.metadata;
            }
        }

        return null;
    }

    private static class MetadataHolder {
        final String frameworkUUID;
        final RemoteServiceMetadata metadata;

        public MetadataHolder(String endpointFrameworkUUID, RemoteServiceMetadata rsm) {
            frameworkUUID = endpointFrameworkUUID;
            metadata = rsm;
        }

        // Hashcode and equals are generated and only look at the UUID.
        // Withint the same framework the endpoing IDs must be unique,
        // so two of them in a single framework with the same ID must be the same one
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((frameworkUUID == null) ? 0 : frameworkUUID.hashCode());
            return result;
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            MetadataHolder other = (MetadataHolder) obj;
            if (frameworkUUID == null) {
                if (other.frameworkUUID != null)
                    return false;
            } else if (!frameworkUUID.equals(other.frameworkUUID))
                return false;
            return true;
        }
    }

    /*
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
    */
}
