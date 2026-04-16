/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 */
package com.orange.iot3mobility.messages.mapem.v200.model.lane;

import java.util.List;

/**
 * Connecting lane descriptor: links this lane to an outbound lane beyond the stop line.
 *
 * @param lane ID of the connecting (outbound) lane.
 * @param maneuver Optional. Allowed manoeuvres from this lane to the connecting lane (allowed_maneuvers strings).
 */
public record ConnectingLane(int lane, List<String> maneuver) {
}

