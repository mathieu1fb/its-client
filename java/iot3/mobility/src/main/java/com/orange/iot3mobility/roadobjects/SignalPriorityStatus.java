/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 4.6)
 */
package com.orange.iot3mobility.roadobjects;

import com.orange.iot3mobility.messages.ssem.core.SsemCodec;
import com.orange.iot3mobility.messages.ssem.v201.model.status.SignalStatusPackage;

import java.util.Collections;
import java.util.List;

/**
 * Represents the RSU's current view of all active priority or preemption request statuses
 * at one intersection, derived from a received SSEM (Signal Status Extended Message,
 * ETSI IS TS 103 301).
 * <p>
 * One {@code SignalPriorityStatus} is maintained per {@code (sourceUuid, regionId, intersectionId)}
 * triplet. A single SSEM may describe statuses for several intersections; each produces a
 * separate object.
 * <p>
 * <strong>Key format:</strong> {@code {sourceUuid}_{regionId}_{intersectionId}}
 * <p>
 * Objects expire on a rolling {@link #MAX_STALENESS_MS}-millisecond timeout, matching
 * the staleness threshold used for {@link SignalController}.
 * <p>
 * Instances are created and managed exclusively by
 * {@link com.orange.iot3mobility.managers.SignalStatusManager};
 * application code should not construct them directly.
 */
public class SignalPriorityStatus {

    /**
     * Rolling timeout in milliseconds.
     * SSEM is event-driven (sent on status change); 5 seconds provides a generous
     * window before declaring a RSU silent.
     */
    public static final int MAX_STALENESS_MS = 5000;

    /** Composite key: {@code {sourceUuid}_{regionId}_{intersectionId}}. */
    private final String uuid;

    /** MQTT source UUID of the RSU that emitted this SSEM. */
    private final String sourceUuid;

    /** Station ID of the RSU, from the SSEM message header. */
    private final long stationId;

    /** Regional component of the intersection ID (0 when absent in the SSEM). */
    private final int regionId;

    /** Local intersection ID. */
    private final int intersectionId;

    /**
     * Current list of per-request status packages for this intersection.
     * Replaced in full on every SSEM update.
     */
    private List<SignalStatusPackage> packages;

    /** Latest raw SSEM frame used to populate or refresh this object. */
    private SsemCodec.SsemFrame<?> ssemFrame;

    /** Timestamp of the last received SSEM update, used for staleness calculation. */
    private long timestamp;

    /** Package-private: constructed only by {@link com.orange.iot3mobility.managers.SignalStatusManager}. */
    public SignalPriorityStatus(String uuid,
                         String sourceUuid,
                         long stationId,
                         int regionId,
                         int intersectionId,
                         List<SignalStatusPackage> packages,
                         SsemCodec.SsemFrame<?> ssemFrame) {
        this.uuid = uuid;
        this.sourceUuid = sourceUuid;
        this.stationId = stationId;
        this.regionId = regionId;
        this.intersectionId = intersectionId;
        this.packages = packages;
        this.ssemFrame = ssemFrame;
        updateTimestamp();
    }

    // -------------------------------------------------------------------------
    // Update
    // -------------------------------------------------------------------------

    /** Replaces the packages list and raw frame, and resets the staleness clock. */
    public void update(List<SignalStatusPackage> packages, SsemCodec.SsemFrame<?> ssemFrame) {
        this.packages = packages;
        this.ssemFrame = ssemFrame;
        updateTimestamp();
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    /** Returns the composite manager key ({@code {sourceUuid}_{regionId}_{intersectionId}}). */
    public String getUuid() { return uuid; }

    /** Returns the MQTT source UUID of the RSU. */
    public String getSourceUuid() { return sourceUuid; }

    /** Returns the station ID of the RSU. */
    public long getStationId() { return stationId; }

    /** Returns the regional component of the intersection ID (0 when absent). */
    public int getRegionId() { return regionId; }

    /** Returns the local ID of the intersection. */
    public int getIntersectionId() { return intersectionId; }

    /**
     * Returns a read-only view of the current per-request status packages for this intersection.
     * Each entry describes the status of one requestor's priority/preemption request.
     */
    public List<SignalStatusPackage> getPackages() {
        return Collections.unmodifiableList(packages);
    }

    /** Returns the latest raw SSEM frame for this intersection's status. */
    public SsemCodec.SsemFrame<?> getSsemFrame() { return ssemFrame; }

    /** Returns the Unix-epoch timestamp of the last received SSEM update. */
    public long getTimestamp() { return timestamp; }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    /** Resets the staleness clock to the current wall-clock time. */
    public void updateTimestamp() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Returns {@code true} if this status is still within its {@link #MAX_STALENESS_MS} window
     * and has not yet expired.
     */
    public boolean stillLiving() {
        return System.currentTimeMillis() - timestamp < MAX_STALENESS_MS;
    }

    // -------------------------------------------------------------------------
    // Testing support (package-private)
    // -------------------------------------------------------------------------

    /**
     * Backdates the internal timestamp so that {@link #stillLiving()} immediately returns
     * {@code false}. Use in unit tests together with the manager's expiry-check method to
     * simulate a timeout without {@code Thread.sleep()}.
     */
    void backdateTimestampForTesting() {
        this.timestamp = System.currentTimeMillis() - MAX_STALENESS_MS - 1;
    }
}

