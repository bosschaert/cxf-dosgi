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

import org.osgi.framework.ServiceReference;

/**
 * Provide access to metadata, such as Service Variables of Remote Services.
 */
public interface RemoteServiceMetadataProvider {
    /**
     * Obtain Remote Service Metadata for a given service, identified by
     * a Service Reference.
     * @param sref The Service Reference
     * @return The Remote Service Metadata for the service or {@code null} if there
     * is no metadata for this service or it does not reference a remote service.
     */
    RemoteServiceMetadata getMetadata(ServiceReference sref);

    /**
     * Obtain Remote Service Metadata for a service denoted by an endpoint ID.
     * @param endpointID The endpoint ID as advertised via the {@code endpoint.id}
     * service property of a remote service.
     * @return The Remote Service Metadata for the endpoint or {@code null} if no
     * metadata for this service can be found.
     */
    RemoteServiceMetadata getMetadata(String endpointID);

    /**
     * Obtain Remote Service Metadata for a service denoted by an endpoint ID and
     * framework UUID. This variant can be useful if endpoint IDs are not unique
     * across frameworks.
     * @param endpointID The endpoint ID as advertised via the {@code endpoint.id}
     * service property of remote service.
     * @param endpointFrameworkUUID The framework uuid as advertised via the
     * {@code endpoint.framework.uuid} property.
     * @return The Remote Service Metadata for the endpoint or {@code null} if no
     * metadata for this service can be found.
     */
    RemoteServiceMetadata getMetadata(String endpointID, String endpointFrameworkUUID);
}
