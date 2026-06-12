/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 4.6)
 */
package com.orange.iot3mobility.roadobjects;

/**
 * Version-agnostic representation of an intersection access point.
 * <p>
 * Identifies the specific entry or exit point at an intersection — exactly one of the three
 * fields is expected to be set (lane, approach, or connection). All three are optional to
 * support all DSRC access-point encoding variants.
 */
public class PriorityAccessPoint {

    /** Lane identifier, or {@code null} when not used. */
    private final Integer lane;

    /** Approach identifier, or {@code null} when not used. */
    private final Integer approach;

    /** Connection identifier, or {@code null} when not used. */
    private final Integer connection;

    public PriorityAccessPoint(Integer lane, Integer approach, Integer connection) {
        this.lane = lane;
        this.approach = approach;
        this.connection = connection;
    }

    /** Returns the lane ID, or {@code null} if this access point is not lane-based. */
    public Integer getLane() { return lane; }

    /** Returns the approach ID, or {@code null} if this access point is not approach-based. */
    public Integer getApproach() { return approach; }

    /** Returns the connection ID, or {@code null} if this access point is not connection-based. */
    public Integer getConnection() { return connection; }

    /** Returns {@code true} if this access point is identified by a lane ID. */
    public boolean hasLane() { return lane != null; }

    /** Returns {@code true} if this access point is identified by an approach ID. */
    public boolean hasApproach() { return approach != null; }

    /** Returns {@code true} if this access point is identified by a connection ID. */
    public boolean hasConnection() { return connection != null; }
}

