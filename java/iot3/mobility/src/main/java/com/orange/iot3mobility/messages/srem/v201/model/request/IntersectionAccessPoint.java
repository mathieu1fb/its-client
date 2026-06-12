/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 4.6)
 */
package com.orange.iot3mobility.messages.srem.v201.model.request;

/**
 * Identifies a specific access point at an intersection — exactly one of the three fields
 * is expected to be set (lane, approach, or connection).
 *
 * @param lane       Optional. Lane ID [0..255].
 * @param approach   Optional. Approach ID [0..255].
 * @param connection Optional. Connection ID [0..255].
 */
public record IntersectionAccessPoint(Integer lane, Integer approach, Integer connection) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Integer lane;
        private Integer approach;
        private Integer connection;

        private Builder() {}

        public Builder lane(Integer lane) { this.lane = lane; return this; }
        public Builder approach(Integer approach) { this.approach = approach; return this; }
        public Builder connection(Integer connection) { this.connection = connection; return this; }

        public IntersectionAccessPoint build() {
            return new IntersectionAccessPoint(lane, approach, connection);
        }
    }
}

