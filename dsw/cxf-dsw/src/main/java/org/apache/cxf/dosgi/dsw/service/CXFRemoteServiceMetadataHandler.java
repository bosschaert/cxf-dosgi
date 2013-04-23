package org.apache.cxf.dosgi.dsw.service;

public interface CXFRemoteServiceMetadataHandler {
    String [] getServiceVariableNames(long id);
    String getServiceVariable(long id, String name);
    // Map<String, String> getServiceVariables(String ... filter);
}
