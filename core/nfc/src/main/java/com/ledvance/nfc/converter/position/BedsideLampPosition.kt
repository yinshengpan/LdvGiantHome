package com.ledvance.nfc.converter.position

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/21/25 14:40
 * Describe : BedsideLampPosition
 */
internal class BedsideLampPosition {
    data object DeviceSN : Position(0x10, 0x13)

    data object TotalPower : Position(0x0020, 0x0021)
    data object SetTripCurrent : Position(0x0024, 0x0027)
    data object L1 : Position(0x0028, 0x0029)
    data object L2 : Position(0x002A, 0x002B)
    data object L1Voltage : Position(0x002C, 0x002D)
    data object L2Voltage : Position(0x002E, 0x002F)
    data object L1Power : Position(0x0030, 0x0031)
    data object L2Power : Position(0x0032, 0x0033)

    data object Crc8 : Position(0x0034, 0x0034)
    data object Crc8CalculationRange : Position(0x0020, 0x0033)
    data object FaultCode : Position(0x00FC, 0x00FE)
    data object FaultCodeCrc8 : Position(0x00FF, 0x00FF)
    data object A1A2BodyRange : Position(0x0000, 0x00FF)
    data object A2ContentRange : Position(0x0020, 0x0037)
    data object A2StartIndex : Position(0x0020, 0x0020)
    data object A3StartIndex : Position(0x0100, 0x0100)
}