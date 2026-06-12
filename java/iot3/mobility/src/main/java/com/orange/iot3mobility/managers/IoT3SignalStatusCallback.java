/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 4.6)
 */
package com.orange.iot3mobility.managers;

import com.orange.iot3mobility.messages.ssem.core.SsemCodec;
import com.orange.iot3mobility.roadobjects.SignalPriorityStatus;

/**
 * Callback interface for SSEM-derived {@link SignalPriorityStatus} lifecycle events.
 * <p>
 * A {@link SignalPriorityStatus} is created when a new {@code (sourceUuid, regionId, intersectionId)}
 * triplet is seen for the first time and updated as subsequent SSEMs arrive for the same intersection.
 * Objects expire on a rolling {@link SignalPriorityStatus#MAX_STALENESS_MS}-millisecond timeout.
 * <p>
 * {@link #ssemArrived} is always fired first, before any object-level callback.
 */
public interface IoT3SignalStatusCallback {

    /**
     * Called for every SSEM message received, before any object-level processing.
     *
     * @param ssemFrame the raw decoded SSEM frame
     */
    void ssemArrived(SsemCodec.SsemFrame<?> ssemFrame);

    /**
     * Called when a signal priority status is seen for the first time for a given intersection
     * from a given RSU source (new {@code (sourceUuid, regionId, intersectionId)} triplet).
     *
     * @param signalPriorityStatus the newly created status object
     */
    void newSignalPriorityStatus(SignalPriorityStatus signalPriorityStatus);

    /**
     * Called when the status of an existing intersection is updated by a subsequent SSEM.
     *
     * @param signalPriorityStatus the updated status object
     */
    void signalPriorityStatusUpdated(SignalPriorityStatus signalPriorityStatus);

    /**
     * Called when a status has not been refreshed within {@link SignalPriorityStatus#MAX_STALENESS_MS}
     * milliseconds and is removed from the manager.
     *
     * @param signalPriorityStatus the expired status object
     */
    void signalPriorityStatusExpired(SignalPriorityStatus signalPriorityStatus);
}

