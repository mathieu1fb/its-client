/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 */
package com.orange.iot3mobility.messages.mapem.v200.model.shared;

import java.util.List;

/**
 * Binds a restriction class ID to the list of user types it applies to.
 *
 * @param id Intersection-unique restriction class identifier. Range: 0..255.
 * @param users List of user type strings (e.g. "pedestrians", "equippedTransit"). Min 1, max 16.
 */
public record RestrictionClassAssignment(int id, List<String> users) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Integer id;
        private List<String> users;

        private Builder() {}

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder users(List<String> users) {
            this.users = users;
            return this;
        }

        public RestrictionClassAssignment build() {
            return new RestrictionClassAssignment(
                    requireNonNull(id, "id"),
                    requireNonNull(users, "users"));
        }

        private static <T> T requireNonNull(T value, String field) {
            if (value == null) throw new IllegalStateException("Missing field: " + field);
            return value;
        }
    }
}

