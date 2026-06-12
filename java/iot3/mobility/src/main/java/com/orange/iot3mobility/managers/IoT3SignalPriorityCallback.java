/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 4.6)
 */
package com.orange.iot3mobility.managers;

import com.orange.iot3mobility.messages.srem.core.SremCodec;
import com.orange.iot3mobility.messages.ssem.core.SsemCodec;
import com.orange.iot3mobility.roadobjects.SignalPriorityRequest;
import com.orange.iot3mobility.roadobjects.SignalPriorityStatus;

/**
 * Unified callback interface for both SREM-derived {@link SignalPriorityRequest} and
 * SSEM-derived {@link SignalPriorityStatus} lifecycle events.
 * <p>
 * Use together with
 * {@link com.orange.iot3mobility.IoT3Mobility#setSignalPriorityRoI(com.orange.iot3mobility.quadkey.LatLng, int, boolean)}
 * and
 * {@link com.orange.iot3mobility.IoT3Mobility#setSignalPriorityCallback(IoT3SignalPriorityCallback)}
 * to handle both SREM requests and SSEM statuses with a single setup call.
 * <p>
 * This interface composes {@link IoT3SignalRequestCallback} and {@link IoT3SignalStatusCallback}.
 */
public interface IoT3SignalPriorityCallback extends IoT3SignalRequestCallback, IoT3SignalStatusCallback {

    // -------------------------------------------------------------------------
    // IoT3SignalRequestCallback (SREM)
    // -------------------------------------------------------------------------

    /**
     * Called for every SREM message received, before object-level processing.
     *
     * @param sremFrame the raw decoded SREM frame
     */
    @Override
    void sremArrived(SremCodec.SremFrame<?> sremFrame);

    /**
     * Called when a priority or preemption request is seen for the first time.
     *
     * @param signalPriorityRequest the newly created request object
     */
    @Override
    void newSignalPriorityRequest(SignalPriorityRequest signalPriorityRequest);

    /**
     * Called when an existing priority or preemption request is refreshed by a subsequent SREM.
     *
     * @param signalPriorityRequest the updated request object
     */
    @Override
    void signalPriorityRequestUpdated(SignalPriorityRequest signalPriorityRequest);

    /**
     * Called when a request has not been refreshed within its lifetime and is removed.
     *
     * @param signalPriorityRequest the expired request object
     */
    @Override
    void signalPriorityRequestExpired(SignalPriorityRequest signalPriorityRequest);

    // -------------------------------------------------------------------------
    // IoT3SignalStatusCallback (SSEM)
    // -------------------------------------------------------------------------

    /**
     * Called for every SSEM message received, before object-level processing.
     *
     * @param ssemFrame the raw decoded SSEM frame
     */
    @Override
    void ssemArrived(SsemCodec.SsemFrame<?> ssemFrame);

    /**
     * Called when a signal priority status is seen for the first time for a given intersection.
     *
     * @param signalPriorityStatus the newly created status object
     */
    @Override
    void newSignalPriorityStatus(SignalPriorityStatus signalPriorityStatus);

    /**
     * Called when the status of an existing intersection is updated by a subsequent SSEM.
     *
     * @param signalPriorityStatus the updated status object
     */
    @Override
    void signalPriorityStatusUpdated(SignalPriorityStatus signalPriorityStatus);

    /**
     * Called when a status has not been refreshed within its staleness threshold and is removed.
     *
     * @param signalPriorityStatus the expired status object
     */
    @Override
    void signalPriorityStatusExpired(SignalPriorityStatus signalPriorityStatus);
}

