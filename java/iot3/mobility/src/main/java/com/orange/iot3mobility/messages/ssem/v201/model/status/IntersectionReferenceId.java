/*
 Copyright 2016-2026 Orange

 This software is distributed under the MIT license, see LICENSE.txt file for more details.

 @author Mathieu LEFEBVRE <mathieu1.lefebvre@orange.com>
 @generated GitHub Copilot (Claude Sonnet 4.6)
 */
package com.orange.iot3mobility.messages.ssem.v201.model.status;

/**
 * Provides a unique mapping to the intersection map in question.
 *
 * @param region Optional. Regional identifier [0..65535].
 * @param id     Required. Intersection ID [0..65535].
 */
public record IntersectionReferenceId(Integer region, int id) {}

