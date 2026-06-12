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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * High-level helper around {@link SremCodec}.
 * <p>
 * Manages a shared {@link JsonFactory} and {@link SremCodec} instance.
 * Provides String-based APIs convenient for MQTT payloads.
 * Thread-safe: stateless, all shared components are immutable.
 */
public final class SremHelper {

    private final JsonFactory jsonFactory;
    private final SremCodec sremCodec;

    /** Default constructor: creates its own {@link JsonFactory}. */
    public SremHelper() {
        this(new JsonFactory());
    }

    /** Constructor with an externally-provided {@link JsonFactory}. */
    public SremHelper(JsonFactory jsonFactory) {
        this.jsonFactory = Objects.requireNonNull(jsonFactory, "jsonFactory");
        this.sremCodec = new SremCodec(this.jsonFactory);
    }

    // -------------------------------------------------------------------------
    // Parsing
    // -------------------------------------------------------------------------

    /**
     * Parse a SREM JSON payload string; the version is auto-detected.
     *
     * @param jsonPayload JSON string containing a SREM envelope
     * @return a {@link SremCodec.SremFrame} with the detected version and typed envelope
     * @throws IOException    if the JSON is malformed or an I/O error occurs
     * @throws SremException if the SREM structure is invalid
     */
    public SremCodec.SremFrame<?> parse(String jsonPayload) throws IOException {
        Objects.requireNonNull(jsonPayload, "jsonPayload");
        return sremCodec.read(jsonPayload);
    }

    /**
     * Parse a SREM JSON payload and return a v2.0.1 envelope.
     * Throws if the detected version is not 2.0.1.
     */
    public SremEnvelope201 parse201(String jsonPayload) throws IOException {
        SremCodec.SremFrame<?> frame = parse(jsonPayload);
        if (frame.version() != SremVersion.V2_0_1) {
            throw new SremException("Expected SREM version 2.0.1 but got " + frame.version());
        }
        return (SremEnvelope201) frame.envelope();
    }

    // -------------------------------------------------------------------------
    // Serialization
    // -------------------------------------------------------------------------

    /**
     * Serialize a v2.0.1 SREM envelope to a JSON string.
     */
    public String toJson(SremEnvelope201 envelope) throws IOException {
        Objects.requireNonNull(envelope, "envelope");
        return writeToString(SremVersion.V2_0_1, envelope);
    }

    /**
     * Generic serialization to JSON string.
     */
    public String toJson(SremVersion version, Object envelope) throws IOException {
        Objects.requireNonNull(version, "version");
        Objects.requireNonNull(envelope, "envelope");
        return writeToString(version, envelope);
    }

    // -------------------------------------------------------------------------
    // Internal
    // -------------------------------------------------------------------------

    private String writeToString(SremVersion version, Object envelope) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
        sremCodec.write(version, envelope, out);
        return out.toString(StandardCharsets.UTF_8);
    }
}

