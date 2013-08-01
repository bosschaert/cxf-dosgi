package org.apache.cxf.dosgi.dsw;

import java.util.Map;

public interface RemoteServiceMetadata {
    String[] listVariablesNames();
    String getVariable(String name);
    Map<String, String> getVariables(String ... filter);
}
