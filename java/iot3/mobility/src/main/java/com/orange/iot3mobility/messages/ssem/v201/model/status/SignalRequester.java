/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 4.6)
 */
package com.orange.iot3mobility.messages.ssem.v201.model.status;

/**
 * Identifies the party that made the initial SREM request.
 *
 * @param id             Required. Station ID of the requestor [0..4294967295].
 * @param request        Required. Request ID [0..255].
 * @param sequenceNumber Required. Sequence number [0..127].
 * @param role           Optional. Basic role [0..23].
 * @param typeData       Optional. Additional type data when role alone is insufficient.
 */
public record SignalRequester(
        long id,
        int request,
        int sequenceNumber,
        Integer role,
        Integer typeData) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private Integer request;
        private Integer sequenceNumber;
        private Integer role;
        private Integer typeData;

        private Builder() {}

        public Builder id(long id) { this.id = id; return this; }
        public Builder request(int request) { this.request = request; return this; }
        public Builder sequenceNumber(int sequenceNumber) { this.sequenceNumber = sequenceNumber; return this; }
        public Builder role(Integer role) { this.role = role; return this; }
        public Builder typeData(Integer typeData) { this.typeData = typeData; return this; }

        public SignalRequester build() {
            return new SignalRequester(
                    requireNonNull(id, "id"),
                    requireNonNull(request, "request"),
                    requireNonNull(sequenceNumber, "sequenceNumber"),
                    role, typeData);
        }

        private static <T> T requireNonNull(T value, String field) {
            if (value == null) throw new IllegalStateException("Missing field: " + field);
            return value;
        }
    }
}

