/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 4.6)
 */
package com.orange.iot3mobility.messages.ssem.v201.validation;

import com.orange.iot3mobility.messages.ssem.v201.model.SsemEnvelope201;
import com.orange.iot3mobility.messages.ssem.v201.model.SsemMessage201;
import com.orange.iot3mobility.messages.ssem.v201.model.status.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SsemValidator201Test {

    @Test
    void validateEnvelope_withValidEnvelope_shouldPass() {
        assertDoesNotThrow(() -> SsemValidator201.validateEnvelope(validEnvelope()));
    }

    @Test
    void validateEnvelope_withNullEnvelope_shouldThrow() {
        assertThrows(SsemValidationException.class, () -> SsemValidator201.validateEnvelope(null));
    }

    @Test
    void validateEnvelope_withBlankSourceUuid_shouldThrow() {
        SsemEnvelope201 envelope = new SsemEnvelope201("ssem", "", 1514764800000L, "2.0.1", validMessage());
        assertThrows(SsemValidationException.class, () -> SsemValidator201.validateEnvelope(envelope));
    }

    @Test
    void validateEnvelope_withTimestampTooLow_shouldThrow() {
        SsemEnvelope201 envelope = new SsemEnvelope201("ssem", "uuid", 0L, "2.0.1", validMessage());
        assertThrows(SsemValidationException.class, () -> SsemValidator201.validateEnvelope(envelope));
    }

    @Test
    void validateEnvelope_withTimestampTooHigh_shouldThrow() {
        SsemEnvelope201 envelope = new SsemEnvelope201("ssem", "uuid", 9999999999999L, "2.0.1", validMessage());
        assertThrows(SsemValidationException.class, () -> SsemValidator201.validateEnvelope(envelope));
    }

    @Test
    void validateEnvelope_withWrongMessageType_shouldThrow() {
        SsemEnvelope201 envelope = new SsemEnvelope201("srem", "uuid", 1514764800000L, "2.0.1", validMessage());
        assertThrows(SsemValidationException.class, () -> SsemValidator201.validateEnvelope(envelope));
    }

    @Test
    void validateEnvelope_withWrongVersion_shouldThrow() {
        SsemEnvelope201 envelope = new SsemEnvelope201("ssem", "uuid", 1514764800000L, "1.0.0", validMessage());
        assertThrows(SsemValidationException.class, () -> SsemValidator201.validateEnvelope(envelope));
    }

    @Test
    void validateEnvelope_withEmptyStatusList_shouldThrow() {
        SsemMessage201 msg = new SsemMessage201(1, 42L, 1000, List.of(), null, null);
        SsemEnvelope201 envelope = new SsemEnvelope201("ssem", "uuid", 1514764800000L, "2.0.1", msg);
        assertThrows(SsemValidationException.class, () -> SsemValidator201.validateEnvelope(envelope));
    }

    @Test
    void validateEnvelope_withNullStatusList_shouldThrow() {
        SsemMessage201 msg = new SsemMessage201(1, 42L, 1000, null, null, null);
        SsemEnvelope201 envelope = new SsemEnvelope201("ssem", "uuid", 1514764800000L, "2.0.1", msg);
        assertThrows(SsemValidationException.class, () -> SsemValidator201.validateEnvelope(envelope));
    }

    @Test
    void validateEnvelope_withSecondOutOfRange_shouldThrow() {
        SsemMessage201 msg = new SsemMessage201(1, 42L, 99999, List.of(validSignalStatus()), null, null);
        SsemEnvelope201 envelope = new SsemEnvelope201("ssem", "uuid", 1514764800000L, "2.0.1", msg);
        assertThrows(SsemValidationException.class, () -> SsemValidator201.validateEnvelope(envelope));
    }

    @Test
    void validateEnvelope_withSequenceNumberOutOfRange_shouldThrow() {
        SsemMessage201 msg = new SsemMessage201(1, 42L, 1000, List.of(validSignalStatus()), null, 200);
        SsemEnvelope201 envelope = new SsemEnvelope201("ssem", "uuid", 1514764800000L, "2.0.1", msg);
        assertThrows(SsemValidationException.class, () -> SsemValidator201.validateEnvelope(envelope));
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static SsemEnvelope201 validEnvelope() {
        return new SsemEnvelope201("ssem", "test-uuid", 1514764800000L, "2.0.1", validMessage());
    }

    private static SsemMessage201 validMessage() {
        return new SsemMessage201(1, 42L, 30000, List.of(validSignalStatus()), null, null);
    }

    private static SignalStatus validSignalStatus() {
        IntersectionAccessPoint inbound = IntersectionAccessPoint.builder().lane(1).build();
        SignalStatusPackage pkg = SignalStatusPackage.builder()
                .inboundOn(inbound)
                .status(1)
                .build();
        return SignalStatus.builder()
                .sequenceNumber(0)
                .id(new IntersectionReferenceId(null, 1001))
                .sigStatus(List.of(pkg))
                .build();
    }
}

