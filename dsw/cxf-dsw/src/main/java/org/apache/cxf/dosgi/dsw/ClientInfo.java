package org.apache.cxf.dosgi.dsw;

import java.security.Principal;

public interface ClientInfo {
    String getHostIPAddress();
    String getFrameworkUUID();
    Principal getPrincipal();
}
