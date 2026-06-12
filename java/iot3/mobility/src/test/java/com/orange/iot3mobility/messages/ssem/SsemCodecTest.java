/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 4.6)
 */
package com.orange.iot3mobility.messages.ssem;

import com.fasterxml.jackson.core.JsonFactory;
import com.orange.iot3mobility.messages.ssem.core.SsemCodec;
import com.orange.iot3mobility.messages.ssem.core.SsemException;
import com.orange.iot3mobility.messages.ssem.core.SsemVersion;
import com.orange.iot3mobility.messages.ssem.v201.model.SsemEnvelope201;
import com.orange.iot3mobility.messages.ssem.v201.model.SsemMessage201;
import com.orange.iot3mobility.messages.ssem.v201.model.status.*;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SsemCodecTest {

    @Test
    void writeReadV201_roundTrip_shouldPreserveEnvelopeFields() throws Exception {
        SsemEnvelope201 envelope = validEnvelope201();
        SsemCodec codec = new SsemCodec(new JsonFactory());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        codec.write(SsemVersion.V2_0_1, envelope, out);

        SsemCodec.SsemFrame<?> frame = codec.read(new ByteArrayInputStream(out.toByteArray()));
        assertEquals(SsemVersion.V2_0_1, frame.version());
        assertTrue(frame.envelope() instanceof SsemEnvelope201);
        SsemEnvelope201 parsed = (SsemEnvelope201) frame.envelope();
        assertEquals(envelope.sourceUuid(), parsed.sourceUuid());
        assertEquals(envelope.timestamp(), parsed.timestamp());
        assertEquals(envelope.message().stationId(), parsed.message().stationId());
    }

    @Test
    void readString_V201_roundTrip_shouldPreserveSourceUuid() throws Exception {
        SsemEnvelope201 envelope = validEnvelope201();
        SsemCodec codec = new SsemCodec(new JsonFactory());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        codec.write(SsemVersion.V2_0_1, envelope, out);

        SsemCodec.SsemFrame<?> frame = codec.read(out.toString());
        assertEquals(SsemVersion.V2_0_1, frame.version());
        SsemEnvelope201 parsed = (SsemEnvelope201) frame.envelope();
        assertEquals(envelope.sourceUuid(), parsed.sourceUuid());
    }

    @Test
    void read_missingVersion_shouldThrowSsemException() {
        SsemCodec codec = new SsemCodec(new JsonFactory());
        String json = "{\"message_type\":\"ssem\"}";
        assertThrows(SsemException.class, () -> codec.read(json));
    }

    @Test
    void read_unsupportedVersion_shouldThrowSsemException() {
        SsemCodec codec = new SsemCodec(new JsonFactory());
        String json = "{\"version\":\"9.9.9\"}";
        assertThrows(SsemException.class, () -> codec.read(json));
    }

    @Test
    void write_withRequester_shouldRoundTripRequesterFields() throws Exception {
        SignalRequester requester = SignalRequester.builder()
                .id(7654321L)
                .request(3)
                .sequenceNumber(99)
                .role(6)
                .build();

        IntersectionAccessPoint inbound = IntersectionAccessPoint.builder().lane(2).build();
        SignalStatusPackage pkg = SignalStatusPackage.builder()
                .inboundOn(inbound)
                .status(4)
                .requester(requester)
                .build();

        SignalStatus signalStatus = SignalStatus.builder()
                .sequenceNumber(1)
                .id(new IntersectionReferenceId(10, 200))
                .sigStatus(List.of(pkg))
                .build();

        SsemMessage201 message = SsemMessage201.builder()
                .protocolVersion(1)
                .stationId(88L)
                .second(15000)
                .status(List.of(signalStatus))
                .build();

        SsemEnvelope201 envelope = SsemEnvelope201.builder()
                .sourceUuid("test-rsu-uuid")
                .timestamp(1514764800001L)
                .message(message)
                .build();

        SsemCodec codec = new SsemCodec(new JsonFactory());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        codec.write(SsemVersion.V2_0_1, envelope, out);

        SsemCodec.SsemFrame<?> frame = codec.read(out.toString());
        SsemEnvelope201 parsed = (SsemEnvelope201) frame.envelope();
        SignalRequester parsedRequester = parsed.message().status().get(0).sigStatus().get(0).requester();
        assertNotNull(parsedRequester);
        assertEquals(7654321L, parsedRequester.id());
        assertEquals(4, parsed.message().status().get(0).sigStatus().get(0).status());
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static SsemEnvelope201 validEnvelope201() {
        IntersectionAccessPoint inbound = IntersectionAccessPoint.builder().lane(1).build();
        SignalStatusPackage pkg = SignalStatusPackage.builder()
                .inboundOn(inbound)
                .status(1)
                .build();

        SignalStatus signalStatus = SignalStatus.builder()
                .sequenceNumber(0)
                .id(new IntersectionReferenceId(null, 1001))
                .sigStatus(List.of(pkg))
                .build();

        SsemMessage201 message = SsemMessage201.builder()
                .protocolVersion(1)
                .stationId(42L)
                .second(30000)
                .status(List.of(signalStatus))
                .build();

        return SsemEnvelope201.builder()
                .sourceUuid("test-rsu-001")
                .timestamp(1514764800000L)
                .message(message)
                .build();
    }
}

