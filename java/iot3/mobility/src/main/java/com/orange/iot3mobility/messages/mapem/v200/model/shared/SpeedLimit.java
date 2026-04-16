/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 */
package com.orange.iot3mobility.messages.mapem.v200.model.shared;

/**
 * A single speed limit entry associating a speed type with its value.
 *
 * @param type Speed limit type (e.g. "maxSpeedInSchoolZone", "vehicleMaxSpeed"). See ETSI TS 103 301.
 * @param speed Speed value in units of 0.02 m/s (ETSI). Range: 0..8191.
 */
public record SpeedLimit(String type, int speed) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String type;
        private Integer speed;

        private Builder() {}

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder speed(int speed) {
            this.speed = speed;
            return this;
        }

        public SpeedLimit build() {
            return new SpeedLimit(
                    requireNonNull(type, "type"),
                    requireNonNull(speed, "speed"));
        }

        private static <T> T requireNonNull(T value, String field) {
            if (value == null) throw new IllegalStateException("Missing field: " + field);
            return value;
        }
    }
}

