/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 5.5)
 */
package com.orange.iot3core.clients;

/**
 * Transport layer used by the {@link MqttClient} to reach the MQTT broker.
 */
public enum MqttTransport {

    /** Classic TCP socket connection (TLS optional). */
    TCP("tcp"),
    /** MQTT over WebSocket (secured with TLS: wss). */
    WEBSOCKET("websocket");

    /** Default WebSocket server path. */
    public static final String DEFAULT_WEBSOCKET_PATH = "mqtt";

    public final String value;

    MqttTransport(String value) {
        this.value = value;
    }

    public static MqttTransport fromValue(String value) {
        for (MqttTransport transport : values()) {
            if (transport.value.equalsIgnoreCase(value)) {
                return transport;
            }
        }
        throw new IllegalArgumentException("Unknown MQTT transport: " + value);
    }
}

