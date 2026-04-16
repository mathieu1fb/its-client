/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 */
package com.orange.iot3mobility.messages.mapem.v200.model.lane;

import java.util.List;

/**
 * Optional attribute set attached to a node point in a lane path.
 *
 * @param localNode Optional. Attribute states pertaining to this specific node point (node_attribute_xy strings).
 * @param disabled Optional. Segment attributes disabled at this node point (segment_attribute_xy strings).
 * @param enabled Optional. Segment attributes enabled at this node point and staying enabled (segment_attribute_xy strings).
 * @param data Optional. Attributes that carry additional numeric data values.
 * @param dWidth Optional. Width delta from this node onward, in cm (offset_b16). Value of zero must not be used.
 * @param dElevation Optional. Elevation delta from this node onward, in 10 cm steps (offset_b16).
 */
public record NodeAttributes(
        List<String> localNode,
        List<String> disabled,
        List<String> enabled,
        List<NodeAttributeData> data,
        Integer dWidth,
        Integer dElevation) {
}

