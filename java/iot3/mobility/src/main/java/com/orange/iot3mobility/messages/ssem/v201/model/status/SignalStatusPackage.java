/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 4.6)
 */
package com.orange.iot3mobility.messages.ssem.v201.model.status;

/**
 * Describes the preemption or priority state for one request at a signal controller.
 *
 * @param inboundOn  Required. The inbound lane or approach from which the request originated.
 * @param status     Required. General status of the priority request [0..7]:
 *                   unknown (0), requested (1), processing (2), watchOtherTraffic (3),
 *                   granted (4), rejected (5), maxPresence (6), reserviceLocked (7).
 * @param requester  Optional. The party that made the initial SREM request.
 * @param outboundOn Optional. The outbound lane or approach.
 * @param minute     Optional. ETA — minute of the year [0..527040].
 * @param second     Optional. ETA — milliseconds within the minute [0..60999].
 * @param duration   Optional. ETA duration extension [0..60999].
 */
public record SignalStatusPackage(
        IntersectionAccessPoint inboundOn,
        int status,
        SignalRequester requester,
        IntersectionAccessPoint outboundOn,
        Integer minute,
        Integer second,
        Integer duration) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private IntersectionAccessPoint inboundOn;
        private Integer status;
        private SignalRequester requester;
        private IntersectionAccessPoint outboundOn;
        private Integer minute;
        private Integer second;
        private Integer duration;

        private Builder() {}

        public Builder inboundOn(IntersectionAccessPoint inboundOn) { this.inboundOn = inboundOn; return this; }
        public Builder status(int status) { this.status = status; return this; }
        public Builder requester(SignalRequester requester) { this.requester = requester; return this; }
        public Builder outboundOn(IntersectionAccessPoint outboundOn) { this.outboundOn = outboundOn; return this; }
        public Builder minute(Integer minute) { this.minute = minute; return this; }
        public Builder second(Integer second) { this.second = second; return this; }
        public Builder duration(Integer duration) { this.duration = duration; return this; }

        public SignalStatusPackage build() {
            return new SignalStatusPackage(
                    requireNonNull(inboundOn, "inboundOn"),
                    requireNonNull(status, "status"),
                    requester, outboundOn, minute, second, duration);
        }

        private static <T> T requireNonNull(T value, String field) {
            if (value == null) throw new IllegalStateException("Missing field: " + field);
            return value;
        }
    }
}

