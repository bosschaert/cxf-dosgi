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
package org.apache.cxf.dosgi.dsw.handlers;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.apache.cxf.dosgi.dsw.ClientContext;
import org.apache.cxf.dosgi.dsw.RemoteServiceInvocationHandler;
import org.osgi.framework.ServiceReference;

public class OSGiRemoteServiceInvocationHandler implements InvocationHandler {
    // a bit of a hack, a place to store the current client-side IP address. Need to move elsewhere.
    public static ThreadLocal<String> ipAddress = new ThreadLocal<String>();

    private final ServiceReference serviceReference;
    private final RemoteServiceInvocationHandler<?> remoteInvocationHandler;

    public OSGiRemoteServiceInvocationHandler(ServiceReference sr, RemoteServiceInvocationHandler<?> factory) {
        serviceReference = sr;
        remoteInvocationHandler = factory;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String clientIP = ipAddress.get();
        if (clientIP == null)
            throw new IllegalArgumentException("Unable to establish client IP");

        ClientContext clientInfo = new CXFClientInfo(clientIP);

        return remoteInvocationHandler.invoke(clientInfo, serviceReference, method, args);
    }

    public static class CXFClientInfo implements ClientContext {
        private final String ip;

        public CXFClientInfo(String clientIP) {
            ip = clientIP;
        }

        public String getHostIPAddress() {
            return ip;
        }

        public String getFrameworkUUID() {
            return null; // TODO
        }

        public Principal getPrincipal() {
            return null; // TODO
        }

        public Map<String, Object> getProperties() {
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("HostIPAddress", ip);
            return m;
        }

        @Override
        public String toString() {
            return "ClientInfo [ip=" + ip + "]";
        }
    }
}
