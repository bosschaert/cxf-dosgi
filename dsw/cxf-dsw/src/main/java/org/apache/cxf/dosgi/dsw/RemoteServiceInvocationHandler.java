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
package org.apache.cxf.dosgi.dsw;

import java.lang.reflect.Method;

import org.osgi.framework.ServiceReference;

/**
 * Provides control over services used by remote clients. When an instance of this class
 * is attached to the "service.exported.handler" property of the service registration the
 * handler will be called for each remote invocation instead of the service object directly.
 */
public interface RemoteServiceInvocationHandler<T> {
    /**
     * Invoke the service on behalf of a remote client.
     * @param client Information in relation to the client.
     * @param reference The Service Reference representing the service being invoked.
     * @param method The method of the service being invoked.
     * @param args The method arguments.
     * @return Provide the return value to be returned to the remote client.
     * @throws Exception if the embedded invocation throws an exception.
     */
    public Object invoke(ClientContext client, ServiceReference /*<T>*/ reference, Method method, Object[] args) throws Exception;
}
