/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 4.6)
 */
package com.orange.iot3mobility.roadobjects;

/**
 * Status of a signal priority or preemption request, derived from the SSEM
 * {@code sig_status.status} field (ETSI IS TS 103 301).
 */
public enum PriorityRequestStatusType {

    UNKNOWN(0),
    REQUESTED(1),
    PROCESSING(2),
    WATCH_OTHER_TRAFFIC(3),
    GRANTED(4),
    REJECTED(5),
    MAX_PRESENCE(6),
    RESERVICE_LOCKED(7);

    public final int value;

    PriorityRequestStatusType(int value) {
        this.value = value;
    }

    /**
     * Returns the {@code PriorityRequestStatusType} matching the given integer value,
     * or {@link #UNKNOWN} if no match is found.
     */
    public static PriorityRequestStatusType fromValue(int value) {
        for (PriorityRequestStatusType type : values()) {
            if (type.value == value) return type;
        }
        return UNKNOWN;
    }
}

