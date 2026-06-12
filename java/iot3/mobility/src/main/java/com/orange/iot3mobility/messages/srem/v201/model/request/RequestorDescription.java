/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 4.6)
 */
package com.orange.iot3mobility.messages.srem.v201.model.request;

/**
 * Requesting device and other user data.
 *
 * @param id               Required. Station ID used in the CAM of the requestor [0..4294967295].
 * @param type             Optional. Vehicle type and class data.
 * @param position         Optional. Current location of the requesting vehicle.
 * @param name             Optional. Human-readable name for debugging.
 * @param routeName        Optional. Transit route name.
 * @param transitStatus    Optional. Basic transit run status [0..5].
 * @param transitOccupancy Optional. Ridership level [0..7].
 * @param transitSchedule  Optional. Schedule adherence in 10-second units [−122..121].
 */
public record RequestorDescription(
        long id,
        RequestorType type,
        RequestorPositionVector position,
        String name,
        String routeName,
        Integer transitStatus,
        Integer transitOccupancy,
        Integer transitSchedule) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private RequestorType type;
        private RequestorPositionVector position;
        private String name;
        private String routeName;
        private Integer transitStatus;
        private Integer transitOccupancy;
        private Integer transitSchedule;

        private Builder() {}

        public Builder id(long id) { this.id = id; return this; }
        public Builder type(RequestorType type) { this.type = type; return this; }
        public Builder position(RequestorPositionVector position) { this.position = position; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder routeName(String routeName) { this.routeName = routeName; return this; }
        public Builder transitStatus(Integer transitStatus) { this.transitStatus = transitStatus; return this; }
        public Builder transitOccupancy(Integer transitOccupancy) { this.transitOccupancy = transitOccupancy; return this; }
        public Builder transitSchedule(Integer transitSchedule) { this.transitSchedule = transitSchedule; return this; }

        public RequestorDescription build() {
            return new RequestorDescription(
                    requireNonNull(id, "id"),
                    type, position, name, routeName,
                    transitStatus, transitOccupancy, transitSchedule);
        }

        private static <T> T requireNonNull(T value, String field) {
            if (value == null) throw new IllegalStateException("Missing field: " + field);
            return value;
        }
    }
}

