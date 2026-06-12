/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 4.6)
 */
package com.orange.iot3mobility.messages.srem.v201.codec;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.orange.iot3mobility.messages.srem.v201.model.SremEnvelope201;
import com.orange.iot3mobility.messages.srem.v201.model.SremMessage201;
import com.orange.iot3mobility.messages.srem.v201.model.request.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Streaming JSON writer for SREM v2.0.1 envelopes.
 */
public final class SremWriter201 {

    private final JsonFactory jsonFactory;

    public SremWriter201(JsonFactory jsonFactory) {
        this.jsonFactory = jsonFactory;
    }

    public void write(SremEnvelope201 envelope, OutputStream out) throws IOException {
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

    private void writeMessage(JsonGenerator gen, SremMessage201 msg) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("protocol_version", msg.protocolVersion());
        gen.writeNumberField("station_id", msg.stationId());
        gen.writeNumberField("second", msg.second());
        if (msg.timestamp() != null)       gen.writeNumberField("timestamp",       msg.timestamp());
        if (msg.sequenceNumber() != null)  gen.writeNumberField("sequence_number", msg.sequenceNumber());
        if (msg.requests() != null && !msg.requests().isEmpty()) {
            gen.writeFieldName("requests");
            writeRequestPackages(gen, msg.requests());
        }
        gen.writeFieldName("requestor");
        writeRequestor(gen, msg.requestor());
        gen.writeEndObject();
    }

    /* --------------------------------------------------------------------- */
    /* Requestor                                                             */
    /* --------------------------------------------------------------------- */

    private void writeRequestor(JsonGenerator gen, RequestorDescription requestor) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("id", requestor.id());
        if (requestor.type() != null) {
            gen.writeFieldName("type");
            writeRequestorType(gen, requestor.type());
        }
        if (requestor.position() != null) {
            gen.writeFieldName("position");
            writeRequestorPositionVector(gen, requestor.position());
        }
        if (requestor.name() != null)             gen.writeStringField("name",              requestor.name());
        if (requestor.routeName() != null)        gen.writeStringField("route_name",        requestor.routeName());
        if (requestor.transitStatus() != null)    gen.writeNumberField("transit_status",    requestor.transitStatus());
        if (requestor.transitOccupancy() != null) gen.writeNumberField("transit_occupancy", requestor.transitOccupancy());
        if (requestor.transitSchedule() != null)  gen.writeNumberField("transit_schedule",  requestor.transitSchedule());
        gen.writeEndObject();
    }

    private void writeRequestorType(JsonGenerator gen, RequestorType type) throws IOException {
        gen.writeStartObject();
        if (type.role() != null)     gen.writeNumberField("role",     type.role());
        if (type.subrole() != null)  gen.writeNumberField("subrole",  type.subrole());
        if (type.request() != null)  gen.writeNumberField("request",  type.request());
        if (type.iso3883() != null)  gen.writeNumberField("iso3883",  type.iso3883());
        if (type.hpmsType() != null) gen.writeNumberField("hpmsType", type.hpmsType());
        gen.writeEndObject();
    }

    private void writeRequestorPositionVector(JsonGenerator gen, RequestorPositionVector pos) throws IOException {
        gen.writeStartObject();
        gen.writeFieldName("position");
        writePosition3D(gen, pos.position());
        if (pos.heading() != null) gen.writeNumberField("heading", pos.heading());
        if (pos.speed() != null)   gen.writeNumberField("speed",   pos.speed());
        gen.writeEndObject();
    }

    private void writePosition3D(JsonGenerator gen, Position3D pos) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("lat", pos.lat());
        gen.writeNumberField("lon", pos.lon());
        if (pos.elevation() != null) gen.writeNumberField("elevation", pos.elevation());
        gen.writeEndObject();
    }

    /* --------------------------------------------------------------------- */
    /* Signal requests                                                       */
    /* --------------------------------------------------------------------- */

    private void writeRequestPackages(JsonGenerator gen, List<SignalRequestPackage> packages) throws IOException {
        gen.writeStartArray();
        for (SignalRequestPackage pkg : packages) writeRequestPackage(gen, pkg);
        gen.writeEndArray();
    }

    private void writeRequestPackage(JsonGenerator gen, SignalRequestPackage pkg) throws IOException {
        gen.writeStartObject();
        gen.writeFieldName("request");
        writeSignalRequest(gen, pkg.request());
        if (pkg.minute() != null)   gen.writeNumberField("minute",   pkg.minute());
        if (pkg.second() != null)   gen.writeNumberField("second",   pkg.second());
        if (pkg.duration() != null) gen.writeNumberField("duration", pkg.duration());
        gen.writeEndObject();
    }

    private void writeSignalRequest(JsonGenerator gen, SignalRequest req) throws IOException {
        gen.writeStartObject();
        gen.writeFieldName("id");
        writeIntersectionReferenceId(gen, req.id());
        gen.writeNumberField("request_id",   req.requestId());
        gen.writeNumberField("request_type", req.requestType());
        gen.writeFieldName("inbound_lane");
        writeIntersectionAccessPoint(gen, req.inboundLane());
        if (req.outboundLane() != null) {
            gen.writeFieldName("outbound_lane");
            writeIntersectionAccessPoint(gen, req.outboundLane());
        }
        gen.writeEndObject();
    }

    private void writeIntersectionReferenceId(JsonGenerator gen, IntersectionReferenceId id) throws IOException {
        gen.writeStartObject();
        if (id.region() != null) gen.writeNumberField("region", id.region());
        gen.writeNumberField("id", id.id());
        gen.writeEndObject();
    }

    private void writeIntersectionAccessPoint(JsonGenerator gen, IntersectionAccessPoint ap) throws IOException {
        gen.writeStartObject();
        if (ap.lane() != null)       gen.writeNumberField("lane",       ap.lane());
        if (ap.approach() != null)   gen.writeNumberField("approach",   ap.approach());
        if (ap.connection() != null) gen.writeNumberField("connection", ap.connection());
        gen.writeEndObject();
    }
}

