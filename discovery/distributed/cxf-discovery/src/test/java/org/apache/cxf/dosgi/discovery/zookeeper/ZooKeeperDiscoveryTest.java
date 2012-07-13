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
package org.apache.cxf.dosgi.discovery.zookeeper;

import java.lang.reflect.Field;
import java.util.Dictionary;
import java.util.Hashtable;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.osgi.framework.BundleContext;

public class ZooKeeperDiscoveryTest extends TestCase {
    public void testZooKeeperDiscoveryConstructor() throws Exception {
        BundleContext ctx = EasyMock.createMock(BundleContext.class);

        Dictionary<String, Object> initialProps = new Hashtable<String, Object>();
        initialProps.put("publish.filter", "(foo=bar)");
        ZooKeeperDiscovery zkd = new ZooKeeperDiscovery(ctx, initialProps);

        assertEquals(initialProps, getField(zkd, "properties"));
        EndpointListenerFactory elf = (EndpointListenerFactory) getField(zkd, "endpointListenerFactory");
        assertEquals("(foo=bar)", getField(elf, "publishFilter"));
        assertNotNull(getField(zkd, "endpointListenerTracker"));
    }

    private Object getField(Object obj, String name) throws Exception {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        return f.get(obj);
    }
}
