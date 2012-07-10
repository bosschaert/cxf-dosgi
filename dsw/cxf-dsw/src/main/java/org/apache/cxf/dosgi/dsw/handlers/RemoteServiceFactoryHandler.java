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

import org.apache.cxf.dosgi.dsw.RemoteServiceFactory;
import org.osgi.framework.ServiceReference;

public class RemoteServiceFactoryHandler implements InvocationHandler {
    static ThreadLocal<String> ipAddress = new ThreadLocal<String>();

    private final ServiceReference serviceReference;
    private final RemoteServiceFactory remoteServiceFactory;

    public RemoteServiceFactoryHandler(ServiceReference sr, RemoteServiceFactory factory) {
        serviceReference = sr;
        remoteServiceFactory = factory;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String clientIP = ipAddress.get();
        if (clientIP == null)
            throw new IllegalArgumentException("Unable to establish client IP");

        Object svc = remoteServiceFactory.getService(clientIP, serviceReference);
        try {
            return method.invoke(svc, args);
        } finally {
            remoteServiceFactory.ungetService(clientIP, serviceReference, svc);
        }
    }
}
