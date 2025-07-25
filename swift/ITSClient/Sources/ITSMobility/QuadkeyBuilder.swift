/*
 * Software Name : ITSClient
 * SPDX-FileCopyrightText: Copyright (c) Orange SA
 * SPDX-License-Identifier: MIT
 *
 * This software is distributed under the MIT license,
 * see the "LICENSE.txt" file for more details or https://opensource.org/license/MIT/
 *
 * Software description: Swift ITS client.
 */

import Foundation

// Computes a quadkey from coordinates for a zoom level and computes the neigbors of quadkey.
// Reference: https://learn.microsoft.com/en-us/bingmaps/articles/bing-maps-tile-system
struct QuadkeyBuilder {
    private struct Tile {
        let x: Int
        let y: Int
    }

    func quadkeyFrom(latitude: Double, longitude: Double, zoomLevel: Int, separator: String = "") -> String {
        let numberOfTiles = pow(2.0, Double(zoomLevel))
        let x = Int((longitude + 180) / 360.0 * numberOfTiles)
        let radianLatitude = latitude * .pi / 180.0
        let y = Int((1.0 - log(tan(radianLatitude) + 1.0 / cos(radianLatitude)) / .pi) / 2.0 * numberOfTiles)

        return quadkey(from: Tile(x: x, y: y), zoomLevel: zoomLevel, separator: separator)
    }

    func neighborQuadkeys(for quadkey: String, separator: String = "") -> [String] {
        var quadkeys = [String]()
        let sanitizedQuadkey = quadkey.replacingOccurrences(of: separator, with: "")
        let tile = tile(from: sanitizedQuadkey)
        let zoomLevel = sanitizedQuadkey.count
        let maxTileValue = Int(pow(2.0, Double(zoomLevel))) - 1
        let westXValue = tile.x > 0 ? tile.x - 1 : maxTileValue
        let eastXValue = tile.x < maxTileValue ? tile.x + 1 : 0

        // North-west
        if tile.y > 0 {
            quadkeys.append(self.quadkey(from: Tile(x: westXValue, y: tile.y - 1),
                                         zoomLevel: zoomLevel,
                                         separator: separator))
        }
        // North
        if tile.y > 0 {
            quadkeys.append(self.quadkey(from: Tile(x: tile.x, y: tile.y - 1),
                                         zoomLevel: zoomLevel,
                                         separator: separator))
        }
        // North-east
        if tile.y > 0 {
            quadkeys.append(self.quadkey(from: Tile(x: eastXValue, y: tile.y - 1),
                                         zoomLevel: zoomLevel,
                                         separator: separator))
        }
        // West
        quadkeys.append(self.quadkey(from: Tile(x: westXValue, y: tile.y),
                                     zoomLevel: zoomLevel,
                                     separator: separator))
        // East
        quadkeys.append(self.quadkey(from: Tile(x: eastXValue, y: tile.y),
                                     zoomLevel: zoomLevel,
                                     separator: separator))
        // South-west
        if tile.y < maxTileValue {
            quadkeys.append(self.quadkey(from: Tile(x: westXValue, y: tile.y + 1),
                                         zoomLevel: zoomLevel,
                                         separator: separator))
        }
        // South
        if tile.y < maxTileValue {
            quadkeys.append(self.quadkey(from: Tile(x: tile.x, y: tile.y + 1),
                                         zoomLevel: zoomLevel,
                                         separator: separator))
        }
        // South-east
        if tile.y < maxTileValue {
            quadkeys.append(self.quadkey(from: Tile(x: eastXValue, y: tile.y + 1),
                                         zoomLevel: zoomLevel,
                                         separator: separator))
        }

        return quadkeys
    }

    private func quadkey(from tile: Tile, zoomLevel: Int, separator: String = "") -> String {
        var quadkey = ""

        for i in stride(from: zoomLevel, to: 0, by: -1) {
            var digit = 0
            let mask = 1 << (i - 1)
            if (tile.x & mask) != 0 {
                digit += 1
            }
            if (tile.y & mask) != 0 {
                digit += 2
            }
            quadkey += String(digit)
        }

        return quadkey.map { String($0) }.joined(separator: separator)
    }

    private func tile(from quadkey: String) -> Tile {
        var x = 0
        var y = 0
        let zoomLevel = quadkey.count

        for i in stride(from: zoomLevel, to: 0, by: -1) {
            let mask = 1 << (i - 1)
            let index = quadkey.index(quadkey.startIndex, offsetBy: zoomLevel - i)
            let digit = quadkey[index].wholeNumberValue
            switch digit {
            case 1:
                x |= mask
            case 2:
                y |= mask
            case 3:
                x |= mask
                y |= mask
            default:
                break
            }
        }

        return Tile(x: x, y: y)
    }
}
