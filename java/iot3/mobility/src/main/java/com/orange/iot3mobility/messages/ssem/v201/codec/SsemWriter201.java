/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 4.6)
 */
package com.orange.iot3mobility.messages.ssem.v201.codec;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.orange.iot3mobility.messages.ssem.v201.model.SsemEnvelope201;
import com.orange.iot3mobility.messages.ssem.v201.model.SsemMessage201;
import com.orange.iot3mobility.messages.ssem.v201.model.status.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Streaming JSON writer for SSEM v2.0.1 envelopes.
 */
public final class SsemWriter201 {

    private final JsonFactory jsonFactory;

    public SsemWriter201(JsonFactory jsonFactory) {
        this.jsonFactory = jsonFactory;
    }

    public void write(SsemEnvelope201 envelope, OutputStream out) throws IOException {
        try (JsonGenerator gen = jsonFactory.createGenerator(out)) {
            gen.writeStartObject();
            gen.writeStringField("message_type", envelope.messageType());
            gen.writeStringField("source_uuid", envelope.sourceUuid());
            gen.writeNumberField("timestamp", envelope.timestamp());
            gen.writeStringField("version", envelope.version());
            gen.writeFieldName("message");
            writeMessage(gen, envelope.message());
            gen.writeEndObject();
        }
    }

    /* --------------------------------------------------------------------- */
    /* Message                                                               */
    /* --------------------------------------------------------------------- */

    private void writeMessage(JsonGenerator gen, SsemMessage201 msg) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("protocol_version", msg.protocolVersion());
        gen.writeNumberField("station_id", msg.stationId());
        gen.writeNumberField("second", msg.second());
        if (msg.timestamp() != null)      gen.writeNumberField("timestamp",       msg.timestamp());
        if (msg.sequenceNumber() != null) gen.writeNumberField("sequence_number", msg.sequenceNumber());
        gen.writeFieldName("status");
        writeSignalStatusList(gen, msg.status());
        gen.writeEndObject();
    }

    /* --------------------------------------------------------------------- */
    /* SignalStatus                                                          */
    /* --------------------------------------------------------------------- */

    private void writeSignalStatusList(JsonGenerator gen, List<SignalStatus> statusList) throws IOException {
        gen.writeStartArray();
        for (SignalStatus s : statusList) writeSignalStatus(gen, s);
        gen.writeEndArray();
    }

    private void writeSignalStatus(JsonGenerator gen, SignalStatus s) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("sequence_number", s.sequenceNumber());
        gen.writeFieldName("id");
        writeIntersectionReferenceId(gen, s.id());
        gen.writeFieldName("sig_status");
        writeSignalStatusPackages(gen, s.sigStatus());
        gen.writeEndObject();
    }

    private void writeIntersectionReferenceId(JsonGenerator gen, IntersectionReferenceId id) throws IOException {
        gen.writeStartObject();
        if (id.region() != null) gen.writeNumberField("region", id.region());
        gen.writeNumberField("id", id.id());
        gen.writeEndObject();
    }

    /* --------------------------------------------------------------------- */
    /* SignalStatusPackage                                                   */
    /* --------------------------------------------------------------------- */

    private void writeSignalStatusPackages(JsonGenerator gen, List<SignalStatusPackage> packages) throws IOException {
        gen.writeStartArray();
        for (SignalStatusPackage pkg : packages) writeSignalStatusPackage(gen, pkg);
        gen.writeEndArray();
    }

    private void writeSignalStatusPackage(JsonGenerator gen, SignalStatusPackage pkg) throws IOException {
        gen.writeStartObject();
        if (pkg.requester() != null) {
            gen.writeFieldName("requester");
            writeSignalRequester(gen, pkg.requester());
        }
        gen.writeFieldName("inbound_on");
        writeIntersectionAccessPoint(gen, pkg.inboundOn());
        if (pkg.outboundOn() != null) {
            gen.writeFieldName("outbound_on");
            writeIntersectionAccessPoint(gen, pkg.outboundOn());
        }
        if (pkg.minute() != null)   gen.writeNumberField("minute",   pkg.minute());
        if (pkg.second() != null)   gen.writeNumberField("second",   pkg.second());
        if (pkg.duration() != null) gen.writeNumberField("duration", pkg.duration());
        gen.writeNumberField("status", pkg.status());
        gen.writeEndObject();
    }

    private void writeIntersectionAccessPoint(JsonGenerator gen, IntersectionAccessPoint ap) throws IOException {
        gen.writeStartObject();
        if (ap.lane() != null)       gen.writeNumberField("lane",       ap.lane());
        if (ap.approach() != null)   gen.writeNumberField("approach",   ap.approach());
        if (ap.connection() != null) gen.writeNumberField("connection", ap.connection());
        gen.writeEndObject();
    }

    /* --------------------------------------------------------------------- */
    /* SignalRequester                                                       */
    /* --------------------------------------------------------------------- */

    private void writeSignalRequester(JsonGenerator gen, SignalRequester requester) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("id",              requester.id());
        gen.writeNumberField("request",         requester.request());
        gen.writeNumberField("sequence_number", requester.sequenceNumber());
        if (requester.role() != null)     gen.writeNumberField("role",      requester.role());
        if (requester.typeData() != null) gen.writeNumberField("type_data", requester.typeData());
        gen.writeEndObject();
    }
}

