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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.dosgi.dsw.Constants;
import org.apache.cxf.dosgi.dsw.OsgiUtils;
import org.osgi.framework.BundleContext;

public final class ConfigTypeHandlerFactory {

    private static final Logger LOG = LogUtils.getL7dLogger(ConfigTypeHandlerFactory.class);
    private static final ConfigTypeHandlerFactory FACTORY = new ConfigTypeHandlerFactory();

    private ConfigTypeHandlerFactory() {
    }

    public static ConfigTypeHandlerFactory getInstance() {
        return FACTORY;
    }

    public ConfigurationTypeHandler getHandler(BundleContext dswBC, List<String> configurationTypes,
                                               Map serviceProperties, Map<String, Object> props) {

        if (configurationTypes.contains(Constants.WS_CONFIG_TYPE)
            || configurationTypes.contains(Constants.WS_CONFIG_TYPE_OLD) || configurationTypes.contains(Constants.RS_CONFIG_TYPE)) {

            boolean jaxrs = isJaxrsRequested(configurationTypes, serviceProperties);

            if (OsgiUtils.getProperty(serviceProperties, Constants.WS_HTTP_SERVICE_CONTEXT) != null
                || OsgiUtils.getProperty(serviceProperties, Constants.RS_HTTP_SERVICE_CONTEXT) != null
                || OsgiUtils.getProperty(serviceProperties, Constants.WS_HTTP_SERVICE_CONTEXT_OLD) != null) {
                return jaxrs
                    ? new JaxRSHttpServiceConfigurationTypeHandler(dswBC, props)
                    : new HttpServiceConfigurationTypeHandler(dswBC, props);

            } else {
                return jaxrs
                    ? new JaxRSPojoConfigurationTypeHandler(dswBC, props)
                    : new PojoConfigurationTypeHandler(dswBC, props);
            }
        } else if (configurationTypes.contains(Constants.WSDL_CONFIG_TYPE)) {
            return new WsdlConfigurationTypeHandler(dswBC, props);
        }

        LOG.warning("None of the configuration types in " + configurationTypes + " is supported.");

        return null;
    }

    private boolean isJaxrsRequested(Collection<String> types,  Map serviceProperties) {

        if (types == null) {
            return false;
        }

        if (types.contains(Constants.RS_CONFIG_TYPE)) {
            String intentsProperty = OsgiUtils.getProperty(serviceProperties, Constants.EXPORTED_INTENTS);
            boolean hasHttpIntent = false, hasSoapIntent = false;
            if (intentsProperty != null) {
                String[] intents = OsgiUtils.parseIntents(intentsProperty);
                for (int i = 0; i < intents.length; i++) {
                    if (intents[i].indexOf("SOAP") > -1) {
                        hasSoapIntent = true;
                        break;
                    }
                    if ("HTTP".equals(intents[i])) {
                        hasHttpIntent = true;
                    }
                }
            }
            if (intentsProperty != null && hasHttpIntent && !hasSoapIntent || intentsProperty == null) {
                return true;
            }
        }
        return false;
    }

}
