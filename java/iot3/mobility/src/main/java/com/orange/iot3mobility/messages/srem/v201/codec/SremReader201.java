/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 4.6)
 */
package com.orange.iot3mobility.messages.srem.v201.codec;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.orange.iot3mobility.messages.srem.v201.model.SremEnvelope201;
import com.orange.iot3mobility.messages.srem.v201.model.SremMessage201;
import com.orange.iot3mobility.messages.srem.v201.model.request.*;
import com.orange.iot3mobility.messages.srem.v201.validation.SremValidationException;
import com.orange.iot3mobility.messages.srem.v201.validation.SremValidator201;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Streaming JSON reader for SREM v2.0.1 payloads.
 */
public final class SremReader201 {

    private final JsonFactory jsonFactory;

    public SremReader201(JsonFactory jsonFactory) {
        this.jsonFactory = jsonFactory;
    }

    public SremEnvelope201 read(InputStream in) throws IOException {
        try (JsonParser parser = jsonFactory.createParser(in)) {
            expect(parser.nextToken(), JsonToken.START_OBJECT);

            String messageType = null, sourceUuid = null, version = null;
            Long timestamp = null;
            SremMessage201 message = null;

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

            SremEnvelope201 envelope = new SremEnvelope201(
                    requireField(messageType, "message_type"),
                    requireField(sourceUuid, "source_uuid"),
                    requireField(timestamp, "timestamp"),
                    requireField(version, "version"),
                    requireField(message, "message"));

            SremValidator201.validateEnvelope(envelope);
            return envelope;
        }
    }

    /* --------------------------------------------------------------------- */
    /* Message                                                               */
    /* --------------------------------------------------------------------- */

    private SremMessage201 readMessage(JsonParser parser) throws IOException {
        expect(parser.getCurrentToken(), JsonToken.START_OBJECT);
        Integer protocolVersion = null;
        Long stationId = null;
        Integer second = null;
        RequestorDescription requestor = null;
        Integer timestamp = null;
        Integer sequenceNumber = null;
        List<SignalRequestPackage> requests = null;

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String field = parser.currentName();
            parser.nextToken();
            switch (field) {
                case "protocol_version" -> protocolVersion = parser.getIntValue();
                case "station_id"       -> stationId = parser.getLongValue();
                case "second"           -> second = parser.getIntValue();
                case "requestor"        -> requestor = readRequestor(parser);
                case "timestamp"        -> timestamp = parser.getIntValue();
                case "sequence_number"  -> sequenceNumber = parser.getIntValue();
                case "requests"         -> requests = readRequestPackages(parser);
                default                 -> parser.skipChildren();
            }
        }
        return new SremMessage201(
                requireField(protocolVersion, "protocol_version"),
                requireField(stationId, "station_id"),
                requireField(second, "second"),
                requireField(requestor, "requestor"),
                timestamp, sequenceNumber, requests);
    }

    /* --------------------------------------------------------------------- */
    /* Requestor                                                             */
    /* --------------------------------------------------------------------- */

    private RequestorDescription readRequestor(JsonParser parser) throws IOException {
        expect(parser.getCurrentToken(), JsonToken.START_OBJECT);
        Long id = null;
        RequestorType type = null;
        RequestorPositionVector position = null;
        String name = null;
        String routeName = null;
        Integer transitStatus = null;
        Integer transitOccupancy = null;
        Integer transitSchedule = null;

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String field = parser.currentName();
            parser.nextToken();
            switch (field) {
                case "id"                 -> id = parser.getLongValue();
                case "type"               -> type = readRequestorType(parser);
                case "position"           -> position = readRequestorPositionVector(parser);
                case "name"               -> name = parser.getValueAsString();
                case "route_name"         -> routeName = parser.getValueAsString();
                case "transit_status"     -> transitStatus = parser.getIntValue();
                case "transit_occupancy"  -> transitOccupancy = parser.getIntValue();
                case "transit_schedule"   -> transitSchedule = parser.getIntValue();
                default                   -> parser.skipChildren();
            }
        }
        return new RequestorDescription(
                requireField(id, "requestor.id"),
                type, position, name, routeName,
                transitStatus, transitOccupancy, transitSchedule);
    }

    private RequestorType readRequestorType(JsonParser parser) throws IOException {
        expect(parser.getCurrentToken(), JsonToken.START_OBJECT);
        Integer role = null, subrole = null, request = null, iso3883 = null, hpmsType = null;

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String field = parser.currentName();
            parser.nextToken();
            switch (field) {
                case "role"      -> role = parser.getIntValue();
                case "subrole"   -> subrole = parser.getIntValue();
                case "request"   -> request = parser.getIntValue();
                case "iso3883"   -> iso3883 = parser.getIntValue();
                case "hpmsType"  -> hpmsType = parser.getIntValue();
                default          -> parser.skipChildren();
            }
        }
        return new RequestorType(role, subrole, request, iso3883, hpmsType);
    }

    private RequestorPositionVector readRequestorPositionVector(JsonParser parser) throws IOException {
        expect(parser.getCurrentToken(), JsonToken.START_OBJECT);
        Position3D position = null;
        Integer heading = null;
        Integer speed = null;

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String field = parser.currentName();
            parser.nextToken();
            switch (field) {
                case "position" -> position = readPosition3D(parser);
                case "heading"  -> heading = parser.getIntValue();
                case "speed"    -> speed = parser.getIntValue();
                default         -> parser.skipChildren();
            }
        }
        return new RequestorPositionVector(
                requireField(position, "requestor.position.position"),
                heading, speed);
    }

    private Position3D readPosition3D(JsonParser parser) throws IOException {
        expect(parser.getCurrentToken(), JsonToken.START_OBJECT);
        Integer lat = null, lon = null;
        Integer elevation = null;

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String field = parser.currentName();
            parser.nextToken();
            switch (field) {
                case "lat"       -> lat = parser.getIntValue();
                case "lon"       -> lon = parser.getIntValue();
                case "elevation" -> elevation = parser.getIntValue();
                default          -> parser.skipChildren();
            }
        }
        return new Position3D(
                requireField(lat, "position.lat"),
                requireField(lon, "position.lon"),
                elevation);
    }

    /* --------------------------------------------------------------------- */
    /* Signal requests                                                       */
    /* --------------------------------------------------------------------- */

    private List<SignalRequestPackage> readRequestPackages(JsonParser parser) throws IOException {
        expect(parser.getCurrentToken(), JsonToken.START_ARRAY);
        List<SignalRequestPackage> list = new ArrayList<>();
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            list.add(readRequestPackage(parser));
        }
        return list;
    }

    private SignalRequestPackage readRequestPackage(JsonParser parser) throws IOException {
        expect(parser.getCurrentToken(), JsonToken.START_OBJECT);
        SignalRequest request = null;
        Integer minute = null, second = null, duration = null;

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String field = parser.currentName();
            parser.nextToken();
            switch (field) {
                case "request"  -> request = readSignalRequest(parser);
                case "minute"   -> minute = parser.getIntValue();
                case "second"   -> second = parser.getIntValue();
                case "duration" -> duration = parser.getIntValue();
                default         -> parser.skipChildren();
            }
        }
        return new SignalRequestPackage(
                requireField(request, "requests.request"),
                minute, second, duration);
    }

    private SignalRequest readSignalRequest(JsonParser parser) throws IOException {
        expect(parser.getCurrentToken(), JsonToken.START_OBJECT);
        IntersectionReferenceId id = null;
        Integer requestId = null, requestType = null;
        IntersectionAccessPoint inboundLane = null, outboundLane = null;

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String field = parser.currentName();
            parser.nextToken();
            switch (field) {
                case "id"            -> id = readIntersectionReferenceId(parser);
                case "request_id"    -> requestId = parser.getIntValue();
                case "request_type"  -> requestType = parser.getIntValue();
                case "inbound_lane"  -> inboundLane = readIntersectionAccessPoint(parser);
                case "outbound_lane" -> outboundLane = readIntersectionAccessPoint(parser);
                default              -> parser.skipChildren();
            }
        }
        return new SignalRequest(
                requireField(id, "request.id"),
                requireField(requestId, "request.request_id"),
                requireField(requestType, "request.request_type"),
                requireField(inboundLane, "request.inbound_lane"),
                outboundLane);
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
    /* Helpers                                                               */
    /* --------------------------------------------------------------------- */

    private void expect(JsonToken actual, JsonToken expected) throws IOException {
        if (actual != expected) {
            throw new IOException("Expected " + expected + " but got " + actual);
        }
    }

    private static <T> T requireField(T value, String field) {
        if (value == null) throw new SremValidationException("Missing mandatory field: " + field);
        return value;
    }
}

