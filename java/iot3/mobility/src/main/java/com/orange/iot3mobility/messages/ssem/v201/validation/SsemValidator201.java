/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 4.6)
 */
package com.orange.iot3mobility.messages.ssem.v201.validation;

import com.orange.iot3mobility.messages.ssem.v201.model.SsemEnvelope201;
import com.orange.iot3mobility.messages.ssem.v201.model.SsemMessage201;

/**
 * Validates mandatory structural constraints of a SSEM v2.0.1 envelope.
 */
public final class SsemValidator201 {

    private SsemValidator201() {}

    /**
     * Validates the top-level envelope and its message payload.
     *
     * @param envelope the envelope to validate
     * @throws SsemValidationException if any constraint is violated
     */
    public static void validateEnvelope(SsemEnvelope201 envelope) {
        if (envelope == null) throw new SsemValidationException("SSEM envelope must not be null");
        if (!"ssem".equals(envelope.messageType())) {
            throw new SsemValidationException("message_type must be 'ssem', got: " + envelope.messageType());
        }
        if (!"2.0.1".equals(envelope.version())) {
            throw new SsemValidationException("version must be '2.0.1', got: " + envelope.version());
        }
        if (envelope.sourceUuid() == null || envelope.sourceUuid().isBlank()) {
            throw new SsemValidationException("source_uuid must not be null or blank");
        }
        if (envelope.timestamp() < 1514764800000L || envelope.timestamp() > 1830297600000L) {
            throw new SsemValidationException("timestamp out of range [1514764800000..1830297600000]: "
                    + envelope.timestamp());
        }
        validateMessage(envelope.message());
    }

    private static void validateMessage(SsemMessage201 msg) {
        if (msg == null) throw new SsemValidationException("SSEM message must not be null");
        if (msg.protocolVersion() < 0 || msg.protocolVersion() > 255) {
            throw new SsemValidationException("protocol_version out of range [0..255]: " + msg.protocolVersion());
        }
        if (msg.stationId() < 0 || msg.stationId() > 4294967295L) {
            throw new SsemValidationException("station_id out of range [0..4294967295]: " + msg.stationId());
        }
        if (msg.second() < 0 || msg.second() > 60999) {
            throw new SsemValidationException("second out of range [0..60999]: " + msg.second());
        }
        if (msg.status() == null || msg.status().isEmpty()) {
            throw new SsemValidationException("SSEM message must contain at least one status entry");
        }
        if (msg.status().size() > 32) {
            throw new SsemValidationException("status count exceeds maximum of 32: " + msg.status().size());
        }
        if (msg.sequenceNumber() != null && (msg.sequenceNumber() < 0 || msg.sequenceNumber() > 127)) {
            throw new SsemValidationException("sequence_number out of range [0..127]: " + msg.sequenceNumber());
        }
        if (msg.timestamp() != null && (msg.timestamp() < 0 || msg.timestamp() > 527040)) {
            throw new SsemValidationException("timestamp out of range [0..527040]: " + msg.timestamp());
        }
    }
}

