/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 4.6)
 */
package com.orange.iot3mobility.messages.ssem.v201.codec;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.orange.iot3mobility.messages.ssem.v201.model.SsemEnvelope201;
import com.orange.iot3mobility.messages.ssem.v201.model.SsemMessage201;
import com.orange.iot3mobility.messages.ssem.v201.model.status.*;
import com.orange.iot3mobility.messages.ssem.v201.validation.SsemValidationException;
import com.orange.iot3mobility.messages.ssem.v201.validation.SsemValidator201;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Streaming JSON reader for SSEM v2.0.1 payloads.
 */
public final class SsemReader201 {

    private final JsonFactory jsonFactory;

    public SsemReader201(JsonFactory jsonFactory) {
        this.jsonFactory = jsonFactory;
    }

    public SsemEnvelope201 read(InputStream in) throws IOException {
        try (JsonParser parser = jsonFactory.createParser(in)) {
            expect(parser.nextToken(), JsonToken.START_OBJECT);

            String messageType = null, sourceUuid = null, version = null;
            Long timestamp = null;
            SsemMessage201 message = null;

            while (parser.nextToken() != JsonToken.END_OBJECT) {
                String field = parser.currentName();
                parser.nextToken();
                switch (field) {
                    case "message_type" -> messageType = parser.getValueAsString();
                    case "source_uuid"  -> sourceUuid = parser.getValueAsString();
                    case "timestamp"    -> timestamp = parser.getLongValue();
                    case "version"      -> version = parser.getValueAsString();
                    case "message"      -> message = readMessage(parser);
                    default             -> parser.skipChildren();
                }
            }

            SsemEnvelope201 envelope = new SsemEnvelope201(
                    requireField(messageType, "message_type"),
                    requireField(sourceUuid, "source_uuid"),
                    requireField(timestamp, "timestamp"),
                    requireField(version, "version"),
                    requireField(message, "message"));

            SsemValidator201.validateEnvelope(envelope);
            return envelope;
        }
    }

    /* --------------------------------------------------------------------- */
    /* Message                                                               */
    /* --------------------------------------------------------------------- */

    private SsemMessage201 readMessage(JsonParser parser) throws IOException {
        expect(parser.getCurrentToken(), JsonToken.START_OBJECT);
        Integer protocolVersion = null;
        Long stationId = null;
        Integer second = null;
        List<SignalStatus> status = null;
        Integer timestamp = null;
        Integer sequenceNumber = null;

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String field = parser.currentName();
            parser.nextToken();
            switch (field) {
                case "protocol_version" -> protocolVersion = parser.getIntValue();
                case "station_id"       -> stationId = parser.getLongValue();
                case "second"           -> second = parser.getIntValue();
                case "status"           -> status = readSignalStatusList(parser);
                case "timestamp"        -> timestamp = parser.getIntValue();
                case "sequence_number"  -> sequenceNumber = parser.getIntValue();
                default                 -> parser.skipChildren();
            }
        }
        return new SsemMessage201(
                requireField(protocolVersion, "protocol_version"),
                requireField(stationId, "station_id"),
                requireField(second, "second"),
                requireField(status, "status"),
                timestamp, sequenceNumber);
    }

    /* --------------------------------------------------------------------- */
    /* SignalStatus                                                          */
    /* --------------------------------------------------------------------- */

    private List<SignalStatus> readSignalStatusList(JsonParser parser) throws IOException {
        expect(parser.getCurrentToken(), JsonToken.START_ARRAY);
        List<SignalStatus> list = new ArrayList<>();
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            list.add(readSignalStatus(parser));
        }
        return list;
    }

    private SignalStatus readSignalStatus(JsonParser parser) throws IOException {
        expect(parser.getCurrentToken(), JsonToken.START_OBJECT);
        Integer sequenceNumber = null;
        IntersectionReferenceId id = null;
        List<SignalStatusPackage> sigStatus = null;

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String field = parser.currentName();
            parser.nextToken();
            switch (field) {
                case "sequence_number" -> sequenceNumber = parser.getIntValue();
                case "id"              -> id = readIntersectionReferenceId(parser);
                case "sig_status"      -> sigStatus = readSignalStatusPackages(parser);
                default                -> parser.skipChildren();
            }
        }
        return new SignalStatus(
                requireField(sequenceNumber, "status.sequence_number"),
                requireField(id, "status.id"),
                requireField(sigStatus, "status.sig_status"));
    }

    private IntersectionReferenceId readIntersectionReferenceId(JsonParser parser) throws IOException {
        expect(parser.getCurrentToken(), JsonToken.START_OBJECT);
        Integer region = null, id = null;

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String field = parser.currentName();
            parser.nextToken();
            switch (field) {
                case "region" -> region = parser.getIntValue();
                case "id"     -> id = parser.getIntValue();
                default       -> parser.skipChildren();
            }
        }
        return new IntersectionReferenceId(region, requireField(id, "intersection_reference_id.id"));
    }

    /* --------------------------------------------------------------------- */
    /* SignalStatusPackage                                                   */
    /* --------------------------------------------------------------------- */

    private List<SignalStatusPackage> readSignalStatusPackages(JsonParser parser) throws IOException {
        expect(parser.getCurrentToken(), JsonToken.START_ARRAY);
        List<SignalStatusPackage> list = new ArrayList<>();
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            list.add(readSignalStatusPackage(parser));
        }
        return list;
    }

    private SignalStatusPackage readSignalStatusPackage(JsonParser parser) throws IOException {
        expect(parser.getCurrentToken(), JsonToken.START_OBJECT);
        IntersectionAccessPoint inboundOn = null;
        Integer status = null;
        SignalRequester requester = null;
        IntersectionAccessPoint outboundOn = null;
        Integer minute = null, second = null, duration = null;

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String field = parser.currentName();
            parser.nextToken();
            switch (field) {
                case "inbound_on"  -> inboundOn = readIntersectionAccessPoint(parser);
                case "status"      -> status = parser.getIntValue();
                case "requester"   -> requester = readSignalRequester(parser);
                case "outbound_on" -> outboundOn = readIntersectionAccessPoint(parser);
                case "minute"      -> minute = parser.getIntValue();
                case "second"      -> second = parser.getIntValue();
                case "duration"    -> duration = parser.getIntValue();
                default            -> parser.skipChildren();
            }
        }
        return new SignalStatusPackage(
                requireField(inboundOn, "sig_status.inbound_on"),
                requireField(status, "sig_status.status"),
                requester, outboundOn, minute, second, duration);
    }

    private IntersectionAccessPoint readIntersectionAccessPoint(JsonParser parser) throws IOException {
        expect(parser.getCurrentToken(), JsonToken.START_OBJECT);
        Integer lane = null, approach = null, connection = null;

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String field = parser.currentName();
            parser.nextToken();
            switch (field) {
                case "lane"       -> lane = parser.getIntValue();
                case "approach"   -> approach = parser.getIntValue();
                case "connection" -> connection = parser.getIntValue();
                default           -> parser.skipChildren();
            }
        }
        return new IntersectionAccessPoint(lane, approach, connection);
    }

    /* --------------------------------------------------------------------- */
    /* SignalRequester                                                       */
    /* --------------------------------------------------------------------- */

    private SignalRequester readSignalRequester(JsonParser parser) throws IOException {
        expect(parser.getCurrentToken(), JsonToken.START_OBJECT);
        Long id = null;
        Integer request = null, sequenceNumber = null, role = null, typeData = null;

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String field = parser.currentName();
            parser.nextToken();
            switch (field) {
                case "id"              -> id = parser.getLongValue();
                case "request"         -> request = parser.getIntValue();
                case "sequence_number" -> sequenceNumber = parser.getIntValue();
                case "role"            -> role = parser.getIntValue();
                case "type_data"       -> typeData = parser.getIntValue();
                default                -> parser.skipChildren();
            }
        }
        return new SignalRequester(
                requireField(id, "requester.id"),
                requireField(request, "requester.request"),
                requireField(sequenceNumber, "requester.sequence_number"),
                role, typeData);
    }

    /* --------------------------------------------------------------------- */
    /* Helpers                                                               */
    /* --------------------------------------------------------------------- */

    private void expect(JsonToken actual, JsonToken expected) throws IOException {
        if (actual != expected) {
            throw new IOException("Expected " + expected + " but got " + actual);
        }
    }

    private static <T> T requireField(T value, String field) {
        if (value == null) throw new SsemValidationException("Missing mandatory field: " + field);
        return value;
    }
}

