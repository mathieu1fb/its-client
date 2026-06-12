/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 4.6)
 */
package com.orange.iot3mobility.messages.srem;

import com.fasterxml.jackson.core.JsonFactory;
import com.orange.iot3mobility.messages.srem.core.SremCodec;
import com.orange.iot3mobility.messages.srem.core.SremException;
import com.orange.iot3mobility.messages.srem.core.SremVersion;
import com.orange.iot3mobility.messages.srem.v201.model.SremEnvelope201;
import com.orange.iot3mobility.messages.srem.v201.model.SremMessage201;
import com.orange.iot3mobility.messages.srem.v201.model.request.*;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;

class SremCodecTest {

    @Test
    void writeReadV201_roundTrip_shouldPreserveEnvelopeFields() throws Exception {
        SremEnvelope201 envelope = validEnvelope201();
        SremCodec codec = new SremCodec(new JsonFactory());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        codec.write(SremVersion.V2_0_1, envelope, out);

        SremCodec.SremFrame<?> frame = codec.read(new ByteArrayInputStream(out.toByteArray()));
        assertEquals(SremVersion.V2_0_1, frame.version());
        assertTrue(frame.envelope() instanceof SremEnvelope201);
        SremEnvelope201 parsed = (SremEnvelope201) frame.envelope();
        assertEquals(envelope.sourceUuid(), parsed.sourceUuid());
        assertEquals(envelope.timestamp(), parsed.timestamp());
        assertEquals(envelope.message().stationId(), parsed.message().stationId());
        assertEquals(envelope.message().requestor().id(), parsed.message().requestor().id());
    }

    @Test
    void readString_V201_roundTrip_shouldPreserveSourceUuid() throws Exception {
        SremEnvelope201 envelope = validEnvelope201();
        SremCodec codec = new SremCodec(new JsonFactory());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        codec.write(SremVersion.V2_0_1, envelope, out);

        SremCodec.SremFrame<?> frame = codec.read(out.toString());
        assertEquals(SremVersion.V2_0_1, frame.version());
        SremEnvelope201 parsed = (SremEnvelope201) frame.envelope();
        assertEquals(envelope.sourceUuid(), parsed.sourceUuid());
    }

    @Test
    void read_missingVersion_shouldThrowSremException() {
        SremCodec codec = new SremCodec(new JsonFactory());
        String json = "{\"message_type\":\"srem\"}";
        assertThrows(SremException.class, () -> codec.read(json));
    }

    @Test
    void read_unsupportedVersion_shouldThrowSremException() {
        SremCodec codec = new SremCodec(new JsonFactory());
        String json = "{\"version\":\"9.9.9\"}";
        assertThrows(SremException.class, () -> codec.read(json));
    }

    @Test
    void write_withRequestPackage_shouldRoundTripRequestId() throws Exception {
        IntersectionAccessPoint lane = IntersectionAccessPoint.builder().lane(3).build();
        SignalRequest request = SignalRequest.builder()
                .id(new IntersectionReferenceId(null, 42))
                .requestId(7)
                .requestType(1)
                .inboundLane(lane)
                .build();
        SignalRequestPackage pkg = SignalRequestPackage.builder()
                .request(request)
                .minute(500)
                .second(30000)
                .build();

        RequestorDescription requestor = RequestorDescription.builder()
                .id(99L)
                .build();

        SremMessage201 message = SremMessage201.builder()
                .protocolVersion(1)
                .stationId(1001L)
                .second(45000)
                .requestor(requestor)
                .requests(java.util.List.of(pkg))
                .build();

        SremEnvelope201 envelope = SremEnvelope201.builder()
                .sourceUuid("test-vehicle-uuid")
                .timestamp(1514764800001L)
                .message(message)
                .build();

        SremCodec codec = new SremCodec(new JsonFactory());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        codec.write(SremVersion.V2_0_1, envelope, out);

        SremCodec.SremFrame<?> frame = codec.read(out.toString());
        SremEnvelope201 parsed = (SremEnvelope201) frame.envelope();
        assertNotNull(parsed.message().requests());
        assertEquals(1, parsed.message().requests().size());
        assertEquals(7, parsed.message().requests().get(0).request().requestId());
        assertEquals(500, parsed.message().requests().get(0).minute());
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static SremEnvelope201 validEnvelope201() {
        RequestorDescription requestor = RequestorDescription.builder()
                .id(12345L)
                .build();

        SremMessage201 message = SremMessage201.builder()
                .protocolVersion(1)
                .stationId(42L)
                .second(30000)
                .requestor(requestor)
                .build();

        return SremEnvelope201.builder()
                .sourceUuid("test-vehicle-001")
                .timestamp(1514764800000L)
                .message(message)
                .build();
    }
}

