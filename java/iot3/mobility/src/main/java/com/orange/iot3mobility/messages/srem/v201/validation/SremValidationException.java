/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 4.6)
 */
package com.orange.iot3mobility.messages.srem.v201.validation;

/**
 * Unchecked exception thrown when a SREM v2.0.1 payload fails structural validation.
 */
public class SremValidationException extends RuntimeException {

    public SremValidationException(String message) {
        super(message);
    }
}

