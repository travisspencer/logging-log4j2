/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.logging.log4j.core.config.plugins.osgi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.util.PluginManager;
import org.apache.logging.log4j.core.helpers.lang.BundleResourceLoader;
import org.apache.logging.log4j.core.impl.Log4jContextFactory;
import org.apache.logging.log4j.simple.SimpleLoggerContextFactory;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.apache.logging.log4j.status.StatusLogger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

/**
 * OSGi BundleActivator.
 */
public class Activator implements org.osgi.framework.BundleActivator {

    private static final Logger LOGGER = StatusLogger.getLogger();

    @Override
    public void start(final BundleContext context) throws Exception {
        registerLoggerContextFactory();
        context.addBundleListener(new Listener());
    }

    private void registerLoggerContextFactory() {
        final LoggerContextFactory current = LogManager.getFactory();
        if (!(current instanceof Log4jContextFactory)) {
            LogManager.setFactory(new Log4jContextFactory());
        }
    }

    @Override
    public void stop(final BundleContext context) throws Exception {
        unregisterLoggerContextFactory();
    }

    private void unregisterLoggerContextFactory() {
        LogManager.setFactory(new SimpleLoggerContextFactory());
    }

    private static class Listener implements BundleListener {

        @Override
        public void bundleChanged(final BundleEvent event) {
            switch (event.getType()) {
                case BundleEvent.STARTED:
                    PluginManager.loadPlugins(new BundleResourceLoader(event.getBundle()));
                    break;

                default:
                    break;
            }
        }
    }
}
