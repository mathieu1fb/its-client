/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 4.6)
 */
package com.orange.iot3mobility.messages.srem.v201.validation;

import com.orange.iot3mobility.messages.srem.v201.model.SremEnvelope201;
import com.orange.iot3mobility.messages.srem.v201.model.SremMessage201;

/**
 * Validates mandatory structural constraints of a SREM v2.0.1 envelope.
 */
public final class SremValidator201 {

    private SremValidator201() {}

    /**
     * Validates the top-level envelope and its message payload.
     *
     * @param envelope the envelope to validate
     * @throws SremValidationException if any constraint is violated
     */
    public static void validateEnvelope(SremEnvelope201 envelope) {
        if (envelope == null) throw new SremValidationException("SREM envelope must not be null");
        if (!"srem".equals(envelope.messageType())) {
            throw new SremValidationException("message_type must be 'srem', got: " + envelope.messageType());
        }
        if (!"2.0.1".equals(envelope.version())) {
            throw new SremValidationException("version must be '2.0.1', got: " + envelope.version());
        }
        if (envelope.sourceUuid() == null || envelope.sourceUuid().isBlank()) {
            throw new SremValidationException("source_uuid must not be null or blank");
        }
        if (envelope.timestamp() < 1514764800000L || envelope.timestamp() > 1830297600000L) {
            throw new SremValidationException("timestamp out of range [1514764800000..1830297600000]: "
                    + envelope.timestamp());
        }
        validateMessage(envelope.message());
    }

    private static void validateMessage(SremMessage201 msg) {
        if (msg == null) throw new SremValidationException("SREM message must not be null");
        if (msg.protocolVersion() < 0 || msg.protocolVersion() > 255) {
            throw new SremValidationException("protocol_version out of range [0..255]: " + msg.protocolVersion());
        }
        if (msg.stationId() < 0 || msg.stationId() > 4294967295L) {
            throw new SremValidationException("station_id out of range [0..4294967295]: " + msg.stationId());
        }
        if (msg.second() < 0 || msg.second() > 60999) {
            throw new SremValidationException("second out of range [0..60999]: " + msg.second());
        }
        if (msg.requestor() == null) {
            throw new SremValidationException("requestor must not be null");
        }
        if (msg.sequenceNumber() != null && (msg.sequenceNumber() < 0 || msg.sequenceNumber() > 127)) {
            throw new SremValidationException("sequence_number out of range [0..127]: " + msg.sequenceNumber());
        }
        if (msg.timestamp() != null && (msg.timestamp() < 0 || msg.timestamp() > 527040)) {
            throw new SremValidationException("timestamp out of range [0..527040]: " + msg.timestamp());
        }
        if (msg.requests() != null && (msg.requests().size() < 1 || msg.requests().size() > 32)) {
            throw new SremValidationException("requests count out of range [1..32]: " + msg.requests().size());
        }
    }
}

