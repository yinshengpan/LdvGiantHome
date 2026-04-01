package com.ledvance.domain.bean.command.giant

import com.ledvance.domain.bean.command.Command

/**
 * @athor : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2026/3/21 09:31
 * Describe : ModeId
 */
enum class ModeId(override val command: Byte) : Command {
    // ========== Base 基础灯光效果 ==========
    AutomaticCycle(0x00.toByte()),                          // 自动循环
    PositiveIllusion(0x01.toByte()),                        // 正向幻彩
    ReverseIllusion(0x02.toByte()),                         // 反向幻彩
    ColorfulEnergy(0xD4.toByte()),                          // 炫彩能量
    ColorfulJumpingTransformation(0xC2.toByte()),           // 炫彩跳变
    RedGreenBlueJump(0xC2.toByte()),                        // 红绿蓝跳变
    HuangQingziJumpingTransformation(0xC3.toByte()),        // 黄青紫跳变
    SevenColorStrobeLight(0xC4.toByte()),                   // 七彩爆闪
    RedGreenBlueStrobeLight(0xC5.toByte()),                 // 红绿蓝爆闪
    YellowGreenPurpleFlicker(0xC6.toByte()),                // 黄绿紫爆闪
    SevenColorGradient(0xC7.toByte()),                      // 七彩渐变
    RedAndYellowAlternatingGradient(0xC8.toByte()),         // 红黄交替渐变
    RedPurpleAlternatingGradient(0xC9.toByte()),            // 红紫交替渐变
    GreenYellowAlternatingGradient(0xCB.toByte()),          // 绿黄交替渐变
    BluePurpleAlternatingGradient(0xCC.toByte()),           // 蓝紫交替渐变
    RedRacehorse(0xCD.toByte()),                            // 红色跑马
    GreenRacehorse(0xCE.toByte()),                          // 绿色跑马
    BlueRacehorse(0xCF.toByte()),                           // 蓝色跑马
    YellowRacehorse(0xD0.toByte()),                         // 黄色跑马
    CyanRacehorse(0xD1.toByte()),                           // 青色跑马
    PurpleRacehorse(0xD2.toByte()),                         // 紫色跑马
    WhiteRacehorse(0xD3.toByte()),                          // 白色跑马
    PositiveRainbowChasingLight(0x4D.toByte()),             // 正向彩虹追逐
    ReverseRainbowChasingLight(0x4E.toByte()),              // 反向彩虹追逐
    PositiveRedGreenBlueChasingLight(0x4F.toByte()),        // 正向红绿蓝追逐
    ReverseRedGreenBlueChasingLight(0x50.toByte()),         // 反向红绿蓝追逐
    PositiveYellowCyanPurpleChasingLight(0x51.toByte()),    // 正向黄青紫追逐
    ReverseYellowCyanPurpleChasingLight(0x52.toByte()),     // 反向黄青紫追逐
    PositiveColorfulFluttering(0x53.toByte()),              // 正向炫彩飘移
    ReverseColorfulFluttering(0x54.toByte()),               // 反向炫彩飘移
    PositiveRedGreenBlueFluttering(0x55.toByte()),          // 正向红绿蓝飘移
    ReverseRedGreenBlueFluttering(0x56.toByte()),           // 反向红绿蓝飘移
    PositiveYellowCyanPurpleFluttering(0x57.toByte()),      // 正向黄青紫飘移
    ReverseYellowCyanPurpleFluttering(0x58.toByte()),       // 反向黄青紫飘移
    PositiveRainbowPainting(0xB5.toByte()),                 // 正向彩虹绘画
    ReverseRainbowPainting(0xB6.toByte()),                  // 反向彩虹绘画
    PositiveRedGreenBluePainting(0xB7.toByte()),            // 正向红绿蓝绘画
    ReverseRedGreenBluePainting(0xB8.toByte()),             // 反向红绿蓝绘画
    PositiveYellowCyanPurplePainting(0xB9.toByte()),        // 正向黄青紫绘画
    ReverseYellowCyanPurplePainting(0xBA.toByte()),         // 反向黄青紫绘画
    ColorfulPaintingClosingCeremony(0xBB.toByte()),         // 炫彩绘画闭幕
    ColorfulSaladScreenBrushing(0xBC.toByte()),             // 炫彩沙拉刷屏
    RedGreenBluePaintingClosingCeremony(0xBD.toByte()),     // 红绿蓝绘画闭幕
    RedGreenBlueSaladScreenBrushing(0xBE.toByte()),         // 红绿蓝沙拉刷屏
    YellowCyanPurplePaintingClosingCeremony(0xBF.toByte()), // 黄青紫绘画闭幕
    YellowCyanPurpleSaladScreenBrushing(0xC0.toByte()),     // 黄青紫沙拉刷屏

    // ========== 窗帘/闭幕式相关 ==========
    ColorfulClosingCeremony(0x39.toByte()),                 // 炫彩闭幕
    ColorfulCurtainPulling(0x3A.toByte()),                  // 炫彩拉幕
    RedGreenBlueClosingCeremony(0x3B.toByte()),             // 红绿蓝闭幕
    RedGreenBlueCurtains(0x3C.toByte()),                    // 红绿蓝拉幕
    HuangQingziClosingCeremony(0x3D.toByte()),              // 黄青紫闭幕
    HuangQingziPulledCurtain(0x3E.toByte()),                // 黄青紫拉幕
    RedClosing(0x3F.toByte()),                              // 红色闭幕
    RedCurtainPulling(0x40.toByte()),                       // 红色拉幕
    GreenClosing(0x41.toByte()),                            // 绿色闭幕
    GreenCurtainPulling(0x42.toByte()),                     // 绿色拉幕
    BlueClosing(0x43.toByte()),                             // 蓝色闭幕
    BlueCurtainPulling(0x44.toByte()),                      // 蓝色拉幕
    YellowClosing(0x45.toByte()),                           // 黄色闭幕
    YellowCurtainPulling(0x46.toByte()),                    // 黄色拉幕
    CyanClosing(0x47.toByte()),                             // 青色闭幕
    CyanCurtainPulling(0x48.toByte()),                      // 青色拉幕
    PurpleClosing(0x49.toByte()),                           // 紫色闭幕
    PurpleCurtainPulling(0x4A.toByte()),                    // 紫色拉幕
    WhiteClosing(0x4B.toByte()),                            // 白色闭幕
    WhiteCurtainPulling(0x4C.toByte()),                     // 白色拉幕

    // ========== 明暗过渡相关 ==========
    PositiveSevenColorLightDarkTransition(0x03.toByte()),   // 正向七彩明暗过渡
    ReverseSevenColorLightDarkTransition(0x04.toByte()),    // 反向七彩明暗过渡
    PositiveRedGreenBlueLightDarkTransition(0x05.toByte()), // 正向红绿蓝明暗过渡
    ReverseRedGreenBlueLightDarkTransition(0x06.toByte()),  // 反向红绿蓝明暗过渡
    PositiveYellowCyanPurpleLightDarkTransition(0x07.toByte()), // 正向黄青紫明暗过渡
    ReverseYellowCyanPurpleLightDarkTransition(0x08.toByte()),  // 反向黄青紫明暗过渡
    PositiveSixColorLightDarkTransitionRed(0x09.toByte()),  // 正向六色明暗过渡(红)
    ReverseSixColorLightDarkTransitionRed(0x0A.toByte()),   // 反向六色明暗过渡(红)
    PositiveSixColorLightDarkTransitionGreen(0x0B.toByte()), // 正向六色明暗过渡(绿)
    ReverseSixColorLightDarkTransitionGreen(0x0C.toByte()),  // 反向六色明暗过渡(绿)
    PositiveSixColorLightDarkTransitionBlue(0x0D.toByte()),  // 正向六色明暗过渡(蓝)
    ReverseSixColorLightDarkTransitionBlue(0x0E.toByte()),   // 反向六色明暗过渡(蓝)
    PositiveSixColorLightDarkTransitionCyan(0x0F.toByte()),  // 正向六色明暗过渡(青)
    ReverseSixColorLightDarkTransitionCyan(0x10.toByte()),   // 反向六色明暗过渡(青)
    PositiveSixColorLightDarkTransitionYellow(0x11.toByte()), // 正向六色明暗过渡(黄)
    ReverseSixColorLightDarkTransitionYellow(0x12.toByte()),  // 反向六色明暗过渡(黄)
    PositiveSixColorLightDarkTransitionPurple(0x13.toByte()), // 正向六色明暗过渡(紫)
    ReverseSixColorLightDarkTransitionPurple(0x14.toByte()),  // 反向六色明暗过渡(紫)
    PositiveSixColorLightDarkTransitionWhite(0x15.toByte()),  // 正向六色明暗过渡(白)
    ReverseSixColorLightDarkTransitionWhite(0x16.toByte()),   // 反向六色明暗过渡(白)

    // ========== 流水效果相关 ==========
    PositiveRainbowFlowingWater(0x27.toByte()),             // 正向彩虹流水
    ReverseRainbowFlowingWater(0x28.toByte()),              // 反向彩虹流水
    ForwardFlowingRedGreenBlueWater(0x29.toByte()),         // 正向红绿蓝流水
    ReverseRedGreenBlueFlowingWater(0x2A.toByte()),         // 反向红绿蓝流水
    ForwardFlowingYellowCyanPurpleWater(0x2B.toByte()),     // 正向黄青紫流水
    ReverseYellowCyanPurpleFlowingWater(0x2C.toByte()),     // 反向黄青紫流水
    ForwardFlowingRedGreenWater(0x2D.toByte()),             // 正向红绿流水
    ReverseRedGreenFlowingWater(0x2E.toByte()),             // 反向红绿流水
    ForwardFlowingGreenBlueWater(0x2F.toByte()),            // 正向绿蓝流水
    ReverseGreenBlueFlowingWater(0x30.toByte()),            // 反向绿蓝流水
    ForwardFlowingYellowBlueWater(0x31.toByte()),           // 正向黄蓝流水
    ReverseYellowBlueFlowingWater(0x32.toByte()),           // 反向黄蓝流水
    ForwardFlowingYellowCyanWater(0x33.toByte()),          // 正向黄青流水
    ReverseYellowCyanFlowingWater(0x34.toByte()),          // 反向黄青流水
    ForwardFlowingCyanPurpleWater(0x35.toByte()),           // 正向青紫流水
    ReverseCyanPurpleFlowingWater(0x36.toByte()),           // 反向青紫流水
    ForwardFlowingBlackWhiteWater(0x37.toByte()),           // 正向黑白流水
    ReverseBlackWhiteFlowingWater(0x38.toByte()),           // 反向黑白流水

    // ========== 白彩交替流水 ==========
    PositiveFlowWhiteRedWhite(0x8F.toByte()),               // 正向流白红白
    ReverseFlowWhiteRedWhite(0x90.toByte()),                // 反向流白红白
    PositiveFlowWhiteGreenWhite(0x91.toByte()),             // 正向流白绿白
    ReverseFlowWhiteGreenWhite(0x92.toByte()),              // 反向流白绿白
    PositiveFlowWhiteBlueWhite(0x93.toByte()),              // 正向流白蓝白
    ReverseFlowWhiteBlueWhite(0x94.toByte()),               // 反向流白蓝白
    PositiveFlowWhiteYellowWhite(0x95.toByte()),            // 正向流白黄白
    ReverseFlowWhiteYellowWhite(0x96.toByte()),             // 反向流白黄白
    PositiveFlowWhiteCyanWhite(0x97.toByte()),              // 正向流白青白
    ReverseFlowWhiteCyanWhite(0x98.toByte()),               // 反向流白青白
    PositiveFlowWhitePurpleWhite(0x99.toByte()),            // 正向流白紫白
    ReverseFlowWhitePurpleWhite(0x9A.toByte()),             // 反向流白紫白
    PositiveFlowRedWhiteRed(0x9B.toByte()),                 // 正向流红白红
    ReverseFlowRedWhiteRed(0x9C.toByte()),                  // 反向流红白红
    PositiveFlowGreenWhiteGreen(0x9D.toByte()),             // 正向流绿白绿
    ReverseFlowGreenWhiteGreen(0x9E.toByte()),              // 反向流绿白绿
    PositiveFlowBlueWhiteBlue(0x9F.toByte()),               // 正向流蓝白蓝
    ReverseFlowBlueWhiteBlue(0xA0.toByte()),                // 反向流蓝白蓝
    PositiveFlowYellowWhiteYellow(0xA1.toByte()),           // 正向流黄白黄
    ReverseFlowYellowWhiteYellow(0xA2.toByte()),            // 反向流黄白黄
    PositiveFlowCyanWhiteCyan(0xA3.toByte()),               // 正向流青白青
    ReverseFlowCyanWhiteCyan(0xA4.toByte()),                // 反向流青白青
    PositiveFlowPurpleWhitePurple(0xA5.toByte()),           // 正向流紫白紫
    ReverseFlowPurpleWhitePurple(0xA6.toByte()),            // 反向流紫白紫

    // ========== 彩色尾迹 ==========
    PositiveRainbowTail(0x17.toByte()),                     // 正向彩虹尾迹
    ReverseRainbowTail(0x18.toByte()),                      // 反向彩虹尾迹
    PositiveRedTail(0x19.toByte()),                         // 正向红色尾迹
    ReverseRedTail(0x1A.toByte()),                          // 反向红色尾迹
    PositiveGreenTail(0x1B.toByte()),                       // 正向绿色尾迹
    ReverseGreenTail(0x1C.toByte()),                        // 反向绿色尾迹
    PositiveBlueTail(0x1D.toByte()),                        // 正向蓝色尾迹
    ReverseBlueTail(0x1E.toByte()),                         // 反向蓝色尾迹
    PositiveYellowTail(0x1F.toByte()),                      // 正向黄色尾迹
    ReverseYellowTail(0x20.toByte()),                       // 反向黄色尾迹
    PositiveCyanTail(0x21.toByte()),                        // 正向青色尾迹
    ReverseCyanTail(0x22.toByte()),                         // 反向青色尾迹
    PositivePurpleTail(0x23.toByte()),                      // 正向紫色尾迹
    ReversePurpleTail(0x24.toByte()),                       // 反向紫色尾迹
    PositiveWhiteTail(0x25.toByte()),                       // 正向白色尾迹
    ReverseWhiteTail(0x26.toByte()),                        // 反向白色尾迹

    // ========== 正向跑动效果 ==========
    RunningForwardRed(0x59.toByte()),                       // 正向跑动-红
    RunningForwardGreen(0x5B.toByte()),                     // 正向跑动-绿
    RunningForwardBlue(0x5D.toByte()),                      // 正向跑动-蓝
    RunningForwardYellow(0x5F.toByte()),                    // 正向跑动-黄
    RunningForwardCyan(0x61.toByte()),                      // 正向跑动-青
    RunningForwardPurple(0x63.toByte()),                    // 正向跑动-紫
    RunningForwardWhite(0x65.toByte()),                     // 正向跑动-白
    RunningForwardSevenColors(0x67.toByte()),               // 正向跑动-七彩
    RunningForwardRedBlueGreen(0x69.toByte()),              // 正向跑动-红绿蓝
    RunningForwardYellowPurpleCyan(0x6B.toByte()),          // 正向跑动-黄青紫
    RunningForwardBluePurpleCyanYellow(0x6D.toByte()),       // 正向跑动-蓝紫青黄
    RunningForwardBlueGreenCyanYellow(0x6F.toByte()),       // 正向跑动-蓝绿青黄
    RunningForwardWhiteBackgroundRedDots(0x71.toByte()),    // 正向跑动-白底红点
    RunningForwardRedBackgroundGreenDots14(0x73.toByte()),  // 正向跑动-红底绿点14
    RunningForwardGreenBackgroundBlueDots(0x75.toByte()),   // 正向跑动-绿底蓝点
    RunningForwardBlueBackgroundYellowDots(0x77.toByte()),  // 正向跑动-蓝底黄点
    RunningForwardYellowBackgroundCyanDots17(0x79.toByte()), // 正向跑动-黄底青点17
    RunningForwardCyanBackgroundPurpleDots(0x7B.toByte()),  // 正向跑动-青底紫点
    RunningForwardPurpleBackgroundWhiteDots(0x7D.toByte()), // 正向跑动-紫底白点
    RunningForwardRedBackgroundWhiteDots(0x7F.toByte()),    // 正向跑动-红底白点
    RunningForwardRedBackgroundSevenColors(0x81.toByte()),  // 正向跑动-红底七彩
    RunningForwardGreenBackgroundSevenColors(0x83.toByte()), // 正向跑动-绿底七彩
    RunningForwardBlueBackgroundSevenColors(0x85.toByte()), // 正向跑动-蓝底七彩
    RunningForwardYellowBackgroundSevenColors(0x87.toByte()), // 正向跑动-黄底七彩
    RunningForwardCyanBackgroundSevenColors(0x89.toByte()), // 正向跑动-青底七彩
    RunningForwardPurpleBackgroundSevenColors(0x8B.toByte()), // 正向跑动-紫底七彩
    RunningForwardWhiteBackgroundSevenColors(0x8D.toByte()), // 正向跑动-白底七彩
    RunningForwardBlueBackgroundGreenDots(0xA7.toByte()),   // 正向跑动-蓝底绿点
    RunningForwardRedBackgroundGreenDots(0xA9.toByte()),    // 正向跑动-红底绿点
    RunningForwardBlueBackgroundRedDots(0xAB.toByte()),     // 正向跑动-蓝底红点
    RunningForwardYellowBackgroundCyanDots(0xAD.toByte()),  // 正向跑动-黄底青点
    RunningForwardPurpleBackgroundYellowDots(0xAF.toByte()), // 正向跑动-紫底黄点
    RunningForwardYellowBackgroundWhiteDots(0xB1.toByte()), // 正向跑动-黄底白点
    RunningForwardWhiteBackgroundYellowDots(0xB3.toByte()), // 正向跑动-白底黄点

    // ========== 反向跑动效果 ==========
    ReverseRunningRed(0x5A.toByte()),                       // 反向跑动-红
    ReverseRunningGreen(0x5C.toByte()),                     // 反向跑动-绿
    ReverseRunningBlue(0x5E.toByte()),                      // 反向跑动-蓝
    ReverseRunningYellow(0x60.toByte()),                    // 反向跑动-黄
    ReverseRunningCyan(0x62.toByte()),                      // 反向跑动-青
    ReverseRunningPurple(0x64.toByte()),                    // 反向跑动-紫
    ReverseRunningWhite(0x66.toByte()),                     // 反向跑动-白
    ReverseRunningSevenColors(0x68.toByte()),               // 反向跑动-七彩
    ReverseRunningRedBlueGreen(0x6A.toByte()),              // 反向跑动-红绿蓝
    ReverseRunningYellowPurpleCyan(0x6C.toByte()),          // 反向跑动-黄青紫
    ReverseRunningBluePurpleCyanYellow(0x6E.toByte()),      // 反向跑动-蓝紫青黄
    ReverseRunningBlueGreenCyanYellow(0x70.toByte()),       // 反向跑动-蓝绿青黄
    ReverseRunningWhiteBackgroundRedDots(0x72.toByte()),    // 反向跑动-白底红点
    ReverseRunningRedBackgroundGreenDots14(0x74.toByte()),  // 反向跑动-红底绿点14
    ReverseRunningGreenBackgroundBlueDots(0x76.toByte()),   // 反向跑动-绿底蓝点
    ReverseRunningBlueBackgroundYellowDots(0x78.toByte()),  // 反向跑动-蓝底黄点
    ReverseRunningYellowBackgroundCyanDots17(0x7A.toByte()), // 反向跑动-黄底青点17
    ReverseRunningCyanBackgroundPurpleDots(0x7C.toByte()),  // 反向跑动-青底紫点
    ReverseRunningPurpleBackgroundWhiteDots(0x7E.toByte()), // 反向跑动-紫底白点
    ReverseRunningRedBackgroundWhiteDots(0x80.toByte()),    // 反向跑动-红底白点
    ReverseRunningRedBackgroundSevenColors(0x82.toByte()),  // 反向跑动-红底七彩
    ReverseRunningGreenBackgroundSevenColors(0x84.toByte()), // 反向跑动-绿底七彩
    ReverseRunningBlueBackgroundSevenColors(0x86.toByte()), // 反向跑动-蓝底七彩
    ReverseRunningYellowBackgroundSevenColors(0x88.toByte()), // 反向跑动-黄底七彩
    ReverseRunningCyanBackgroundSevenColors(0x8A.toByte()), // 反向跑动-青底七彩
    ReverseRunningPurpleBackgroundSevenColors(0x8C.toByte()), // 反向跑动-紫底七彩
    ReverseRunningWhiteBackgroundSevenColors(0x8E.toByte()), // 反向跑动-白底七彩
    ReverseRunningBlueBackgroundGreenDots(0xA8.toByte()),   // 反向跑动-蓝底绿点
    ReverseRunningRedBackgroundGreenDots(0xAA.toByte()),    // 反向跑动-红底绿点
    ReverseRunningBlueBackgroundRedDots(0xAC.toByte()),     // 反向跑动-蓝底红点
    ReverseRunningYellowBackgroundCyanDots(0xAE.toByte()),  // 反向跑动-黄底青点
    ReverseRunningPurpleBackgroundYellowDots(0xB0.toByte()), // 反向跑动-紫底黄点
    ReverseRunningYellowBackgroundWhiteDots(0xB2.toByte()), // 反向跑动-黄底白点
    ReverseRunningWhiteBackgroundYellowDots(0xB4.toByte()), // 反向跑动-白底黄点

    // ========== 特殊效果/堆叠/过渡 ==========
    SpecialSevenColorsOpening25PM(0xD5.toByte()),           // 特殊-七彩开场25PM
    OrangeRedDarkBrightDarkFilteredFlowingWater(0xD6.toByte()), // 橙红暗亮暗过滤流水
    YellowGreenDarkBrightDarkFilteredFlowingWater(0xD7.toByte()), // 黄绿暗亮暗过滤流水
    GreenDarkBrightDarkFilteredFlowingWater(0xD8.toByte()), // 绿色暗亮暗过滤流水
    CyanBlueDarkBrightDarkFilteredFlowingWater(0xD9.toByte()), // 青蓝暗亮暗过滤流水
    BlueDarkBrightDarkFilteredFlowingWater(0xDA.toByte()),  // 蓝色暗亮暗过滤流水
    PurpleDarkBrightDarkFilteredFlowingWater(0xDB.toByte()), // 紫色暗亮暗过滤流水
    RedDarkBrightDarkFilteredFlowingWater(0xDC.toByte()),   // 红色暗亮暗过滤流水
    SevenColorStacking(0xDD.toByte()),                      // 七彩堆叠
    OrangeStacking(0xDE.toByte()),                          // 橙色堆叠
    YellowGreenStacking(0xDF.toByte()),                     // 黄绿堆叠
    GreenStacking(0xE0.toByte()),                           // 绿色堆叠
    CyanBlueStacking(0xE1.toByte()),                        // 青蓝堆叠
    BlueStacking(0xE2.toByte()),                            // 蓝色堆叠
    PurpleStacking(0xE3.toByte()),                          // 紫色堆叠
    RedStacking(0xE4.toByte()),                             // 红色堆叠
    ColorfulGradient(0xE5.toByte()),                        // 炫彩渐变
    ColorfulTransition(0xE6.toByte()),                      // 炫彩过渡
    RedPurpleTransition(0xE7.toByte()),                     // 红紫过渡
    YellowWhiteTransition(0xE8.toByte()),                   // 黄白过渡
    YellowOrangeTransition(0xE9.toByte());                  // 黄橙过渡


    companion object {

        fun fromInt(value: Int): ModeId? {
            // 将 Int 转为 Byte（自动截断高位，符合 Byte 取值范围 -128~127）
            val targetByte = value.toByte()
            // 遍历所有枚举项，匹配 command 字节值
            return entries.find { it.command == targetByte }
        }

        // 基本的枚举列表
        val baseItems = listOf(
            AutomaticCycle,
            PositiveIllusion,
            ReverseIllusion,
            ColorfulEnergy,
            ColorfulJumpingTransformation,
            RedGreenBlueJump,
            HuangQingziJumpingTransformation,
            SevenColorStrobeLight,
            RedGreenBlueStrobeLight,
            YellowGreenPurpleFlicker,
            SevenColorGradient,
            RedAndYellowAlternatingGradient,
            RedPurpleAlternatingGradient,
            GreenYellowAlternatingGradient,
            BluePurpleAlternatingGradient,
            RedRacehorse,
            GreenRacehorse,
            BlueRacehorse,
            YellowRacehorse,
            CyanRacehorse,
            PurpleRacehorse,
            WhiteRacehorse,
            PositiveRainbowChasingLight,
            ReverseRainbowChasingLight,
            PositiveRedGreenBlueChasingLight,
            ReverseRedGreenBlueChasingLight,
            PositiveYellowCyanPurpleChasingLight,
            ReverseYellowCyanPurpleChasingLight,
            PositiveColorfulFluttering,
            ReverseColorfulFluttering,
            PositiveRedGreenBlueFluttering,
            ReverseRedGreenBlueFluttering,
            PositiveYellowCyanPurpleFluttering,
            ReverseYellowCyanPurpleFluttering,
            PositiveRainbowPainting,
            ReverseRainbowPainting,
            PositiveRedGreenBluePainting,
            ReverseRedGreenBluePainting,
            PositiveYellowCyanPurplePainting,
            ReverseYellowCyanPurplePainting,
            ColorfulPaintingClosingCeremony,
            ColorfulSaladScreenBrushing,
            RedGreenBluePaintingClosingCeremony,
            RedGreenBlueSaladScreenBrushing,
            YellowCyanPurplePaintingClosingCeremony,
            YellowCyanPurpleSaladScreenBrushing
        )

        // 窗帘/闭幕式对应的枚举列表
        val curtainItems = listOf(
            ColorfulClosingCeremony,
            ColorfulCurtainPulling,
            RedGreenBlueClosingCeremony,
            RedGreenBlueCurtains,
            HuangQingziClosingCeremony,
            HuangQingziPulledCurtain,
            RedClosing,
            RedCurtainPulling,
            GreenClosing,
            GreenCurtainPulling,
            BlueClosing,
            BlueCurtainPulling,
            YellowClosing,
            YellowCurtainPulling,
            CyanClosing,
            CyanCurtainPulling,
            PurpleClosing,
            PurpleCurtainPulling,
            WhiteClosing,
            WhiteCurtainPulling
        )

        // 明暗过渡对应的枚举列表
        val transitionItems = listOf(
            PositiveSevenColorLightDarkTransition,
            ReverseSevenColorLightDarkTransition,
            PositiveRedGreenBlueLightDarkTransition,
            ReverseRedGreenBlueLightDarkTransition,
            PositiveYellowCyanPurpleLightDarkTransition,
            ReverseYellowCyanPurpleLightDarkTransition,
            PositiveSixColorLightDarkTransitionRed,
            ReverseSixColorLightDarkTransitionRed,
            PositiveSixColorLightDarkTransitionGreen,
            ReverseSixColorLightDarkTransitionGreen,
            PositiveSixColorLightDarkTransitionBlue,
            ReverseSixColorLightDarkTransitionBlue,
            PositiveSixColorLightDarkTransitionCyan,
            ReverseSixColorLightDarkTransitionCyan,
            PositiveSixColorLightDarkTransitionYellow,
            ReverseSixColorLightDarkTransitionYellow,
            PositiveSixColorLightDarkTransitionPurple,
            ReverseSixColorLightDarkTransitionPurple,
            PositiveSixColorLightDarkTransitionWhite,
            ReverseSixColorLightDarkTransitionWhite
        )

        // 流水效果对应的枚举列表
        val flowingWaterItems = listOf(
            PositiveRainbowFlowingWater,
            ReverseRainbowFlowingWater,
            ForwardFlowingRedGreenBlueWater,
            ReverseRedGreenBlueFlowingWater,
            ForwardFlowingYellowCyanPurpleWater,
            ReverseYellowCyanPurpleFlowingWater,
            ForwardFlowingRedGreenWater,
            ReverseRedGreenFlowingWater,
            ForwardFlowingGreenBlueWater,
            ReverseGreenBlueFlowingWater,
            ForwardFlowingYellowBlueWater,
            ReverseYellowBlueFlowingWater,
            ForwardFlowingYellowCyanWater,
            ReverseYellowCyanFlowingWater,
            ForwardFlowingCyanPurpleWater,
            ReverseCyanPurpleFlowingWater,
            ForwardFlowingBlackWhiteWater,
            ReverseBlackWhiteFlowingWater
        )

        // 白彩交替流水对应的枚举列表
        val flowItems = listOf(
            PositiveFlowWhiteRedWhite,
            ReverseFlowWhiteRedWhite,
            PositiveFlowWhiteGreenWhite,
            ReverseFlowWhiteGreenWhite,
            PositiveFlowWhiteBlueWhite,
            ReverseFlowWhiteBlueWhite,
            PositiveFlowWhiteYellowWhite,
            ReverseFlowWhiteYellowWhite,
            PositiveFlowWhiteCyanWhite,
            ReverseFlowWhiteCyanWhite,
            PositiveFlowWhitePurpleWhite,
            ReverseFlowWhitePurpleWhite,
            PositiveFlowRedWhiteRed,
            ReverseFlowRedWhiteRed,
            PositiveFlowGreenWhiteGreen,
            ReverseFlowGreenWhiteGreen,
            PositiveFlowBlueWhiteBlue,
            ReverseFlowBlueWhiteBlue,
            PositiveFlowYellowWhiteYellow,
            ReverseFlowYellowWhiteYellow,
            PositiveFlowCyanWhiteCyan,
            ReverseFlowCyanWhiteCyan,
            PositiveFlowPurpleWhitePurple,
            ReverseFlowPurpleWhitePurple
        )

        // 彩色尾迹对应的枚举列表
        val tailItems = listOf(
            PositiveRainbowTail,
            ReverseRainbowTail,
            PositiveRedTail,
            ReverseRedTail,
            PositiveGreenTail,
            ReverseGreenTail,
            PositiveBlueTail,
            ReverseBlueTail,
            PositiveYellowTail,
            ReverseYellowTail,
            PositiveCyanTail,
            ReverseCyanTail,
            PositivePurpleTail,
            ReversePurpleTail,
            PositiveWhiteTail,
            ReverseWhiteTail
        )

        // 正向跑动效果对应的枚举列表
        val runItems = listOf(
            RunningForwardRed,
            RunningForwardGreen,
            RunningForwardBlue,
            RunningForwardYellow,
            RunningForwardCyan,
            RunningForwardPurple,
            RunningForwardWhite,
            RunningForwardSevenColors,
            RunningForwardRedBlueGreen,
            RunningForwardYellowPurpleCyan,
            RunningForwardBluePurpleCyanYellow,
            RunningForwardBlueGreenCyanYellow,
            RunningForwardWhiteBackgroundRedDots,
            RunningForwardRedBackgroundGreenDots14,
            RunningForwardGreenBackgroundBlueDots,
            RunningForwardBlueBackgroundYellowDots,
            RunningForwardYellowBackgroundCyanDots17,
            RunningForwardCyanBackgroundPurpleDots,
            RunningForwardPurpleBackgroundWhiteDots,
            RunningForwardRedBackgroundWhiteDots,
            RunningForwardRedBackgroundSevenColors,
            RunningForwardGreenBackgroundSevenColors,
            RunningForwardBlueBackgroundSevenColors,
            RunningForwardYellowBackgroundSevenColors,
            RunningForwardCyanBackgroundSevenColors,
            RunningForwardPurpleBackgroundSevenColors,
            RunningForwardWhiteBackgroundSevenColors,
            RunningForwardBlueBackgroundGreenDots,
            RunningForwardRedBackgroundGreenDots,
            RunningForwardBlueBackgroundRedDots,
            RunningForwardYellowBackgroundCyanDots,
            RunningForwardPurpleBackgroundYellowDots,
            RunningForwardYellowBackgroundWhiteDots,
            RunningForwardWhiteBackgroundYellowDots
        )

        // 反向跑动效果对应的枚举列表
        val runBackItems = listOf(
            ReverseRunningRed,
            ReverseRunningGreen,
            ReverseRunningBlue,
            ReverseRunningYellow,
            ReverseRunningCyan,
            ReverseRunningPurple,
            ReverseRunningWhite,
            ReverseRunningSevenColors,
            ReverseRunningRedBlueGreen,
            ReverseRunningYellowPurpleCyan,
            ReverseRunningBluePurpleCyanYellow,
            ReverseRunningBlueGreenCyanYellow,
            ReverseRunningWhiteBackgroundRedDots,
            ReverseRunningRedBackgroundGreenDots14,
            ReverseRunningGreenBackgroundBlueDots,
            ReverseRunningBlueBackgroundYellowDots,
            ReverseRunningYellowBackgroundCyanDots17,
            ReverseRunningCyanBackgroundPurpleDots,
            ReverseRunningPurpleBackgroundWhiteDots,
            ReverseRunningRedBackgroundWhiteDots,
            ReverseRunningRedBackgroundSevenColors,
            ReverseRunningGreenBackgroundSevenColors,
            ReverseRunningBlueBackgroundSevenColors,
            ReverseRunningYellowBackgroundSevenColors,
            ReverseRunningCyanBackgroundSevenColors,
            ReverseRunningPurpleBackgroundSevenColors,
            ReverseRunningWhiteBackgroundSevenColors,
            ReverseRunningBlueBackgroundGreenDots,
            ReverseRunningRedBackgroundGreenDots,
            ReverseRunningBlueBackgroundRedDots,
            ReverseRunningYellowBackgroundCyanDots,
            ReverseRunningPurpleBackgroundYellowDots,
            ReverseRunningYellowBackgroundWhiteDots,
            ReverseRunningWhiteBackgroundYellowDots
        )

        // 特殊效果/堆叠/过渡对应的枚举列表
        val otherItems = listOf(
            SpecialSevenColorsOpening25PM,
            OrangeRedDarkBrightDarkFilteredFlowingWater,
            YellowGreenDarkBrightDarkFilteredFlowingWater,
            GreenDarkBrightDarkFilteredFlowingWater,
            CyanBlueDarkBrightDarkFilteredFlowingWater,
            BlueDarkBrightDarkFilteredFlowingWater,
            PurpleDarkBrightDarkFilteredFlowingWater,
            RedDarkBrightDarkFilteredFlowingWater,
            SevenColorStacking,
            OrangeStacking,
            YellowGreenStacking,
            GreenStacking,
            CyanBlueStacking,
            BlueStacking,
            PurpleStacking,
            RedStacking,
            ColorfulGradient,
            ColorfulTransition,
            RedPurpleTransition,
            YellowWhiteTransition,
            YellowOrangeTransition
        )
    }
}
