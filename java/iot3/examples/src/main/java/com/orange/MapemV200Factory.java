package com.orange;

import com.orange.iot3mobility.TrueTime;
import com.orange.iot3mobility.messages.EtsiConverter;
import com.orange.iot3mobility.messages.mapem.v200.model.MapemEnvelope200;
import com.orange.iot3mobility.messages.mapem.v200.model.MapemMessage200;
import com.orange.iot3mobility.messages.mapem.v200.model.intersection.IntersectionGeometry;
import com.orange.iot3mobility.messages.mapem.v200.model.intersection.IntersectionReferenceId;
import com.orange.iot3mobility.messages.mapem.v200.model.lane.GenericLane;
import com.orange.iot3mobility.messages.mapem.v200.model.lane.LaneAttributes;
import com.orange.iot3mobility.messages.mapem.v200.model.lane.LaneType;
import com.orange.iot3mobility.messages.mapem.v200.model.lane.NodeDelta;
import com.orange.iot3mobility.messages.mapem.v200.model.lane.NodeList;
import com.orange.iot3mobility.messages.mapem.v200.model.lane.NodeXY;
import com.orange.iot3mobility.messages.mapem.v200.model.lane.NodeXYOffset;
import com.orange.iot3mobility.messages.mapem.v200.model.shared.Position3D;
import com.orange.iot3mobility.quadkey.LatLng;

import java.util.List;

/**
 * Factory producing a minimal MAPEM v2.0.0 test message.
 * <p>
 * The generated message describes a fictitious intersection near the given reference position,
 * with one ingress lane and one egress lane, each defined by 3 node_xy offset points.
 */
final class MapemV200Factory {

    private static final int PROTOCOL_VERSION = 2;
    private static final long STATION_ID = 654321L;
    private static final int MSG_ISSUE_REVISION = 0;
    private static final int REGION_ID = 10;
    private static final int INTERSECTION_ID = 1001;
    private static final int INTERSECTION_REVISION = 1;
    private static final int LANE_WIDTH_CM = 350;

    private MapemV200Factory() {
        // Factory class
    }

    /**
     * Build a minimal MAPEM v2.0.0 envelope with one intersection at the given position.
     *
     * @param sourceUuid the source UUID of the emitting station
     * @param position   the geographic reference point for the intersection
     * @return the constructed {@link MapemEnvelope200}
     */
    static MapemEnvelope200 createTestMapemEnvelope(String sourceUuid, LatLng position) {
        Position3D refPoint = Position3D.builder()
                .latitude(EtsiConverter.latitudeEtsi(position.getLatitude()))
                .longitude(EtsiConverter.longitudeEtsi(position.getLongitude()))
                .build();

        // Ingress lane (lane 1): 3 nodes offset southward from ref point (negative y, in cm)
        GenericLane ingressLane = GenericLane.builder()
                .laneId(1)
                .ingressApproach(1)
                .laneAttributes(LaneAttributes.builder()
                        .directionalUse(List.of("ingressPath"))
                        .sharedWith(List.of())
                        .laneType(new LaneType(List.of(), null, null, null, null, null, null, null))
                        .build())
                .nodeList(new NodeList(List.of(
                        NodeXY.builder().delta(new NodeDelta(new NodeXYOffset(0, -1500), null)).build(),
                        NodeXY.builder().delta(new NodeDelta(new NodeXYOffset(0, -1500), null)).build(),
                        NodeXY.builder().delta(new NodeDelta(new NodeXYOffset(0, -1500), null)).build()
                ), null))
                .build();

        // Egress lane (lane 2): 3 nodes offset northward from ref point (positive y), shifted east by 350 cm
        GenericLane egressLane = GenericLane.builder()
                .laneId(2)
                .egressApproach(1)
                .laneAttributes(LaneAttributes.builder()
                        .directionalUse(List.of("egressPath"))
                        .sharedWith(List.of())
                        .laneType(new LaneType(List.of(), null, null, null, null, null, null, null))
                        .build())
                .nodeList(new NodeList(List.of(
                        NodeXY.builder().delta(new NodeDelta(new NodeXYOffset(350, 1500), null)).build(),
                        NodeXY.builder().delta(new NodeDelta(new NodeXYOffset(0, 1500), null)).build(),
                        NodeXY.builder().delta(new NodeDelta(new NodeXYOffset(0, 1500), null)).build()
                ), null))
                .build();

        IntersectionGeometry intersection = IntersectionGeometry.builder()
                .id(new IntersectionReferenceId(REGION_ID, INTERSECTION_ID))
                .revision(INTERSECTION_REVISION)
                .refPoint(refPoint)
                .laneWidth(LANE_WIDTH_CM)
                .laneSet(List.of(ingressLane, egressLane))
                .build();

        return MapemEnvelope200.builder()
                .origin("self")
                .sourceUuid(sourceUuid)
                .timestamp(TrueTime.getAccurateTime())
                .message(MapemMessage200.builder()
                        .protocolVersion(PROTOCOL_VERSION)
                        .stationId(STATION_ID)
                        .msgIssueRevision(MSG_ISSUE_REVISION)
                        .intersections(List.of(intersection))
                        .build())
                .build();
    }
}

