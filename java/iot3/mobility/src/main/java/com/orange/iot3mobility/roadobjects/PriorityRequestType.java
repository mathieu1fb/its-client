/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 4.6)
 */
package com.orange.iot3mobility.roadobjects;

/**
 * Type of a signal priority or preemption request, derived from the SREM
 * {@code request_type} field (ETSI IS TS 103 301).
 */
public enum PriorityRequestType {

    RESERVED(0),
    PRIORITY_REQUEST(1),
    PRIORITY_REQUEST_UPDATE(2),
    PRIORITY_CANCELLATION(3),
    /** Fallback for unrecognised values. */
    UNKNOWN(-1);

    public final int value;

    PriorityRequestType(int value) {
        this.value = value;
    }

    /**
     * Returns the {@code PriorityRequestType} matching the given integer value,
     * or {@link #UNKNOWN} if no match is found.
     */
    public static PriorityRequestType fromValue(int value) {
        for (PriorityRequestType type : values()) {
            if (type.value == value && type != UNKNOWN) return type;
        }
        return UNKNOWN;
    }
}

