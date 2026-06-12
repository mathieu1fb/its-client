/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 4.6)
 */
package com.orange.iot3mobility.managers;

import com.orange.iot3mobility.messages.srem.core.SremCodec;
import com.orange.iot3mobility.roadobjects.SignalPriorityRequest;

/**
 * Callback interface for SREM-derived {@link SignalPriorityRequest} lifecycle events.
 * <p>
 * A {@link SignalPriorityRequest} is created when a new {@code (sourceUuid, stationId, requestId)}
 * triplet is seen for the first time and updated as subsequent SREMs refresh it.
 * Objects expire on a rolling {@link SignalPriorityRequest#LIFETIME_MS}-millisecond timeout.
 * <p>
 * {@link #sremArrived} is always fired first, before any object-level callback.
 */
public interface IoT3SignalRequestCallback {

    /**
     * Called for every SREM message received, before any object-level processing.
     *
     * @param sremFrame the raw decoded SREM frame
     */
    void sremArrived(SremCodec.SremFrame<?> sremFrame);

    /**
     * Called when a priority or preemption request is seen for the first time
     * (new {@code (sourceUuid, stationId, requestId)} triplet).
     *
     * @param signalPriorityRequest the newly created request object
     */
    void newSignalPriorityRequest(SignalPriorityRequest signalPriorityRequest);

    /**
     * Called when an existing priority or preemption request is refreshed by a subsequent SREM.
     *
     * @param signalPriorityRequest the updated request object
     */
    void signalPriorityRequestUpdated(SignalPriorityRequest signalPriorityRequest);

    /**
     * Called when a request has not been refreshed within {@link SignalPriorityRequest#LIFETIME_MS}
     * milliseconds and is removed from the manager.
     *
     * @param signalPriorityRequest the expired request object
     */
    void signalPriorityRequestExpired(SignalPriorityRequest signalPriorityRequest);
}

