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

import java.security.Principal;

import org.osgi.framework.ServiceReference;

/**
 * Provides control over services used by remote clients.
 */
public interface RemoteServiceFactory<T> {
    /**
     * Called before every invocation of the service by a remote client.
     *
     * @param clientIP The IP Address of the client.
     * @param reference The OSGi Service Reference of the service being invoked.
     * @return The Service Object for the client to use.
     */
    public T getService(Principal client, ServiceReference /*<T>*/ reference);

    /**
     * Called after every invocation of the service by a remote client.
     *
     * @param clientIP The IP Address of the client.
     * @param reference The OSGi Service Reference of the service that was invoked.
     * @param service The Service Object that the client invoked.
     */
    public void ungetService(Principal client, ServiceReference /*<T>*/ reference, T service);
}
