/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 4.6)
 */
package com.orange.iot3mobility.messages.srem.v201.validation;

import com.orange.iot3mobility.messages.srem.v201.model.SremEnvelope201;
import com.orange.iot3mobility.messages.srem.v201.model.SremMessage201;
import com.orange.iot3mobility.messages.srem.v201.model.request.RequestorDescription;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SremValidator201Test {

    @Test
    void validateEnvelope_withValidEnvelope_shouldPass() {
        assertDoesNotThrow(() -> SremValidator201.validateEnvelope(validEnvelope()));
    }

    @Test
    void validateEnvelope_withNullEnvelope_shouldThrow() {
        assertThrows(SremValidationException.class, () -> SremValidator201.validateEnvelope(null));
    }

    @Test
    void validateEnvelope_withBlankSourceUuid_shouldThrow() {
        SremEnvelope201 envelope = new SremEnvelope201("srem", "  ", 1514764800000L, "2.0.1", validMessage());
        assertThrows(SremValidationException.class, () -> SremValidator201.validateEnvelope(envelope));
    }

    @Test
    void validateEnvelope_withTimestampTooLow_shouldThrow() {
        SremEnvelope201 envelope = new SremEnvelope201("srem", "uuid", 0L, "2.0.1", validMessage());
        assertThrows(SremValidationException.class, () -> SremValidator201.validateEnvelope(envelope));
    }

    @Test
    void validateEnvelope_withTimestampTooHigh_shouldThrow() {
        SremEnvelope201 envelope = new SremEnvelope201("srem", "uuid", 9999999999999L, "2.0.1", validMessage());
        assertThrows(SremValidationException.class, () -> SremValidator201.validateEnvelope(envelope));
    }

    @Test
    void validateEnvelope_withWrongMessageType_shouldThrow() {
        SremEnvelope201 envelope = new SremEnvelope201("spatem", "uuid", 1514764800000L, "2.0.1", validMessage());
        assertThrows(SremValidationException.class, () -> SremValidator201.validateEnvelope(envelope));
    }

    @Test
    void validateEnvelope_withWrongVersion_shouldThrow() {
        SremEnvelope201 envelope = new SremEnvelope201("srem", "uuid", 1514764800000L, "1.0.0", validMessage());
        assertThrows(SremValidationException.class, () -> SremValidator201.validateEnvelope(envelope));
    }

    @Test
    void validateEnvelope_withSecondOutOfRange_shouldThrow() {
        SremMessage201 msg = new SremMessage201(1, 42L, 99999, validRequestor(), null, null, null);
        SremEnvelope201 envelope = new SremEnvelope201("srem", "uuid", 1514764800000L, "2.0.1", msg);
        assertThrows(SremValidationException.class, () -> SremValidator201.validateEnvelope(envelope));
    }

    @Test
    void validateEnvelope_withNullRequestor_shouldThrow() {
        SremMessage201 msg = new SremMessage201(1, 42L, 1000, null, null, null, null);
        SremEnvelope201 envelope = new SremEnvelope201("srem", "uuid", 1514764800000L, "2.0.1", msg);
        assertThrows(SremValidationException.class, () -> SremValidator201.validateEnvelope(envelope));
    }

    @Test
    void validateEnvelope_withSequenceNumberOutOfRange_shouldThrow() {
        SremMessage201 msg = new SremMessage201(1, 42L, 1000, validRequestor(), null, 200, null);
        SremEnvelope201 envelope = new SremEnvelope201("srem", "uuid", 1514764800000L, "2.0.1", msg);
        assertThrows(SremValidationException.class, () -> SremValidator201.validateEnvelope(envelope));
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static SremEnvelope201 validEnvelope() {
        return new SremEnvelope201("srem", "test-uuid", 1514764800000L, "2.0.1", validMessage());
    }

    private static SremMessage201 validMessage() {
        return new SremMessage201(1, 42L, 30000, validRequestor(), null, null, null);
    }

    private static RequestorDescription validRequestor() {
        return RequestorDescription.builder().id(999L).build();
    }
}

