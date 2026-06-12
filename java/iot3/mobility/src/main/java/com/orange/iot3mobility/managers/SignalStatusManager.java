/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 4.6)
 */
package com.orange.iot3mobility.managers;

import com.orange.iot3mobility.messages.ssem.SsemHelper;
import com.orange.iot3mobility.messages.ssem.core.SsemCodec;
import com.orange.iot3mobility.messages.ssem.core.SsemVersion;
import com.orange.iot3mobility.messages.ssem.v201.model.SsemEnvelope201;
import com.orange.iot3mobility.messages.ssem.v201.model.SsemMessage201;
import com.orange.iot3mobility.messages.ssem.v201.model.status.IntersectionAccessPoint;
import com.orange.iot3mobility.messages.ssem.v201.model.status.SignalRequester;
import com.orange.iot3mobility.messages.ssem.v201.model.status.SignalStatus;
import com.orange.iot3mobility.messages.ssem.v201.model.status.SignalStatusPackage;
import com.orange.iot3mobility.roadobjects.PriorityAccessPoint;
import com.orange.iot3mobility.roadobjects.PriorityRequestStatus;
import com.orange.iot3mobility.roadobjects.PriorityRequestStatusType;
import com.orange.iot3mobility.roadobjects.RoadIntersection;
import com.orange.iot3mobility.roadobjects.SignalPriorityStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages {@link SignalPriorityStatus} objects derived from received SSEM messages.
 * <p>
 * One {@link SignalPriorityStatus} is maintained per {@code (sourceUuid, regionId, intersectionId)}
 * triplet. A single SSEM that describes several intersections produces a separate object for each.
 * Objects expire on a rolling {@link SignalPriorityStatus#MAX_STALENESS_MS}-millisecond timeout.
 * <p>
 * State is static — there is one shared store per JVM process.
 */
public class SignalStatusManager {

    private static final Logger LOGGER = Logger.getLogger(SignalStatusManager.class.getName());
    private static final String TAG = "IoT3Mobility.SignalStatusManager";

    private static final ArrayList<SignalPriorityStatus> SIGNAL_PRIORITY_STATUSES = new ArrayList<>();
    private static final HashMap<String, SignalPriorityStatus> SIGNAL_PRIORITY_STATUS_MAP = new HashMap<>();

    private static IoT3SignalStatusCallback ioT3SignalStatusCallback;
    private static ScheduledExecutorService scheduler;

    // -------------------------------------------------------------------------
    // Init
    // -------------------------------------------------------------------------

    /**
     * Registers the callback and starts the expiry scheduler (idempotent).
     *
     * @param callback the callback to receive lifecycle events
     */
    public static void init(IoT3SignalStatusCallback callback) {
        SignalStatusManager.ioT3SignalStatusCallback = callback;
        startExpirationCheck();
    }

    // -------------------------------------------------------------------------
    // SSEM processing
    // -------------------------------------------------------------------------

    /**
     * Decodes a raw SSEM JSON payload and updates the signal priority status store.
     *
     * @param message    raw JSON string received from the broker
     * @param ssemHelper helper used to decode the payload
     */
    public static void processSsem(String message, SsemHelper ssemHelper) {
        if (ioT3SignalStatusCallback == null) return;
        try {
            SsemCodec.SsemFrame<?> ssemFrame = ssemHelper.parse(message);
            ioT3SignalStatusCallback.ssemArrived(ssemFrame);

            if (ssemFrame.version() == SsemVersion.V2_0_1) {
                SsemEnvelope201 envelope = (SsemEnvelope201) ssemFrame.envelope();
                processSsemEnvelope201(envelope, ssemFrame);
            }
        } catch (IOException ioException) {
            LOGGER.log(Level.WARNING, TAG + " SSEM parsing error: " + ioException);
        } catch (RuntimeException runtimeException) {
            LOGGER.log(Level.WARNING, TAG + " SSEM processing error: " + runtimeException);
        }
    }

    private static void processSsemEnvelope201(SsemEnvelope201 envelope,
                                               SsemCodec.SsemFrame<?> ssemFrame) {
        SsemMessage201 msg = envelope.message();
        String sourceUuid = envelope.sourceUuid();
        long stationId = msg.stationId();

        // A single SSEM can carry statuses for multiple intersections
        synchronized (SIGNAL_PRIORITY_STATUS_MAP) {
            for (SignalStatus signalStatus : msg.status()) {
                int regionId = signalStatus.id().region() != null ? signalStatus.id().region() : 0;
                int intersectionId = signalStatus.id().id();
                String key = buildKey(sourceUuid, regionId, intersectionId);

                SignalPriorityStatus existing = SIGNAL_PRIORITY_STATUS_MAP.get(key);
                if (existing == null) {
                    SignalPriorityStatus newStatus = new SignalPriorityStatus(
                            key, sourceUuid, stationId, regionId, intersectionId,
                            toPriorityRequestStatuses(signalStatus.sigStatus()), ssemFrame);
                    SIGNAL_PRIORITY_STATUS_MAP.put(key, newStatus);
                    synchronized (SIGNAL_PRIORITY_STATUSES) {
                        SIGNAL_PRIORITY_STATUSES.add(newStatus);
                    }
                    tryResolveForNew(newStatus);
                    ioT3SignalStatusCallback.newSignalPriorityStatus(newStatus);
                } else {
                    existing.update(toPriorityRequestStatuses(signalStatus.sigStatus()), ssemFrame);
                    ioT3SignalStatusCallback.signalPriorityStatusUpdated(existing);
                }
            }
        }
    }

    /**
     * Converts a list of SSEM model {@link SignalStatusPackage} entries to the version-agnostic
     * {@link PriorityRequestStatus} road object type.
     */
    private static List<PriorityRequestStatus> toPriorityRequestStatuses(
            List<SignalStatusPackage> packages) {
        List<PriorityRequestStatus> result = new ArrayList<>(packages.size());
        for (SignalStatusPackage pkg : packages) {
            result.add(toPriorityRequestStatus(pkg));
        }
        return result;
    }

    private static PriorityRequestStatus toPriorityRequestStatus(SignalStatusPackage pkg) {
        SignalRequester requester = pkg.requester();
        Long requestorStationId = requester != null ? requester.id() : null;
        Integer requestId = requester != null ? requester.request() : null;
        return new PriorityRequestStatus(
                toLaneAccessPoint(pkg.inboundOn()),
                PriorityRequestStatusType.fromValue(pkg.status()),
                requestorStationId,
                requestId,
                toLaneAccessPoint(pkg.outboundOn()),
                pkg.minute(),
                pkg.second(),
                pkg.duration());
    }

    private static PriorityAccessPoint toLaneAccessPoint(IntersectionAccessPoint ap) {
        if (ap == null) return null;
        return new PriorityAccessPoint(ap.lane(), ap.approach(), ap.connection());
    }

    /**
     * Attempts to resolve the intersection reference point on a newly created status object
     * from already-available MAPEM data. Called immediately after creation.
     */
    private static void tryResolveForNew(SignalPriorityStatus status) {
        for (RoadIntersection intersection : RoadGeometryManager.getRoadIntersections()) {
            if (status.resolveIntersectionRefPoint(intersection)) break;
        }
    }

    /**
     * Called by {@link RoadGeometryManager} when a {@link RoadIntersection} is created or updated.
     * Finds every {@link SignalPriorityStatus} describing that intersection and resolves its
     * intersection reference point; fires {@code signalPriorityStatusUpdated} for each resolved object.
     *
     * @param roadIntersection the newly available intersection
     */
    public static void tryResolvePositionsFromIntersection(RoadIntersection roadIntersection) {
        synchronized (SIGNAL_PRIORITY_STATUSES) {
            for (SignalPriorityStatus status : SIGNAL_PRIORITY_STATUSES) {
                if (status.resolveIntersectionRefPoint(roadIntersection)) {
                    if (ioT3SignalStatusCallback != null) {
                        ioT3SignalStatusCallback.signalPriorityStatusUpdated(status);
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Expiry
    // -------------------------------------------------------------------------

    private static void checkAndRemoveExpiredStatuses() {
        synchronized (SIGNAL_PRIORITY_STATUSES) {
            Iterator<SignalPriorityStatus> iterator = SIGNAL_PRIORITY_STATUSES.iterator();
            while (iterator.hasNext()) {
                SignalPriorityStatus status = iterator.next();
                if (!status.stillLiving()) {
                    iterator.remove();
                    synchronized (SIGNAL_PRIORITY_STATUS_MAP) {
                        SIGNAL_PRIORITY_STATUS_MAP.values().remove(status);
                    }
                    if (ioT3SignalStatusCallback != null) {
                        ioT3SignalStatusCallback.signalPriorityStatusExpired(status);
                    }
                }
            }
        }
    }

    private static synchronized void startExpirationCheck() {
        if (scheduler == null || scheduler.isShutdown()) {
            scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleWithFixedDelay(
                    SignalStatusManager::checkAndRemoveExpiredStatuses, 1, 1, TimeUnit.SECONDS);
        }
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Returns a read-only snapshot of all currently active signal priority statuses.
     */
    public static List<SignalPriorityStatus> getSignalPriorityStatuses() {
        synchronized (SIGNAL_PRIORITY_STATUSES) {
            return Collections.unmodifiableList(new ArrayList<>(SIGNAL_PRIORITY_STATUSES));
        }
    }

    /**
     * Removes all stored signal priority statuses immediately without firing expiry callbacks.
     * Call this when leaving a geographic area to avoid unbounded memory growth.
     */
    public static void clear() {
        synchronized (SIGNAL_PRIORITY_STATUSES) {
            SIGNAL_PRIORITY_STATUSES.clear();
        }
        synchronized (SIGNAL_PRIORITY_STATUS_MAP) {
            SIGNAL_PRIORITY_STATUS_MAP.clear();
        }
    }

    // -------------------------------------------------------------------------
    // Key builder
    // -------------------------------------------------------------------------

    /** Key: {@code {sourceUuid}_{regionId}_{intersectionId}}. */
    static String buildKey(String sourceUuid, int regionId, int intersectionId) {
        return sourceUuid + "_" + regionId + "_" + intersectionId;
    }

    // -------------------------------------------------------------------------
    // Testing support (package-private)
    // -------------------------------------------------------------------------

    static IoT3SignalStatusCallback getCallbackForTesting() {
        return ioT3SignalStatusCallback;
    }

    static void checkAndRemoveExpiredStatusesForTesting() {
        checkAndRemoveExpiredStatuses();
    }

    static synchronized void resetForTesting() {
        synchronized (SIGNAL_PRIORITY_STATUSES) {
            SIGNAL_PRIORITY_STATUSES.clear();
        }
        synchronized (SIGNAL_PRIORITY_STATUS_MAP) {
            SIGNAL_PRIORITY_STATUS_MAP.clear();
        }
        ioT3SignalStatusCallback = null;
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
            scheduler = null;
        }
    }
}

