/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 4.6)
 */
package com.orange.iot3mobility.messages.srem.v201.model.request;

/**
 * Geographic position in DSRC Position3D format.
 *
 * @param lat       Required. Latitude in 1/10 micro-degree units [−900000000..900000001].
 * @param lon       Required. Longitude in 1/10 micro-degree units [−1800000000..1800000001].
 * @param elevation Optional. Elevation in decimetres above the WGS-84 ellipsoid [−4096..61439].
 */
public record Position3D(int lat, int lon, Integer elevation) {}

