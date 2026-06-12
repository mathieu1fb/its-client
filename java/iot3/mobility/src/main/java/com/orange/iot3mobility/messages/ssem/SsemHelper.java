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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * High-level helper around {@link SsemCodec}.
 * <p>
 * Manages a shared {@link JsonFactory} and {@link SsemCodec} instance.
 * Provides String-based APIs convenient for MQTT payloads.
 * Thread-safe: stateless, all shared components are immutable.
 */
public final class SsemHelper {

    private final JsonFactory jsonFactory;
    private final SsemCodec ssemCodec;

    /** Default constructor: creates its own {@link JsonFactory}. */
    public SsemHelper() {
        this(new JsonFactory());
    }

    /** Constructor with an externally-provided {@link JsonFactory}. */
    public SsemHelper(JsonFactory jsonFactory) {
        this.jsonFactory = Objects.requireNonNull(jsonFactory, "jsonFactory");
        this.ssemCodec = new SsemCodec(this.jsonFactory);
    }

    // -------------------------------------------------------------------------
    // Parsing
    // -------------------------------------------------------------------------

    /**
     * Parse a SSEM JSON payload string; the version is auto-detected.
     *
     * @param jsonPayload JSON string containing a SSEM envelope
     * @return a {@link SsemCodec.SsemFrame} with the detected version and typed envelope
     * @throws IOException    if the JSON is malformed or an I/O error occurs
     * @throws SsemException if the SSEM structure is invalid
     */
    public SsemCodec.SsemFrame<?> parse(String jsonPayload) throws IOException {
        Objects.requireNonNull(jsonPayload, "jsonPayload");
        return ssemCodec.read(jsonPayload);
    }

    /**
     * Parse a SSEM JSON payload and return a v2.0.1 envelope.
     * Throws if the detected version is not 2.0.1.
     */
    public SsemEnvelope201 parse201(String jsonPayload) throws IOException {
        SsemCodec.SsemFrame<?> frame = parse(jsonPayload);
        if (frame.version() != SsemVersion.V2_0_1) {
            throw new SsemException("Expected SSEM version 2.0.1 but got " + frame.version());
        }
        return (SsemEnvelope201) frame.envelope();
    }

    // -------------------------------------------------------------------------
    // Serialization
    // -------------------------------------------------------------------------

    /**
     * Serialize a v2.0.1 SSEM envelope to a JSON string.
     */
    public String toJson(SsemEnvelope201 envelope) throws IOException {
        Objects.requireNonNull(envelope, "envelope");
        return writeToString(SsemVersion.V2_0_1, envelope);
    }

    /**
     * Generic serialization to JSON string.
     */
    public String toJson(SsemVersion version, Object envelope) throws IOException {
        Objects.requireNonNull(version, "version");
        Objects.requireNonNull(envelope, "envelope");
        return writeToString(version, envelope);
    }

    // -------------------------------------------------------------------------
    // Internal
    // -------------------------------------------------------------------------

    private String writeToString(SsemVersion version, Object envelope) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
        ssemCodec.write(version, envelope, out);
        return out.toString(StandardCharsets.UTF_8);
    }
}

