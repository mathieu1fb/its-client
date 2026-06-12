/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 4.6)
 */
package com.orange.iot3mobility.managers;

import com.orange.iot3mobility.messages.EtsiConverter;
import com.orange.iot3mobility.messages.srem.SremHelper;
import com.orange.iot3mobility.messages.srem.core.SremCodec;
import com.orange.iot3mobility.messages.srem.core.SremVersion;
import com.orange.iot3mobility.messages.srem.v201.model.SremEnvelope201;
import com.orange.iot3mobility.messages.srem.v201.model.SremMessage201;
import com.orange.iot3mobility.messages.srem.v201.model.request.IntersectionAccessPoint;
import com.orange.iot3mobility.messages.srem.v201.model.request.Position3D;
import com.orange.iot3mobility.messages.srem.v201.model.request.SignalRequest;
import com.orange.iot3mobility.messages.srem.v201.model.request.SignalRequestPackage;
import com.orange.iot3mobility.quadkey.LatLng;
import com.orange.iot3mobility.roadobjects.PriorityAccessPoint;
import com.orange.iot3mobility.roadobjects.PriorityRequestType;
import com.orange.iot3mobility.roadobjects.RoadIntersection;
import com.orange.iot3mobility.roadobjects.SignalController;
import com.orange.iot3mobility.roadobjects.SignalPriorityRequest;

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
 * Manages {@link SignalPriorityRequest} objects derived from received SREM messages.
 * <p>
 * One {@link SignalPriorityRequest} is maintained per {@code (sourceUuid, stationId, requestId)}
 * triplet. A single SREM that carries multiple {@code requests} entries produces a separate object
 * for each entry. Objects expire on a rolling
 * {@link SignalPriorityRequest#LIFETIME_MS}-millisecond timeout.
 * <p>
 * State is static — there is one shared store per JVM process.
 */
public class SignalRequestManager {

    private static final Logger LOGGER = Logger.getLogger(SignalRequestManager.class.getName());
    private static final String TAG = "IoT3Mobility.SignalRequestManager";

    private static final ArrayList<SignalPriorityRequest> SIGNAL_PRIORITY_REQUESTS = new ArrayList<>();
    private static final HashMap<String, SignalPriorityRequest> SIGNAL_PRIORITY_REQUEST_MAP = new HashMap<>();

    private static IoT3SignalRequestCallback ioT3SignalRequestCallback;
    private static ScheduledExecutorService scheduler;

    // -------------------------------------------------------------------------
    // Init
    // -------------------------------------------------------------------------

    /**
     * Registers the callback and starts the expiry scheduler (idempotent).
     *
     * @param callback the callback to receive lifecycle events
     */
    public static void init(IoT3SignalRequestCallback callback) {
        SignalRequestManager.ioT3SignalRequestCallback = callback;
        startExpirationCheck();
    }

    // -------------------------------------------------------------------------
    // SREM processing
    // -------------------------------------------------------------------------

    /**
     * Decodes a raw SREM JSON payload and updates the signal priority request store.
     *
     * @param message   raw JSON string received from the broker
     * @param sremHelper helper used to decode the payload
     */
    public static void processSrem(String message, SremHelper sremHelper) {
        if (ioT3SignalRequestCallback == null) return;
        try {
            SremCodec.SremFrame<?> sremFrame = sremHelper.parse(message);
            ioT3SignalRequestCallback.sremArrived(sremFrame);

            if (sremFrame.version() == SremVersion.V2_0_1) {
                SremEnvelope201 envelope = (SremEnvelope201) sremFrame.envelope();
                processSremEnvelope201(envelope, sremFrame);
            }
        } catch (IOException ioException) {
            LOGGER.log(Level.WARNING, TAG + " SREM parsing error: " + ioException);
        } catch (RuntimeException runtimeException) {
            LOGGER.log(Level.WARNING, TAG + " SREM processing error: " + runtimeException);
        }
    }

    private static void processSremEnvelope201(SremEnvelope201 envelope,
                                               SremCodec.SremFrame<?> sremFrame) {
        SremMessage201 msg = envelope.message();
        String sourceUuid = envelope.sourceUuid();
        long stationId = msg.stationId();

        // Resolve requestor position once — shared across all request packages
        LatLng requestorPosition = resolvePosition(msg);

        // A single SREM can carry multiple request packages (up to 32)
        List<SignalRequestPackage> packages = msg.requests();
        if (packages == null || packages.isEmpty()) return;

        synchronized (SIGNAL_PRIORITY_REQUEST_MAP) {
            for (SignalRequestPackage pkg : packages) {
                SignalRequest req = pkg.request();
                int requestId = req.requestId();
                int regionId = req.id().region() != null ? req.id().region() : 0;
                int intersectionId = req.id().id();
                String key = buildKey(sourceUuid, stationId, requestId);

                SignalPriorityRequest existing = SIGNAL_PRIORITY_REQUEST_MAP.get(key);
                if (existing == null) {
                    PriorityAccessPoint inboundLane = toLaneAccessPoint(req.inboundLane());
                    PriorityRequestType requestType = PriorityRequestType.fromValue(req.requestType());
                    SignalPriorityRequest newRequest = new SignalPriorityRequest(
                            key, sourceUuid, stationId, requestId, requestType,
                            regionId, intersectionId, inboundLane,
                            requestorPosition, sremFrame);
                    SIGNAL_PRIORITY_REQUEST_MAP.put(key, newRequest);
                    synchronized (SIGNAL_PRIORITY_REQUESTS) {
                        SIGNAL_PRIORITY_REQUESTS.add(newRequest);
                    }
                    tryResolveNewRequest(newRequest);
                    ioT3SignalRequestCallback.newSignalPriorityRequest(newRequest);
                } else {
                    existing.update(sremFrame);
                    ioT3SignalRequestCallback.signalPriorityRequestUpdated(existing);
                }
            }
        }
    }

    /**
     * Attempts to resolve positions on a newly created request from already-available
     * MAPEM and SPATEM data. Called immediately after creation.
     */
    private static void tryResolveNewRequest(SignalPriorityRequest request) {
        for (RoadIntersection intersection : RoadGeometryManager.getRoadIntersections()) {
            if (request.resolveIntersectionRefPoint(intersection)) break;
        }
        for (SignalController controller : SignalControllerManager.getSignalControllers()) {
            if (request.resolveSignalGroupPosition(controller)) break;
        }
    }

    /**
     * Called by {@link RoadGeometryManager} when a {@link RoadIntersection} is created or updated.
     * Finds every {@link SignalPriorityRequest} targeting that intersection and resolves its
     * intersection reference point; fires {@code signalPriorityRequestUpdated} for each resolved object.
     *
     * @param roadIntersection the newly available intersection
     */
    public static void tryResolvePositionsFromIntersection(RoadIntersection roadIntersection) {
        synchronized (SIGNAL_PRIORITY_REQUESTS) {
            for (SignalPriorityRequest request : SIGNAL_PRIORITY_REQUESTS) {
                if (request.resolveIntersectionRefPoint(roadIntersection)) {
                    if (ioT3SignalRequestCallback != null) {
                        ioT3SignalRequestCallback.signalPriorityRequestUpdated(request);
                    }
                }
            }
        }
    }

    /**
     * Called by {@link SignalControllerManager} when a {@link SignalController} is created or its
     * signal group positions are resolved from MAPEM data.
     * Finds every {@link SignalPriorityRequest} targeting that intersection and attempts to resolve
     * its inbound signal group position; fires {@code signalPriorityRequestUpdated} for each.
     *
     * @param signalController the newly available or updated signal controller
     */
    public static void tryResolvePositionsFromController(SignalController signalController) {
        synchronized (SIGNAL_PRIORITY_REQUESTS) {
            for (SignalPriorityRequest request : SIGNAL_PRIORITY_REQUESTS) {
                if (request.resolveSignalGroupPosition(signalController)) {
                    if (ioT3SignalRequestCallback != null) {
                        ioT3SignalRequestCallback.signalPriorityRequestUpdated(request);
                    }
                }
            }
        }
    }

    /**
     * Converts a SREM model {@link IntersectionAccessPoint} to the version-agnostic
     * {@link PriorityAccessPoint} road object type.
     */
    private static PriorityAccessPoint toLaneAccessPoint(IntersectionAccessPoint ap) {
        if (ap == null) return null;
        return new PriorityAccessPoint(ap.lane(), ap.approach(), ap.connection());
    }

    /**
     * Attempts to decode the requestor position from DSRC units to WGS-84 degrees.
     * Returns {@code null} if the SREM carries no position.
     */
    private static LatLng resolvePosition(SremMessage201 msg) {
        if (msg.requestor() == null || msg.requestor().position() == null) return null;
        Position3D pos = msg.requestor().position().position();
        if (pos == null) return null;
        return new LatLng(
                EtsiConverter.latitudeDegrees(pos.lat()),
                EtsiConverter.longitudeDegrees(pos.lon()));
    }

    // -------------------------------------------------------------------------
    // Expiry
    // -------------------------------------------------------------------------

    private static void checkAndRemoveExpiredRequests() {
        synchronized (SIGNAL_PRIORITY_REQUESTS) {
            Iterator<SignalPriorityRequest> iterator = SIGNAL_PRIORITY_REQUESTS.iterator();
            while (iterator.hasNext()) {
                SignalPriorityRequest request = iterator.next();
                if (!request.stillLiving()) {
                    iterator.remove();
                    synchronized (SIGNAL_PRIORITY_REQUEST_MAP) {
                        SIGNAL_PRIORITY_REQUEST_MAP.values().remove(request);
                    }
                    if (ioT3SignalRequestCallback != null) {
                        ioT3SignalRequestCallback.signalPriorityRequestExpired(request);
                    }
                }
            }
        }
    }

    private static synchronized void startExpirationCheck() {
        if (scheduler == null || scheduler.isShutdown()) {
            scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleWithFixedDelay(
                    SignalRequestManager::checkAndRemoveExpiredRequests, 1, 1, TimeUnit.SECONDS);
        }
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Returns a read-only snapshot of all currently active signal priority requests.
     */
    public static List<SignalPriorityRequest> getSignalPriorityRequests() {
        synchronized (SIGNAL_PRIORITY_REQUESTS) {
            return Collections.unmodifiableList(new ArrayList<>(SIGNAL_PRIORITY_REQUESTS));
        }
    }

    /**
     * Removes all stored signal priority requests immediately without firing expiry callbacks.
     * Call this when leaving a geographic area to avoid unbounded memory growth.
     */
    public static void clear() {
        synchronized (SIGNAL_PRIORITY_REQUESTS) {
            SIGNAL_PRIORITY_REQUESTS.clear();
        }
        synchronized (SIGNAL_PRIORITY_REQUEST_MAP) {
            SIGNAL_PRIORITY_REQUEST_MAP.clear();
        }
    }

    // -------------------------------------------------------------------------
    // Key builder
    // -------------------------------------------------------------------------

    /** Key: {@code {sourceUuid}_{stationId}_{requestId}}. */
    static String buildKey(String sourceUuid, long stationId, int requestId) {
        return sourceUuid + "_" + stationId + "_" + requestId;
    }

    // -------------------------------------------------------------------------
    // Testing support (package-private)
    // -------------------------------------------------------------------------

    static IoT3SignalRequestCallback getCallbackForTesting() {
        return ioT3SignalRequestCallback;
    }

    static void checkAndRemoveExpiredRequestsForTesting() {
        checkAndRemoveExpiredRequests();
    }

    static synchronized void resetForTesting() {
        synchronized (SIGNAL_PRIORITY_REQUESTS) {
            SIGNAL_PRIORITY_REQUESTS.clear();
        }
        synchronized (SIGNAL_PRIORITY_REQUEST_MAP) {
            SIGNAL_PRIORITY_REQUEST_MAP.clear();
        }
        ioT3SignalRequestCallback = null;
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
            scheduler = null;
        }
    }
}

