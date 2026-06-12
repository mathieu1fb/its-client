/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 4.6)
 */
package com.orange.iot3mobility.messages.srem.v201.model.request;

/**
 * Position of the requesting vehicle.
 *
 * @param position Required. Position in DSRC Position3D format.
 * @param heading  Optional. Heading in 0.0125 degree units [0..28800].
 * @param speed    Optional. Speed in 0.02 m/s units [0..8191].
 */
public record RequestorPositionVector(Position3D position, Integer heading, Integer speed) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Position3D position;
        private Integer heading;
        private Integer speed;

        private Builder() {}

        public Builder position(Position3D position) { this.position = position; return this; }
        public Builder heading(Integer heading) { this.heading = heading; return this; }
        public Builder speed(Integer speed) { this.speed = speed; return this; }

        public RequestorPositionVector build() {
            return new RequestorPositionVector(
                    requireNonNull(position, "position"),
                    heading,
                    speed);
        }

        private static <T> T requireNonNull(T value, String field) {
            if (value == null) throw new IllegalStateException("Missing field: " + field);
            return value;
        }
    }
}

