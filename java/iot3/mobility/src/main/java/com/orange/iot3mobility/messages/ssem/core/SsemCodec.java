/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 4.6)
 */
package com.orange.iot3mobility.messages.ssem.core;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.orange.iot3mobility.messages.ssem.v201.codec.SsemReader201;
import com.orange.iot3mobility.messages.ssem.v201.codec.SsemWriter201;
import com.orange.iot3mobility.messages.ssem.v201.model.SsemEnvelope201;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Unified entry point to decode/encode SSEM envelopes across all supported versions.
 */
public final class SsemCodec {

    /**
     * Wrapper exposing both the detected SSEM version and the decoded envelope.
     *
     * @param <T> Envelope type (currently only {@link SsemEnvelope201}).
     */
    public record SsemFrame<T>(SsemVersion version, T envelope) {}

    private final JsonFactory jsonFactory;
    private final SsemReader201 reader201;
    private final SsemWriter201 writer201;

    public SsemCodec(JsonFactory jsonFactory) {
        this.jsonFactory = Objects.requireNonNull(jsonFactory, "jsonFactory");
        this.reader201 = new SsemReader201(jsonFactory);
        this.writer201 = new SsemWriter201(jsonFactory);
    }

    /**
     * Reads and decodes a SSEM JSON payload from an {@link InputStream}.
     */
    public SsemFrame<?> read(InputStream in) throws IOException {
        byte[] payload = in.readAllBytes();
        SsemVersion version = detectVersion(payload);
        return switch (version) {
            case V2_0_1 -> new SsemFrame<>(version, reader201.read(new ByteArrayInputStream(payload)));
        };
    }

    /**
     * Reads and decodes a SSEM JSON string.
     */
    public SsemFrame<?> read(String json) throws IOException {
        Objects.requireNonNull(json, "json");
        byte[] payload = json.getBytes(StandardCharsets.UTF_8);
        SsemVersion version = detectVersion(payload);
        return switch (version) {
            case V2_0_1 -> new SsemFrame<>(version, reader201.read(new ByteArrayInputStream(payload)));
        };
    }

    /**
     * Writes the given envelope to the provided {@link OutputStream}.
     */
    public void write(SsemVersion version, Object envelope, OutputStream out) throws IOException {
        switch (version) {
            case V2_0_1 -> writer201.write(cast(envelope, SsemEnvelope201.class), out);
            default -> throw new SsemException("Unsupported version: " + version);
        }
    }

    private SsemVersion detectVersion(byte[] payload) throws IOException {
        try (JsonParser parser = jsonFactory.createParser(payload)) {
            if (parser.nextToken() != JsonToken.START_OBJECT) {
                throw new SsemException("Expected JSON object at root");
            }
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                String field = parser.currentName();
                parser.nextToken();
                if ("version".equals(field)) {
                    return SsemVersion.fromJsonValue(parser.getValueAsString());
                } else {
                    parser.skipChildren();
                }
            }
        }
        throw new SsemException("Missing 'version' field in SSEM payload");
    }

    @SuppressWarnings("unchecked")
    private static <T> T cast(Object value, Class<T> type) {
        if (!type.isInstance(value)) {
            throw new SsemException("Expected envelope of type " + type.getName()
                    + " but got " + (value == null ? "null" : value.getClass().getName()));
        }
        return (T) value;
    }
}

