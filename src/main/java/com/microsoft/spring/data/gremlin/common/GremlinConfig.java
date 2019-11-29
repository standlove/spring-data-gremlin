/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.tinkerpop.gremlin.driver.MessageSerializer;
import org.apache.tinkerpop.gremlin.driver.ser.Serializers;

@Getter
@Setter
@AllArgsConstructor
@Builder(builderMethodName = "defaultBuilder")
public class GremlinConfig {
    private String endpoint;

    private int port;

    private String username;

    private String password;

    private boolean sslEnabled;

    private boolean telemetryAllowed;

    private MessageSerializer serializer;

    private int maxContentLength;

    public GremlinConfig(final String endpoint, final int port, final String username,
                         final String password, final boolean sslEnabled, final boolean telemetryAllowed,
                         final String serializer, final int maxContentLength) {
        this.endpoint = endpoint;
        this.port = port;
        this.username = username;
        this.password = password;
        this.sslEnabled = sslEnabled;
        this.telemetryAllowed = telemetryAllowed;
        this.serializer = Serializers.valueOf(serializer).simpleInstance();
        this.maxContentLength = maxContentLength;
    }

    public static GremlinConfigBuilder builder(String endpoint, String username, String password) {
        return defaultBuilder()
                .endpoint(endpoint)
                .username(username)
                .password(password)
                .port(Constants.DEFAULT_ENDPOINT_PORT)
                .sslEnabled(true)
                .serializer(Serializers.GRAPHSON.simpleInstance())
                .telemetryAllowed(true);
    }
}
