package org.apache.cxf.dosgi.dsw;

public interface RemoteServiceMetadataHandler {
    String[] listServiceVariablesNames();
    String getServiceVariable(String name);
}
