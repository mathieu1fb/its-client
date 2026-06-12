/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 4.6)
 */
package com.orange.iot3mobility.messages.srem.v201.model;

/**
 * Top-level SREM v2.0.1 envelope.
 *
 * @param messageType Required. Always {@code "srem"}.
 * @param sourceUuid  Required. Identifier of the emitting station.
 * @param timestamp   Required. Unix epoch ms when the message was generated [1514764800000..1830297600000].
 * @param version     Required. Always {@code "2.0.1"}.
 * @param message     Required. SREM payload.
 */
public record SremEnvelope201(
        String messageType,
        String sourceUuid,
        long timestamp,
        String version,
        SremMessage201 message) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String sourceUuid;
        private Long timestamp;
        private SremMessage201 message;

        // messageType and version are constants — hardcoded by this builder
        private static final String MESSAGE_TYPE = "srem";
        private static final String VERSION = "2.0.1";

        private Builder() {}

        public Builder sourceUuid(String sourceUuid) { this.sourceUuid = sourceUuid; return this; }
        public Builder timestamp(long timestamp) { this.timestamp = timestamp; return this; }
        public Builder message(SremMessage201 message) { this.message = message; return this; }

        public SremEnvelope201 build() {
            return new SremEnvelope201(
                    MESSAGE_TYPE,
                    requireNonNull(sourceUuid, "sourceUuid"),
                    requireNonNull(timestamp, "timestamp"),
                    VERSION,
                    requireNonNull(message, "message"));
        }

        private static <T> T requireNonNull(T value, String field) {
            if (value == null) throw new IllegalStateException("Missing field: " + field);
            return value;
        }
    }
}

