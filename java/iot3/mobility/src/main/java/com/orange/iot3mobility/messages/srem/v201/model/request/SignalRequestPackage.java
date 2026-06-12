/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 4.6)
 */
package com.orange.iot3mobility.messages.srem.v201.model.request;

/**
 * A single signal request entry with optional timing information.
 *
 * @param request  Required. The signal priority or preemption request.
 * @param minute   Optional. ETA — minute of the year [0..527040].
 * @param second   Optional. ETA — milliseconds within the minute [0..60999].
 * @param duration Optional. Duration extending the ETA window [0..60999].
 */
public record SignalRequestPackage(
        SignalRequest request,
        Integer minute,
        Integer second,
        Integer duration) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private SignalRequest request;
        private Integer minute;
        private Integer second;
        private Integer duration;

        private Builder() {}

        public Builder request(SignalRequest request) { this.request = request; return this; }
        public Builder minute(Integer minute) { this.minute = minute; return this; }
        public Builder second(Integer second) { this.second = second; return this; }
        public Builder duration(Integer duration) { this.duration = duration; return this; }

        public SignalRequestPackage build() {
            return new SignalRequestPackage(
                    requireNonNull(request, "request"),
                    minute, second, duration);
        }

        private static <T> T requireNonNull(T value, String field) {
            if (value == null) throw new IllegalStateException("Missing field: " + field);
            return value;
        }
    }
}

