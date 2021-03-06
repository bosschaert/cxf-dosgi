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
package org.apache.cxf.dosgi.topologymanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easymock.IAnswer;
import org.easymock.IMocksControl;
import org.easymock.classextension.EasyMock;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.remoteserviceadmin.EndpointDescription;
import org.osgi.service.remoteserviceadmin.EndpointListener;
import org.osgi.service.remoteserviceadmin.ExportReference;
import org.osgi.service.remoteserviceadmin.ExportRegistration;

import org.junit.Test;

public class TopologyManagerTest {

    @Test
    public void testNotifyListenersOfRemovalIfAppropriate() throws InvalidSyntaxException {

        IMocksControl c = EasyMock.createNiceControl();

        BundleContext bc = c.createMock(BundleContext.class);
        ServiceReference sref = c.createMock(ServiceReference.class);
        ExportRegistration exReg = c.createMock(ExportRegistration.class);
        ExportRegistration exReg2 = c.createMock(ExportRegistration.class);
        EndpointListener epl = EasyMock.createMock(EndpointListener.class);
        EndpointDescription epd = c.createMock(EndpointDescription.class);
        EndpointDescription epd2 = c.createMock(EndpointDescription.class);
        ExportReference exRef = c.createMock(ExportReference.class);
        ExportReference exRef2 = c.createMock(ExportReference.class);
        
        
        Map props = new HashMap();
        String[] oc = new String[1];
        oc[0] = "myClass";
        props.put("objectClass", oc);
        
        Map props2 = new HashMap();
        oc = new String[1];
        oc[0] = "notMyClass";
        props2.put("objectClass", oc);
        
        
        EasyMock.expect(bc.getService(EasyMock.eq(sref))).andReturn(epl).anyTimes();
        EasyMock.expect(bc.createFilter((String)EasyMock.anyObject())).andAnswer(new IAnswer<Filter>() {
            public Filter answer() throws Throwable {
                return FrameworkUtil.createFilter((String)EasyMock.getCurrentArguments()[0]);
            }
        }).anyTimes();
        EasyMock.expect(sref.getProperty(EasyMock.eq(EndpointListener.ENDPOINT_LISTENER_SCOPE)))
            .andReturn("(objectClass=myClass)").anyTimes();

        
        EasyMock.expect(exReg.getExportReference()).andReturn(exRef).anyTimes();
        EasyMock.expect(exRef.getExportedEndpoint()).andReturn(epd).anyTimes();
        EasyMock.expect(epd.getProperties()).andReturn(props).anyTimes();
        
        EasyMock.expect(exReg2.getExportReference()).andReturn(exRef2).anyTimes();
        EasyMock.expect(exRef2.getExportedEndpoint()).andReturn(epd2).anyTimes();
        EasyMock.expect(epd2.getProperties()).andReturn(props2).anyTimes();
        
        // must only be called for the first EndpointDestription ! 
        epl.endpointRemoved(EasyMock.eq(epd), EasyMock.eq("(objectClass=myClass)"));
        EasyMock.expectLastCall().once();
        
        c.replay();
        EasyMock.replay(epl);
        
        TopologyManager tm = new TopologyManager(bc, null);


        List<ExportRegistration> exRegs = new ArrayList<ExportRegistration>();
        exRegs.add(exReg);
        exRegs.add(exReg2);

        tm.notifyListenersOfRemovalIfAppropriate(sref, exRegs);

        c.verify();
        EasyMock.verify(epl);

    }

}
