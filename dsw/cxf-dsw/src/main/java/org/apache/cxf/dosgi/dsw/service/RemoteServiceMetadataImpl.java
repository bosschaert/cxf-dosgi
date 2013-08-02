package org.apache.cxf.dosgi.dsw.service;

import java.util.Dictionary;
import java.util.Map;

import org.apache.cxf.dosgi.dsw.RemoteServiceMetadata;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.remoteserviceadmin.RemoteConstants;
import org.osgi.util.tracker.ServiceTracker;

public class RemoteServiceMetadataImpl implements RemoteServiceMetadata {
    private static final String SERVICE_STATUS = "service.status";
    private final long endpointServiceID;
    private final ServiceTracker handlerTracker;
    private final UpdateStatusPropertyCallBack updateStatusCallBack;
    private String lastStatus = null;

    public RemoteServiceMetadataImpl(BundleContext ctx, Dictionary<String, Object> serviceProps, final UpdateStatusPropertyCallBack callback) {
        updateStatusCallBack = callback;
        try {
            endpointServiceID = (Long) serviceProps.get(RemoteConstants.ENDPOINT_SERVICE_ID);

            String filter = "(&(objectClass=" + CXFRemoteServiceMetadataHandler.class.getName() +
                    ")(" + RemoteConstants.ENDPOINT_FRAMEWORK_UUID + "=" +
                    serviceProps.get(RemoteConstants.ENDPOINT_FRAMEWORK_UUID) + "))";
            handlerTracker = new ServiceTracker(ctx, ctx.createFilter(filter), null) {
                @Override
                public Object addingService(ServiceReference reference) {
                    new Thread(new Runnable() {
                        public void run() {
                            getVariable(SERVICE_STATUS);
                        }
                    }).start();
                    Object svc = super.addingService(reference);
                    System.out.println("### Service added now:" + svc);
                    return svc;
                }
            };
            handlerTracker.open();
            System.out.println("### Looking for a service: " + filter);
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
        System.out.println("### Found service: " + svc);
        if (svc instanceof CXFRemoteServiceMetadataHandler) {
            String result = ((CXFRemoteServiceMetadataHandler) svc).getServiceVariable(endpointServiceID, name);
            if (SERVICE_STATUS.equals(name)) {
                System.out.println("### Obtained service.status: " + result);
                if (result == null) {
                    if (lastStatus != null) {
                        updateStatus(result);
                    }
                } else if (!result.equals(lastStatus)) {
                    updateStatus(result);
                }
            }
            return result;
        }
        return null;
    }

    private void updateStatus(String status) {
        System.out.println("### Updating Service Status: " + status);
        lastStatus = status;
        updateStatusCallBack.updateStatusProperty(this, status);
    }

    public Map<String, String> getVariables(String... filters) {
        Object svc = handlerTracker.getService();
        if (svc instanceof CXFRemoteServiceMetadataHandler) {
            Map<String, String> serviceVariables = ((CXFRemoteServiceMetadataHandler) svc).getServiceVariables(endpointServiceID, filters);
            if (serviceVariables.containsKey(SERVICE_STATUS)) {
                String status = serviceVariables.get(SERVICE_STATUS);
                System.out.println("~~~ Obtained service.status: " + status);
                if (status == null) {
                    if (lastStatus != null) {
                        updateStatus(status);
                    }
                } else if (!status.equals(lastStatus)) {
                    updateStatus(status);
                }
            }
            return serviceVariables;
        }
        return null;
    }

    public static interface UpdateStatusPropertyCallBack {
        void updateStatusProperty(RemoteServiceMetadata rsm, String status);
    }
}

