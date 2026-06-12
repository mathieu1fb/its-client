/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 4.6)
 */
package com.orange.iot3mobility.messages.srem.core;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.orange.iot3mobility.messages.srem.v201.codec.SremReader201;
import com.orange.iot3mobility.messages.srem.v201.codec.SremWriter201;
import com.orange.iot3mobility.messages.srem.v201.model.SremEnvelope201;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Unified entry point to decode/encode SREM envelopes across all supported versions.
 */
public final class SremCodec {

    /**
     * Wrapper exposing both the detected SREM version and the decoded envelope.
     *
     * @param <T> Envelope type (currently only {@link SremEnvelope201}).
     */
    public record SremFrame<T>(SremVersion version, T envelope) {}

    private final JsonFactory jsonFactory;
    private final SremReader201 reader201;
    private final SremWriter201 writer201;

    public SremCodec(JsonFactory jsonFactory) {
        this.jsonFactory = Objects.requireNonNull(jsonFactory, "jsonFactory");
        this.reader201 = new SremReader201(jsonFactory);
        this.writer201 = new SremWriter201(jsonFactory);
    }

    /**
     * Reads and decodes a SREM JSON payload from an {@link InputStream}.
     */
    public SremFrame<?> read(InputStream in) throws IOException {
        byte[] payload = in.readAllBytes();
        SremVersion version = detectVersion(payload);
        return switch (version) {
            case V2_0_1 -> new SremFrame<>(version, reader201.read(new ByteArrayInputStream(payload)));
        };
    }

    /**
     * Reads and decodes a SREM JSON string.
     */
    public SremFrame<?> read(String json) throws IOException {
        Objects.requireNonNull(json, "json");
        byte[] payload = json.getBytes(StandardCharsets.UTF_8);
        SremVersion version = detectVersion(payload);
        return switch (version) {
            case V2_0_1 -> new SremFrame<>(version, reader201.read(new ByteArrayInputStream(payload)));
        };
    }

    /**
     * Writes the given envelope to the provided {@link OutputStream}.
     */
    public void write(SremVersion version, Object envelope, OutputStream out) throws IOException {
        switch (version) {
            case V2_0_1 -> writer201.write(cast(envelope, SremEnvelope201.class), out);
            default -> throw new SremException("Unsupported version: " + version);
        }
    }

    private SremVersion detectVersion(byte[] payload) throws IOException {
        try (JsonParser parser = jsonFactory.createParser(payload)) {
            if (parser.nextToken() != JsonToken.START_OBJECT) {
                throw new SremException("Expected JSON object at root");
            }
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                String field = parser.currentName();
                parser.nextToken();
                if ("version".equals(field)) {
                    return SremVersion.fromJsonValue(parser.getValueAsString());
                } else {
                    parser.skipChildren();
                }
            }
        }
        throw new SremException("Missing 'version' field in SREM payload");
    }

    @SuppressWarnings("unchecked")
    private static <T> T cast(Object value, Class<T> type) {
        if (!type.isInstance(value)) {
            throw new SremException("Expected envelope of type " + type.getName()
                    + " but got " + (value == null ? "null" : value.getClass().getName()));
        }
        return (T) value;
    }
}

