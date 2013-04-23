package org.apache.cxf.dosgi.dsw.service;

import java.util.Dictionary;

import org.apache.cxf.dosgi.dsw.RemoteServiceMetadataHandler;
import org.osgi.framework.BundleContext;
import org.osgi.service.remoteserviceadmin.RemoteConstants;
import org.osgi.util.tracker.ServiceTracker;

public class ServiceMetadataHandler implements RemoteServiceMetadataHandler {
    private final long endpointServiceID;
    private final ServiceTracker handlerTracker;

    public ServiceMetadataHandler(BundleContext ctx, Dictionary<String, Object> serviceProps) {
        endpointServiceID = (Long) serviceProps.get(RemoteConstants.ENDPOINT_SERVICE_ID);
        handlerTracker = new ServiceTracker(ctx, CXFRemoteServiceMetadataHandler.class.getName(), null);
        handlerTracker.open();
    }

    // TODO add close method...
    public String[] listServiceVariablesNames() {
        return new String [] {"a", "b", "c"};
        /*
        Object svc = handlerTracker.getService();
        if (svc instanceof CXFRemoteServiceMetadataHandler) {
            return ((CXFRemoteServiceMetadataHandler) svc).getServiceVariableNames(endpointServiceID);
        }
        return null;
        */
    }

    public String getServiceVariable(String name) {
        return "testing 123";
        /*
        Object svc = handlerTracker.getService();
        if (svc instanceof CXFRemoteServiceMetadataHandler) {
            return ((CXFRemoteServiceMetadataHandler) svc).getServiceVariable(endpointServiceID, name);
        }
        return null;
        */
    }
}
