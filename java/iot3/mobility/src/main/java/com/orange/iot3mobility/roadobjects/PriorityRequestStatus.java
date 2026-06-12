/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 4.6)
 */
package com.orange.iot3mobility.roadobjects;

/**
 * Version-agnostic representation of the preemption or priority status for one request
 * at a signal controller, derived from a SSEM {@code sig_status} package
 * (ETSI IS TS 103 301).
 * <p>
 * Each instance corresponds to a single requestor's active priority or preemption request
 * and its current handling state at the intersection.
 */
public class PriorityRequestStatus {

    /** Required. Inbound access point from which the requestor approached. */
    private final PriorityAccessPoint inboundOn;

    /** Required. Current handling state of this request. */
    private final PriorityRequestStatusType statusType;

    /** Optional. Station ID of the vehicle that sent the original SREM, from {@code requester.id}. */
    private final Long requestorStationId;

    /** Optional. Request identifier echoed from the SREM, from {@code requester.request}. */
    private final Integer requestId;

    /** Optional. Outbound access point the requestor intends to use. */
    private final PriorityAccessPoint outboundOn;

    /** Optional. ETA — minute of the current UTC year [0..527040]. */
    private final Integer etaMinute;

    /** Optional. ETA — milliseconds within the minute [0..60999]. */
    private final Integer etaSecond;

    /** Optional. ETA window duration [0..60999]. */
    private final Integer etaDuration;

    public PriorityRequestStatus(PriorityAccessPoint inboundOn,
                                 PriorityRequestStatusType statusType,
                                 Long requestorStationId,
                                 Integer requestId,
                                 PriorityAccessPoint outboundOn,
                                 Integer etaMinute,
                                 Integer etaSecond,
                                 Integer etaDuration) {
        this.inboundOn = inboundOn;
        this.statusType = statusType;
        this.requestorStationId = requestorStationId;
        this.requestId = requestId;
        this.outboundOn = outboundOn;
        this.etaMinute = etaMinute;
        this.etaSecond = etaSecond;
        this.etaDuration = etaDuration;
    }

    /** Returns the inbound access point. */
    public PriorityAccessPoint getInboundOn() { return inboundOn; }

    /** Returns the current handling status of this request. */
    public PriorityRequestStatusType getStatusType() { return statusType; }

    /**
     * Returns the station ID of the vehicle that sent the original SREM,
     * or {@code null} if no requester information is present.
     */
    public Long getRequestorStationId() { return requestorStationId; }

    /**
     * Returns the request identifier echoed from the SREM,
     * or {@code null} if no requester information is present.
     */
    public Integer getRequestId() { return requestId; }

    /**
     * Returns the outbound access point the requestor intends to use,
     * or {@code null} if not specified.
     */
    public PriorityAccessPoint getOutboundOn() { return outboundOn; }

    /**
     * Returns the ETA minute of the current UTC year [0..527040],
     * or {@code null} if not specified.
     */
    public Integer getEtaMinute() { return etaMinute; }

    /**
     * Returns the ETA milliseconds within the minute [0..60999],
     * or {@code null} if not specified.
     */
    public Integer getEtaSecond() { return etaSecond; }

    /**
     * Returns the ETA duration extension in milliseconds [0..60999],
     * or {@code null} if not specified.
     */
    public Integer getEtaDuration() { return etaDuration; }
}

