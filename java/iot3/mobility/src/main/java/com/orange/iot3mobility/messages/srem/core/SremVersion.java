/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 4.6)
 */
package com.orange.iot3mobility.messages.srem.core;

/**
 * Supported SREM JSON envelope versions.
 */
public enum SremVersion {
    V2_0_1("2.0.1");

    private final String jsonValue;

    SremVersion(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    public String jsonValue() {
        return jsonValue;
    }

    public static SremVersion fromJsonValue(String value) {
        for (SremVersion version : values()) {
            if (version.jsonValue.equals(value)) {
                return version;
            }
        }
        throw new SremException("Unsupported SREM version: " + value);
    }
}

