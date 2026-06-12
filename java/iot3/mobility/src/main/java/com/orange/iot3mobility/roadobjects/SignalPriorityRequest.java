/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 4.6)
 */
package com.orange.iot3mobility.roadobjects;

import com.orange.iot3mobility.messages.srem.core.SremCodec;
import com.orange.iot3mobility.quadkey.LatLng;

/**
 * Represents one active signal priority or preemption request from a single vehicle or VRU,
 * derived from a received SREM (Signal Request Extended Message, ETSI IS TS 103 301).
 * <p>
 * One {@code SignalPriorityRequest} is maintained per {@code (sourceUuid, stationId, requestId)}
 * triplet. Multiple requests from the same station for different intersections each produce a
 * separate object.
 * <p>
 * <strong>Key format:</strong> {@code {sourceUuid}_{stationId}_{requestId}}
 * <p>
 * Objects expire on a rolling {@link #LIFETIME_MS}-millisecond timeout. Vehicles must emit
 * at least one SREM per second while a request is active; a 3-second window provides a
 * comfortable margin above that minimum rate.
 * <p>
 * Two optional geographic positions are resolved automatically from MAPEM/SPATEM data when
 * available: {@link #getIntersectionRefPoint()} (the target intersection reference point) and
 * {@link #getSignalGroupPosition()} (the stop-line of the inbound signal group).
 * <p>
 * Instances are created and managed exclusively by
 * {@link com.orange.iot3mobility.managers.SignalRequestManager};
 * application code should not construct them directly.
 */
public class SignalPriorityRequest {

    /**
     * Rolling timeout in milliseconds.
     * Vehicles must emit at least 1 SREM/s while a request is active (ETSI minimum);
     * 3 000 ms gives a comfortable grace margin.
     */
    public static final int LIFETIME_MS = 3000;

    /** Composite key: {@code {sourceUuid}_{stationId}_{requestId}}. */
    private final String uuid;

    /** MQTT source UUID of the requesting station. */
    private final String sourceUuid;

    /** Station ID of the requesting ITS-S, from the SREM message header. */
    private final long stationId;

    /** Request identifier, unique per station for the duration of the request [0..255]. */
    private final int requestId;

    /** Type of priority or preemption request. */
    private final PriorityRequestType requestType;

    /** Regional component of the target intersection ID (0 when absent in the SREM). */
    private final int regionId;

    /** Local intersection ID of the target intersection. */
    private final int intersectionId;

    /** Inbound access point where the requestor is currently located. */
    private final PriorityAccessPoint accessPoint;

    /**
     * Geographic position of the requestor at the time the SREM was sent, decoded from
     * DSRC units to WGS-84 degrees. {@code null} when the SREM contains no position.
     */
    private final LatLng position;

    /** Latest raw SREM frame used to populate or refresh this object. */
    private SremCodec.SremFrame<?> sremFrame;

    /** Timestamp of the last received SREM update, used for rolling-expiry calculation. */
    private long timestamp;

    /**
     * Geographic reference point of the target intersection, resolved from MAPEM data.
     * {@code null} until a matching {@link RoadIntersection} is available.
     */
    private LatLng intersectionRefPoint;

    /**
     * Stop-line position of the signal group controlling the inbound approach, resolved from
     * MAPEM + SPATEM data via {@link SignalController#getSignalGroupForLane(int)}.
     * {@code null} until both MAPEM and SPATEM data are available for the target intersection,
     * or when {@code inboundLane} does not reference a lane ID.
     */
    private LatLng signalGroupPosition;

    /** Package-private: constructed only by {@link com.orange.iot3mobility.managers.SignalRequestManager}. */
    public SignalPriorityRequest(String uuid,
                                 String sourceUuid,
                                 long stationId,
                                 int requestId,
                                 PriorityRequestType requestType,
                                 int regionId,
                                 int intersectionId,
                                 PriorityAccessPoint accessPoint,
                                 LatLng position,
                                 SremCodec.SremFrame<?> sremFrame) {
        this.uuid = uuid;
        this.sourceUuid = sourceUuid;
        this.stationId = stationId;
        this.requestId = requestId;
        this.requestType = requestType;
        this.regionId = regionId;
        this.intersectionId = intersectionId;
        this.accessPoint = accessPoint;
        this.position = position;
        this.sremFrame = sremFrame;
        updateTimestamp();
    }

    // -------------------------------------------------------------------------
    // Update
    // -------------------------------------------------------------------------

    /** Updates the raw frame reference and resets the rolling expiry clock. */
    public void update(SremCodec.SremFrame<?> sremFrame) {
        this.sremFrame = sremFrame;
        updateTimestamp();
    }

    // -------------------------------------------------------------------------
    // Position resolution (called by managers only)
    // -------------------------------------------------------------------------

    /**
     * Attempts to resolve {@link #intersectionRefPoint} from the provided intersection.
     * Does nothing if the ref point is already set, or if the intersection does not match
     * this request's {@code (regionId, intersectionId)}.
     *
     * @param roadIntersection candidate intersection
     * @return {@code true} if the ref point was newly resolved by this call
     */
    public boolean resolveIntersectionRefPoint(RoadIntersection roadIntersection) {
        if (intersectionRefPoint != null) return false;
        if (roadIntersection.getRegionId() != regionId
                || roadIntersection.getIntersectionId() != intersectionId) return false;
        intersectionRefPoint = roadIntersection.getRefPoint();
        return intersectionRefPoint != null;
    }

    /**
     * Attempts to resolve {@link #signalGroupPosition} from the provided signal controller.
     * Only applicable when {@link #accessPoint} carries a lane ID; does nothing for approach- or
     * connection-based access points. Does nothing if already resolved, or if the controller does
     * not match this request's {@code (regionId, intersectionId)}.
     *
     * @param signalController candidate signal controller
     * @return {@code true} if the position was newly resolved by this call
     */
    public boolean resolveSignalGroupPosition(SignalController signalController) {
        if (signalGroupPosition != null) return false;
        if (signalController.getRegionId() != regionId
                || signalController.getIntersectionId() != intersectionId) return false;
        if (accessPoint == null || !accessPoint.hasLane()) return false;
        SignalGroup signalGroup = signalController.getSignalGroupForLane(accessPoint.getLane());
        if (signalGroup == null || signalGroup.getPosition() == null) return false;
        signalGroupPosition = signalGroup.getPosition();
        return true;
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    /** Returns the composite manager key ({@code {sourceUuid}_{stationId}_{requestId}}). */
    public String getUuid() { return uuid; }

    /** Returns the MQTT source UUID of the requesting station. */
    public String getSourceUuid() { return sourceUuid; }

    /** Returns the station ID of the requesting ITS-S. */
    public long getStationId() { return stationId; }

    /** Returns the request ID [0..255]. */
    public int getRequestId() { return requestId; }

    /** Returns the type of this priority or preemption request. */
    public PriorityRequestType getRequestType() { return requestType; }

    /** Returns the regional component of the target intersection ID (0 when absent). */
    public int getRegionId() { return regionId; }

    /** Returns the local ID of the target intersection. */
    public int getIntersectionId() { return intersectionId; }

    /** Returns the inbound access point at the target intersection. */
    public PriorityAccessPoint getAccessPoint() { return accessPoint; }

    /**
     * Returns the WGS-84 position of the requestor at the time of the SREM,
     * or {@code null} if no position was included in the message.
     */
    public LatLng getPosition() { return position; }

    /** Returns the latest raw SREM frame for this request. */
    public SremCodec.SremFrame<?> getSremFrame() { return sremFrame; }

    /** Returns the Unix-epoch timestamp of the last received SREM update. */
    public long getTimestamp() { return timestamp; }

    /**
     * Returns the WGS-84 reference point of the target intersection, resolved from MAPEM data,
     * or {@code null} if no MAPEM-derived intersection data is yet available.
     */
    public LatLng getIntersectionRefPoint() { return intersectionRefPoint; }

    /**
     * Returns the WGS-84 stop-line position of the signal group controlling the inbound approach,
     * resolved from MAPEM + SPATEM data, or {@code null} if not yet available or if the inbound
     * access point does not reference a lane ID.
     */
    public LatLng getSignalGroupPosition() { return signalGroupPosition; }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    /** Resets the rolling expiry clock to the current wall-clock time. */
    public void updateTimestamp() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Returns {@code true} if this request is still within its {@link #LIFETIME_MS} window
     * and has not yet expired.
     */
    public boolean stillLiving() {
        return System.currentTimeMillis() - timestamp < LIFETIME_MS;
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
        this.timestamp = System.currentTimeMillis() - LIFETIME_MS - 1;
    }
}

