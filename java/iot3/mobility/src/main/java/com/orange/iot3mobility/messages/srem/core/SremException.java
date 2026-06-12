/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 4.6)
 */
package com.orange.iot3mobility.messages.srem.core;

/**
 * Unchecked exception thrown when a SREM payload cannot be parsed or encoded.
 */
public class SremException extends RuntimeException {

    public SremException(String message) {
        super(message);
    }

    public SremException(String message, Throwable cause) {
        super(message, cause);
    }
}

