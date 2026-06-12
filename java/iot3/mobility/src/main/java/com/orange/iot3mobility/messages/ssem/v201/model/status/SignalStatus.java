/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 4.6)
 */
package com.orange.iot3mobility.messages.ssem.v201.model.status;

import java.util.List;

/**
 * Status of all active priority or preemption requests for a single intersection.
 *
 * @param sequenceNumber Required. Updated whenever the contents change [0..127].
 * @param id             Required. Intersection identifier.
 * @param sigStatus      Required. List of per-request status packages [1..32].
 */
public record SignalStatus(
        int sequenceNumber,
        IntersectionReferenceId id,
        List<SignalStatusPackage> sigStatus) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Integer sequenceNumber;
        private IntersectionReferenceId id;
        private List<SignalStatusPackage> sigStatus;

        private Builder() {}

        public Builder sequenceNumber(int sequenceNumber) { this.sequenceNumber = sequenceNumber; return this; }
        public Builder id(IntersectionReferenceId id) { this.id = id; return this; }
        public Builder sigStatus(List<SignalStatusPackage> sigStatus) { this.sigStatus = sigStatus; return this; }

        public SignalStatus build() {
            return new SignalStatus(
                    requireNonNull(sequenceNumber, "sequenceNumber"),
                    requireNonNull(id, "id"),
                    requireNonNull(sigStatus, "sigStatus"));
        }

        private static <T> T requireNonNull(T value, String field) {
            if (value == null) throw new IllegalStateException("Missing field: " + field);
            return value;
        }
    }
}

