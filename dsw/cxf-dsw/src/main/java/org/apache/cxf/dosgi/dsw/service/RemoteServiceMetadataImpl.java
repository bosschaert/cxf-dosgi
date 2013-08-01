package org.apache.cxf.dosgi.dsw.service;

import java.util.Dictionary;
import java.util.Map;

import org.apache.cxf.dosgi.dsw.RemoteServiceMetadata;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.remoteserviceadmin.RemoteConstants;
import org.osgi.util.tracker.ServiceTracker;

public class RemoteServiceMetadataImpl implements RemoteServiceMetadata {
    private final long endpointServiceID;
    private final ServiceTracker handlerTracker;

    public RemoteServiceMetadataImpl(BundleContext ctx, Dictionary<String, Object> serviceProps) {
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

