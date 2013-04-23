package org.apache.cxf.dosgi.dsw.service;

import org.apache.cxf.dosgi.dsw.ClientInfo;
import org.apache.cxf.dosgi.dsw.RemoteServiceInvocationHandler;
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
        System.out.println("^^^ in getServiceVariableNames() : " + id);
        return getHandler(id).listServiceVariablesNames(getClientContext());
    }

    public String getServiceVariable(long id, String name) {
        System.out.println("^^^ in getServiceVariable() : " + id + "^" + name);
        return getHandler(id).getServiceVariable(getClientContext(), name);
    }

    private RemoteServiceInvocationHandler<?> getHandler(long id) {
        try {
            ServiceReference[] refs = bundleContext.getServiceReferences(null, "(" + Constants.SERVICE_ID + "=" + id + ")");
            if (refs == null || refs.length < 1)
                return null;

            Object handler = refs[0].getProperty("service.exported.handler");
            if (handler instanceof RemoteServiceInvocationHandler) {
                return (RemoteServiceInvocationHandler<?>) handler;
            }
            return null;
        } catch (InvalidSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private ClientInfo getClientContext() {
        String clientIP = OSGiRemoteServiceInvocationHandler.ipAddress.get();
        if (clientIP == null)
            throw new IllegalArgumentException("Unable to obtain client IP");

        return new OSGiRemoteServiceInvocationHandler.CXFClientInfo(clientIP);
    }
}
