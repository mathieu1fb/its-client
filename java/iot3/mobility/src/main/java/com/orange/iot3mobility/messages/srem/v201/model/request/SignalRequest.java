/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 4.6)
 */
package com.orange.iot3mobility.messages.srem.v201.model.request;

/**
 * A single signal priority or preemption request targeting one intersection.
 *
 * @param id           Required. Intersection identifier.
 * @param requestId    Required. Request ID [0..255].
 * @param requestType  Required. Priority request type [0..3]:
 *                     priorityRequestTypeReserved (0), priorityRequest (1),
 *                     priorityRequestUpdate (2), priorityCancellation (3).
 * @param inboundLane  Required. Inbound lane or approach where the requestor is located.
 * @param outboundLane Optional. Outbound lane or approach the requestor intends to use.
 */
public record SignalRequest(
        IntersectionReferenceId id,
        int requestId,
        int requestType,
        IntersectionAccessPoint inboundLane,
        IntersectionAccessPoint outboundLane) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private IntersectionReferenceId id;
        private Integer requestId;
        private Integer requestType;
        private IntersectionAccessPoint inboundLane;
        private IntersectionAccessPoint outboundLane;

        private Builder() {}

        public Builder id(IntersectionReferenceId id) { this.id = id; return this; }
        public Builder requestId(int requestId) { this.requestId = requestId; return this; }
        public Builder requestType(int requestType) { this.requestType = requestType; return this; }
        public Builder inboundLane(IntersectionAccessPoint inboundLane) { this.inboundLane = inboundLane; return this; }
        public Builder outboundLane(IntersectionAccessPoint outboundLane) { this.outboundLane = outboundLane; return this; }

        public SignalRequest build() {
            return new SignalRequest(
                    requireNonNull(id, "id"),
                    requireNonNull(requestId, "requestId"),
                    requireNonNull(requestType, "requestType"),
                    requireNonNull(inboundLane, "inboundLane"),
                    outboundLane);
        }

        private static <T> T requireNonNull(T value, String field) {
            if (value == null) throw new IllegalStateException("Missing field: " + field);
            return value;
        }
    }
}

