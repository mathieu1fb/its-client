/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 4.6)
 */
package com.orange.iot3mobility.messages.ssem.v201.model;

import com.orange.iot3mobility.messages.ssem.v201.model.status.SignalStatus;

import java.util.List;

/**
 * SSEM payload — Signal Status Extended Message.
 *
 * @param protocolVersion Required. Message and protocol version [0..255].
 * @param stationId       Required. Originating station ID [0..4294967295].
 * @param second          Required. Milliseconds within the current minute [0..60999].
 * @param status          Required. List of signal status entries [1..32].
 * @param timestamp       Optional. Minute of the current UTC year [0..527040].
 * @param sequenceNumber  Optional. Sequence number [0..127].
 */
public record SsemMessage201(
        int protocolVersion,
        long stationId,
        int second,
        List<SignalStatus> status,
        Integer timestamp,
        Integer sequenceNumber) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Integer protocolVersion;
        private Long stationId;
        private Integer second;
        private List<SignalStatus> status;
        private Integer timestamp;
        private Integer sequenceNumber;

        private Builder() {}

        public Builder protocolVersion(int protocolVersion) { this.protocolVersion = protocolVersion; return this; }
        public Builder stationId(long stationId) { this.stationId = stationId; return this; }
        public Builder second(int second) { this.second = second; return this; }
        public Builder status(List<SignalStatus> status) { this.status = status; return this; }
        public Builder timestamp(Integer timestamp) { this.timestamp = timestamp; return this; }
        public Builder sequenceNumber(Integer sequenceNumber) { this.sequenceNumber = sequenceNumber; return this; }

        public SsemMessage201 build() {
            return new SsemMessage201(
                    requireNonNull(protocolVersion, "protocolVersion"),
                    requireNonNull(stationId, "stationId"),
                    requireNonNull(second, "second"),
                    requireNonNull(status, "status"),
                    timestamp, sequenceNumber);
        }

        private static <T> T requireNonNull(T value, String field) {
            if (value == null) throw new IllegalStateException("Missing field: " + field);
            return value;
        }
    }
}

