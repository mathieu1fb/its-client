/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 4.6)
 */
package com.orange.iot3mobility.messages.srem.v201.model.request;

/**
 * Vehicle type and class data for a requestor.
 *
 * @param role     Optional. Basic role [0..23].
 * @param subrole  Optional. Sub-role value [0..15].
 * @param request  Optional. Request classification [0..15].
 * @param iso3883  Optional. ISO 3883 vehicle category [0..255].
 * @param hpmsType Optional. HPMS vehicle type [0..15].
 */
public record RequestorType(Integer role, Integer subrole, Integer request,
                            Integer iso3883, Integer hpmsType) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Integer role;
        private Integer subrole;
        private Integer request;
        private Integer iso3883;
        private Integer hpmsType;

        private Builder() {}

        public Builder role(Integer role) { this.role = role; return this; }
        public Builder subrole(Integer subrole) { this.subrole = subrole; return this; }
        public Builder request(Integer request) { this.request = request; return this; }
        public Builder iso3883(Integer iso3883) { this.iso3883 = iso3883; return this; }
        public Builder hpmsType(Integer hpmsType) { this.hpmsType = hpmsType; return this; }

        public RequestorType build() {
            return new RequestorType(role, subrole, request, iso3883, hpmsType);
        }
    }
}

