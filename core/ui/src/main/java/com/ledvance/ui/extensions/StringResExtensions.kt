package com.ledvance.ui.extensions

import com.ledvance.domain.bean.command.giant.DeviceMicRhythm
import com.ledvance.domain.bean.command.giant.ModeId
import com.ledvance.domain.bean.command.giant.scenes.FloorScenes
import com.ledvance.domain.bean.command.giant.scenes.Scene
import com.ledvance.domain.bean.command.giant.scenes.TableScenes
import com.ledvance.ui.R
import com.ledvance.utils.extensions.getString
import java.time.DayOfWeek

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/24/26 14:23
 * Describe : StringResourceExtensions
 */
fun DayOfWeek.getFullNameResId(): Int {
    return when (this) {
        DayOfWeek.MONDAY -> R.string.monday
        DayOfWeek.TUESDAY -> R.string.tuesday
        DayOfWeek.WEDNESDAY -> R.string.wednesday
        DayOfWeek.THURSDAY -> R.string.thursday
        DayOfWeek.FRIDAY -> R.string.friday
        DayOfWeek.SATURDAY -> R.string.saturday
        DayOfWeek.SUNDAY -> R.string.sunday
    }
}

fun DayOfWeek.getShortNameResId(): Int {
    return when (this) {
        DayOfWeek.MONDAY -> R.string.mon
        DayOfWeek.TUESDAY -> R.string.tue
        DayOfWeek.WEDNESDAY -> R.string.wed
        DayOfWeek.THURSDAY -> R.string.thu
        DayOfWeek.FRIDAY -> R.string.fri
        DayOfWeek.SATURDAY -> R.string.sat
        DayOfWeek.SUNDAY -> R.string.sun
    }
}

fun Set<DayOfWeek>.toDisplayText(): String {
    if (isEmpty()) return getString(R.string.never)
    if (size == 7) return getString(R.string.everyday)
    return this
        .sortedBy { it.value } // 周一~周日排序
        .joinToString(",") {
            getString(it.getShortNameResId())
        }
}

fun Scene.getNameResId(): Int {
    return when (this) {
        FloorScenes.Sunrise -> R.string.scene_sunrise          // 日出
        FloorScenes.Sunset -> R.string.scene_sunset            // 日落
        FloorScenes.Birthday -> R.string.scene_birthday        // 生日
        FloorScenes.Candlelight -> R.string.scene_candlelight  // 烛光
        FloorScenes.Fireworks -> R.string.scene_fireworks      // 烟花
        FloorScenes.Party -> R.string.scene_party              // 聚会
        FloorScenes.Appointment -> R.string.scene_appointment  // 约会
        FloorScenes.StarrySky -> R.string.scene_starry_sky     // 星空
        FloorScenes.Romantic -> R.string.scene_romantic        // 浪漫
        FloorScenes.Disco -> R.string.scene_disco              // 迪斯科
        FloorScenes.Rainbow -> R.string.scene_rainbow          // 彩虹
        FloorScenes.Film -> R.string.scene_film                // 电影
        FloorScenes.ChristmasEve -> R.string.scene_christmas_eve // 圣诞夜
        FloorScenes.FlowingWater -> R.string.scene_flowing_water // 流水
        FloorScenes.Sleep -> R.string.scene_sleep              // 睡眠
        FloorScenes.Ocean -> R.string.scene_ocean              // 海洋
        FloorScenes.Forest -> R.string.scene_forest            // 森林
        FloorScenes.Read -> R.string.scene_read                // 阅读
        FloorScenes.Work -> R.string.scene_work                // 工作
        FloorScenes.Colorful -> R.string.scene_colorful        // 炫彩
        FloorScenes.Soft -> R.string.scene_soft                // 柔和
        FloorScenes.WeddingDay -> R.string.scene_wedding_day   // 结婚纪念日
        FloorScenes.Snowflake -> R.string.scene_snowflake      // 雪花
        FloorScenes.Flame -> R.string.scene_flame              // 火焰
        FloorScenes.Lightning -> R.string.scene_lightning      // 闪电
        FloorScenes.ValentinesDay -> R.string.scene_valentines_day // 情人节
        FloorScenes.Halloween -> R.string.scene_halloween      // 万圣节
        FloorScenes.Alert -> R.string.scene_alert              // 警报
        FloorScenes.TimeMachine -> R.string.scene_time_machine // 时光机
        FloorScenes.TimeMachine2 -> R.string.scene_time_machine_2 // 时光机2
        FloorScenes.Meteor -> R.string.scene_meteor            // 流星
        FloorScenes.Meteor2 -> R.string.scene_meteor_2         // 流星2
        FloorScenes.FireworksShow -> R.string.scene_fireworks_show // 烟花秀

        // 自然类场景
        TableScenes.SpringBreeze -> R.string.scene_spring_breeze      // 春风
        TableScenes.Sky -> R.string.scene_sky                          // 天空
        TableScenes.Firefly -> R.string.scene_firefly                  // 萤火虫
        TableScenes.CherryBlossom -> R.string.scene_cherry_blossom     // 樱花
        TableScenes.MountainForest -> R.string.scene_mountain_forest   // 山林
        TableScenes.Lake -> R.string.scene_lake                        // 湖
        TableScenes.Forest -> R.string.scene_forest                    // 森林
        TableScenes.Aurora -> R.string.scene_aurora                    // 极光
        TableScenes.Flame -> R.string.scene_flame                      // 火焰
        TableScenes.Rainbow -> R.string.scene_rainbow                  // 彩虹
        TableScenes.StarrySky -> R.string.scene_starry_sky             // 星空
        TableScenes.Lightning -> R.string.scene_lightning              // 闪电
        TableScenes.Meteor -> R.string.scene_meteor                    // 流星
        TableScenes.SeaWave -> R.string.scene_sea_wave                 // 海浪
        TableScenes.RipplingWheat -> R.string.scene_rippling_wheat     // 麦浪
        TableScenes.Snowflake -> R.string.scene_snowflake              // 雪花
        TableScenes.Ripples -> R.string.scene_ripples                  // 涟漪
        TableScenes.Ocean -> R.string.scene_ocean                      // 海洋
        TableScenes.Rain -> R.string.scene_rain                        // 雨

        // 节日类场景
        TableScenes.Christmas -> R.string.scene_christmas              // 圣诞节
        TableScenes.Halloween -> R.string.scene_halloween              // 万圣节
        TableScenes.Easter -> R.string.scene_easter                    // 复活节
        TableScenes.ValentinesDay -> R.string.scene_valentines_day     // 情人节
        TableScenes.Carnival -> R.string.scene_carnival                // 狂欢节

        // 氛围类场景
        TableScenes.Sunrise -> R.string.scene_sunrise                  // 日出
        TableScenes.Sunset -> R.string.scene_sunset                    // 日落
        TableScenes.Candlelight -> R.string.scene_candlelight          // 烛光
        TableScenes.Romantic -> R.string.scene_romantic                // 浪漫
        TableScenes.Soft -> R.string.scene_soft                        // 柔和
        TableScenes.Dreamy -> R.string.scene_dreamy                    // 梦幻
        TableScenes.Accompany -> R.string.scene_accompany              // 陪伴
        TableScenes.Heal -> R.string.scene_heal                        // 治愈
        TableScenes.ComfortAble -> R.string.scene_comfortable          // 安慰的
        TableScenes.Feather -> R.string.scene_feather                  // 羽毛

        // 生活类场景
        TableScenes.Work -> R.string.scene_work                        // 工作
        TableScenes.Read -> R.string.scene_read                        // 阅读
        TableScenes.Sleep -> R.string.scene_sleep                      // 睡眠
        TableScenes.Happy -> R.string.scene_happy                      // 快乐
        TableScenes.Colorful -> R.string.scene_colorful                // 炫彩
        TableScenes.Party -> R.string.scene_party                      // 聚会
        TableScenes.Disco -> R.string.scene_disco                      // 迪斯科
        TableScenes.WeddingDay -> R.string.scene_wedding_day           // 结婚纪念日
        TableScenes.KitchenAroma -> R.string.scene_kitchen_aroma       // 厨房香气
        else -> R.string.scene_sunrise
    }
}


fun DeviceMicRhythm.getNameResId(): Int {
    return when (this) {
        DeviceMicRhythm.Energy -> R.string.music_device_mic_energy    // 能量模式1
        DeviceMicRhythm.Energy1 -> R.string.music_device_mic_energy_1    // 能量模式1
        DeviceMicRhythm.Energy2 -> R.string.music_device_mic_energy_2    // 能量模式2
        DeviceMicRhythm.Rhythm -> R.string.music_device_mic_rhythm    // 律动模式1
        DeviceMicRhythm.Rhythm1 -> R.string.music_device_mic_rhythm_1    // 律动模式1
        DeviceMicRhythm.Rhythm2 -> R.string.music_device_mic_rhythm_2    // 律动模式2
        DeviceMicRhythm.Spectrum1 -> R.string.music_device_mic_spectrum_1// 频谱模式1
        DeviceMicRhythm.Spectrum2 -> R.string.music_device_mic_spectrum_2// 频谱模式2
        DeviceMicRhythm.Roll1 -> R.string.music_device_mic_roll_1        // 滚动模式1
        DeviceMicRhythm.Roll2 -> R.string.music_device_mic_roll_2        // 滚动模式2
    }
}

fun ModeId.getNameResId(): Int {
    return when (this) {
        // ========== Base 基础灯光效果 ==========
        ModeId.AutomaticCycle -> R.string.light_effect_automatic_cycle
        ModeId.PositiveIllusion -> R.string.light_effect_positive_illusion
        ModeId.ReverseIllusion -> R.string.light_effect_reverse_illusion
        ModeId.ColorfulEnergy -> R.string.light_effect_colorful_energy
        ModeId.ColorfulJumpingTransformation -> R.string.light_effect_colorful_jumping_transformation
        ModeId.RedGreenBlueJump -> R.string.light_effect_red_green_blue_jump
        ModeId.HuangQingziJumpingTransformation -> R.string.light_effect_huang_qingzi_jumping_transformation
        ModeId.SevenColorStrobeLight -> R.string.light_effect_seven_color_strobe_light
        ModeId.RedGreenBlueStrobeLight -> R.string.light_effect_red_green_blue_strobe_light
        ModeId.YellowGreenPurpleFlicker -> R.string.light_effect_yellow_green_purple_flicker
        ModeId.SevenColorGradient -> R.string.light_effect_seven_color_gradient
        ModeId.RedAndYellowAlternatingGradient -> R.string.light_effect_red_and_yellow_alternating_gradient
        ModeId.RedPurpleAlternatingGradient -> R.string.light_effect_red_purple_alternating_gradient
        ModeId.GreenYellowAlternatingGradient -> R.string.light_effect_green_yellow_alternating_gradient
        ModeId.BluePurpleAlternatingGradient -> R.string.light_effect_blue_purple_alternating_gradient
        ModeId.RedRacehorse -> R.string.light_effect_red_racehorse
        ModeId.GreenRacehorse -> R.string.light_effect_green_racehorse
        ModeId.BlueRacehorse -> R.string.light_effect_blue_racehorse
        ModeId.YellowRacehorse -> R.string.light_effect_yellow_racehorse
        ModeId.CyanRacehorse -> R.string.light_effect_cyan_racehorse
        ModeId.PurpleRacehorse -> R.string.light_effect_purple_racehorse
        ModeId.WhiteRacehorse -> R.string.light_effect_white_racehorse
        ModeId.PositiveRainbowChasingLight -> R.string.light_effect_positive_rainbow_chasing_light
        ModeId.ReverseRainbowChasingLight -> R.string.light_effect_reverse_rainbow_chasing_light
        ModeId.PositiveRedGreenBlueChasingLight -> R.string.light_effect_positive_red_green_blue_chasing_light
        ModeId.ReverseRedGreenBlueChasingLight -> R.string.light_effect_reverse_red_green_blue_chasing_light
        ModeId.PositiveYellowCyanPurpleChasingLight -> R.string.light_effect_positive_yellow_cyan_purple_chasing_light
        ModeId.ReverseYellowCyanPurpleChasingLight -> R.string.light_effect_reverse_yellow_cyan_purple_chasing_light
        ModeId.PositiveColorfulFluttering -> R.string.light_effect_positive_colorful_fluttering
        ModeId.ReverseColorfulFluttering -> R.string.light_effect_reverse_colorful_fluttering
        ModeId.PositiveRedGreenBlueFluttering -> R.string.light_effect_positive_red_green_blue_fluttering
        ModeId.ReverseRedGreenBlueFluttering -> R.string.light_effect_reverse_red_green_blue_fluttering
        ModeId.PositiveYellowCyanPurpleFluttering -> R.string.light_effect_positive_yellow_cyan_purple_fluttering
        ModeId.ReverseYellowCyanPurpleFluttering -> R.string.light_effect_reverse_yellow_cyan_purple_fluttering
        ModeId.PositiveRainbowPainting -> R.string.light_effect_positive_rainbow_painting
        ModeId.ReverseRainbowPainting -> R.string.light_effect_reverse_rainbow_painting
        ModeId.PositiveRedGreenBluePainting -> R.string.light_effect_positive_red_green_blue_painting
        ModeId.ReverseRedGreenBluePainting -> R.string.light_effect_reverse_red_green_blue_painting
        ModeId.PositiveYellowCyanPurplePainting -> R.string.light_effect_positive_yellow_cyan_purple_painting
        ModeId.ReverseYellowCyanPurplePainting -> R.string.light_effect_reverse_yellow_cyan_purple_painting
        ModeId.ColorfulPaintingClosingCeremony -> R.string.light_effect_colorful_painting_closing_ceremony
        ModeId.ColorfulSaladScreenBrushing -> R.string.light_effect_colorful_salad_screen_brushing
        ModeId.RedGreenBluePaintingClosingCeremony -> R.string.light_effect_red_green_blue_painting_closing_ceremony
        ModeId.RedGreenBlueSaladScreenBrushing -> R.string.light_effect_red_green_blue_salad_screen_brushing
        ModeId.YellowCyanPurplePaintingClosingCeremony -> R.string.light_effect_yellow_cyan_purple_painting_closing_ceremony
        ModeId.YellowCyanPurpleSaladScreenBrushing -> R.string.light_effect_yellow_cyan_purple_salad_screen_brushing

        // ========== 窗帘/闭幕式相关 ==========
        ModeId.ColorfulClosingCeremony -> R.string.light_effect_colorful_closing_ceremony
        ModeId.ColorfulCurtainPulling -> R.string.light_effect_colorful_curtain_pulling
        ModeId.RedGreenBlueClosingCeremony -> R.string.light_effect_red_green_blue_closing_ceremony
        ModeId.RedGreenBlueCurtains -> R.string.light_effect_red_green_blue_curtains
        ModeId.HuangQingziClosingCeremony -> R.string.light_effect_huang_qingzi_closing_ceremony
        ModeId.HuangQingziPulledCurtain -> R.string.light_effect_huang_qingzi_pulled_curtain
        ModeId.RedClosing -> R.string.light_effect_red_closing
        ModeId.RedCurtainPulling -> R.string.light_effect_red_curtain_pulling
        ModeId.GreenClosing -> R.string.light_effect_green_closing
        ModeId.GreenCurtainPulling -> R.string.light_effect_green_curtain_pulling
        ModeId.BlueClosing -> R.string.light_effect_blue_closing
        ModeId.BlueCurtainPulling -> R.string.light_effect_blue_curtain_pulling
        ModeId.YellowClosing -> R.string.light_effect_yellow_closing
        ModeId.YellowCurtainPulling -> R.string.light_effect_yellow_curtain_pulling
        ModeId.CyanClosing -> R.string.light_effect_cyan_closing
        ModeId.CyanCurtainPulling -> R.string.light_effect_cyan_curtain_pulling
        ModeId.PurpleClosing -> R.string.light_effect_purple_closing
        ModeId.PurpleCurtainPulling -> R.string.light_effect_purple_curtain_pulling
        ModeId.WhiteClosing -> R.string.light_effect_white_closing
        ModeId.WhiteCurtainPulling -> R.string.light_effect_white_curtain_pulling

        // ========== 明暗过渡相关 ==========
        ModeId.PositiveSevenColorLightDarkTransition -> R.string.light_effect_positive_seven_color_light_dark_transition
        ModeId.ReverseSevenColorLightDarkTransition -> R.string.light_effect_reverse_seven_color_light_dark_transition
        ModeId.PositiveRedGreenBlueLightDarkTransition -> R.string.light_effect_positive_red_green_blue_light_dark_transition
        ModeId.ReverseRedGreenBlueLightDarkTransition -> R.string.light_effect_reverse_red_green_blue_light_dark_transition
        ModeId.PositiveYellowCyanPurpleLightDarkTransition -> R.string.light_effect_positive_yellow_cyan_purple_light_dark_transition
        ModeId.ReverseYellowCyanPurpleLightDarkTransition -> R.string.light_effect_reverse_yellow_cyan_purple_light_dark_transition
        ModeId.PositiveSixColorLightDarkTransitionRed -> R.string.light_effect_positive_six_color_light_dark_transition_red
        ModeId.ReverseSixColorLightDarkTransitionRed -> R.string.light_effect_reverse_six_color_light_dark_transition_red
        ModeId.PositiveSixColorLightDarkTransitionGreen -> R.string.light_effect_positive_six_color_light_dark_transition_green
        ModeId.ReverseSixColorLightDarkTransitionGreen -> R.string.light_effect_reverse_six_color_light_dark_transition_green
        ModeId.PositiveSixColorLightDarkTransitionBlue -> R.string.light_effect_positive_six_color_light_dark_transition_blue
        ModeId.ReverseSixColorLightDarkTransitionBlue -> R.string.light_effect_reverse_six_color_light_dark_transition_blue
        ModeId.PositiveSixColorLightDarkTransitionCyan -> R.string.light_effect_positive_six_color_light_dark_transition_cyan
        ModeId.ReverseSixColorLightDarkTransitionCyan -> R.string.light_effect_reverse_six_color_light_dark_transition_cyan
        ModeId.PositiveSixColorLightDarkTransitionYellow -> R.string.light_effect_positive_six_color_light_dark_transition_yellow
        ModeId.ReverseSixColorLightDarkTransitionYellow -> R.string.light_effect_reverse_six_color_light_dark_transition_yellow
        ModeId.PositiveSixColorLightDarkTransitionPurple -> R.string.light_effect_positive_six_color_light_dark_transition_purple
        ModeId.ReverseSixColorLightDarkTransitionPurple -> R.string.light_effect_reverse_six_color_light_dark_transition_purple
        ModeId.PositiveSixColorLightDarkTransitionWhite -> R.string.light_effect_positive_six_color_light_dark_transition_white
        ModeId.ReverseSixColorLightDarkTransitionWhite -> R.string.light_effect_reverse_six_color_light_dark_transition_white

        // ========== 流水效果相关 ==========
        ModeId.PositiveRainbowFlowingWater -> R.string.light_effect_positive_rainbow_flowing_water
        ModeId.ReverseRainbowFlowingWater -> R.string.light_effect_reverse_rainbow_flowing_water
        ModeId.ForwardFlowingRedGreenBlueWater -> R.string.light_effect_forward_flowing_red_green_blue_water
        ModeId.ReverseRedGreenBlueFlowingWater -> R.string.light_effect_reverse_red_green_blue_flowing_water
        ModeId.ForwardFlowingYellowCyanPurpleWater -> R.string.light_effect_forward_flowing_yellow_cyan_purple_water
        ModeId.ReverseYellowCyanPurpleFlowingWater -> R.string.light_effect_reverse_yellow_cyan_purple_flowing_water
        ModeId.ForwardFlowingRedGreenWater -> R.string.light_effect_forward_flowing_red_green_water
        ModeId.ReverseRedGreenFlowingWater -> R.string.light_effect_reverse_red_green_flowing_water
        ModeId.ForwardFlowingGreenBlueWater -> R.string.light_effect_forward_flowing_green_blue_water
        ModeId.ReverseGreenBlueFlowingWater -> R.string.light_effect_reverse_green_blue_flowing_water
        ModeId.ForwardFlowingYellowBlueWater -> R.string.light_effect_forward_flowing_yellow_blue_water
        ModeId.ReverseYellowBlueFlowingWater -> R.string.light_effect_reverse_yellow_blue_flowing_water
        ModeId.ForwardFlowingYellowCyanWater -> R.string.light_effect_forward_flowing_yellow_cyan_water
        ModeId.ReverseYellowCyanFlowingWater -> R.string.light_effect_reverse_yellow_cyan_flowing_water
        ModeId.ForwardFlowingCyanPurpleWater -> R.string.light_effect_forward_flowing_cyan_purple_water
        ModeId.ReverseCyanPurpleFlowingWater -> R.string.light_effect_reverse_cyan_purple_flowing_water
        ModeId.ForwardFlowingBlackWhiteWater -> R.string.light_effect_forward_flowing_black_white_water
        ModeId.ReverseBlackWhiteFlowingWater -> R.string.light_effect_reverse_black_white_flowing_water

        // ========== 白彩交替流水 ==========
        ModeId.PositiveFlowWhiteRedWhite -> R.string.light_effect_positive_flow_white_red_white
        ModeId.ReverseFlowWhiteRedWhite -> R.string.light_effect_reverse_flow_white_red_white
        ModeId.PositiveFlowWhiteGreenWhite -> R.string.light_effect_positive_flow_white_green_white
        ModeId.ReverseFlowWhiteGreenWhite -> R.string.light_effect_reverse_flow_white_green_white
        ModeId.PositiveFlowWhiteBlueWhite -> R.string.light_effect_positive_flow_white_blue_white
        ModeId.ReverseFlowWhiteBlueWhite -> R.string.light_effect_reverse_flow_white_blue_white
        ModeId.PositiveFlowWhiteYellowWhite -> R.string.light_effect_positive_flow_white_yellow_white
        ModeId.ReverseFlowWhiteYellowWhite -> R.string.light_effect_reverse_flow_white_yellow_white
        ModeId.PositiveFlowWhiteCyanWhite -> R.string.light_effect_positive_flow_white_cyan_white
        ModeId.ReverseFlowWhiteCyanWhite -> R.string.light_effect_reverse_flow_white_cyan_white
        ModeId.PositiveFlowWhitePurpleWhite -> R.string.light_effect_positive_flow_white_purple_white
        ModeId.ReverseFlowWhitePurpleWhite -> R.string.light_effect_reverse_flow_white_purple_white
        ModeId.PositiveFlowRedWhiteRed -> R.string.light_effect_positive_flow_red_white_red
        ModeId.ReverseFlowRedWhiteRed -> R.string.light_effect_reverse_flow_red_white_red
        ModeId.PositiveFlowGreenWhiteGreen -> R.string.light_effect_positive_flow_green_white_green
        ModeId.ReverseFlowGreenWhiteGreen -> R.string.light_effect_reverse_flow_green_white_green
        ModeId.PositiveFlowBlueWhiteBlue -> R.string.light_effect_positive_flow_blue_white_blue
        ModeId.ReverseFlowBlueWhiteBlue -> R.string.light_effect_reverse_flow_blue_white_blue
        ModeId.PositiveFlowYellowWhiteYellow -> R.string.light_effect_positive_flow_yellow_white_yellow
        ModeId.ReverseFlowYellowWhiteYellow -> R.string.light_effect_reverse_flow_yellow_white_yellow
        ModeId.PositiveFlowCyanWhiteCyan -> R.string.light_effect_positive_flow_cyan_white_cyan
        ModeId.ReverseFlowCyanWhiteCyan -> R.string.light_effect_reverse_flow_cyan_white_cyan
        ModeId.PositiveFlowPurpleWhitePurple -> R.string.light_effect_positive_flow_purple_white_purple
        ModeId.ReverseFlowPurpleWhitePurple -> R.string.light_effect_reverse_flow_purple_white_purple

        // ========== 彩色尾迹 ==========
        ModeId.PositiveRainbowTail -> R.string.light_effect_positive_rainbow_tail
        ModeId.ReverseRainbowTail -> R.string.light_effect_reverse_rainbow_tail
        ModeId.PositiveRedTail -> R.string.light_effect_positive_red_tail
        ModeId.ReverseRedTail -> R.string.light_effect_reverse_red_tail
        ModeId.PositiveGreenTail -> R.string.light_effect_positive_green_tail
        ModeId.ReverseGreenTail -> R.string.light_effect_reverse_green_tail
        ModeId.PositiveBlueTail -> R.string.light_effect_positive_blue_tail
        ModeId.ReverseBlueTail -> R.string.light_effect_reverse_blue_tail
        ModeId.PositiveYellowTail -> R.string.light_effect_positive_yellow_tail
        ModeId.ReverseYellowTail -> R.string.light_effect_reverse_yellow_tail
        ModeId.PositiveCyanTail -> R.string.light_effect_positive_cyan_tail
        ModeId.ReverseCyanTail -> R.string.light_effect_reverse_cyan_tail
        ModeId.PositivePurpleTail -> R.string.light_effect_positive_purple_tail
        ModeId.ReversePurpleTail -> R.string.light_effect_reverse_purple_tail
        ModeId.PositiveWhiteTail -> R.string.light_effect_positive_white_tail
        ModeId.ReverseWhiteTail -> R.string.light_effect_reverse_white_tail

        // ========== 正向跑动效果 ==========
        ModeId.RunningForwardRed -> R.string.light_effect_running_forward_red
        ModeId.RunningForwardGreen -> R.string.light_effect_running_forward_green
        ModeId.RunningForwardBlue -> R.string.light_effect_running_forward_blue
        ModeId.RunningForwardYellow -> R.string.light_effect_running_forward_yellow
        ModeId.RunningForwardCyan -> R.string.light_effect_running_forward_cyan
        ModeId.RunningForwardPurple -> R.string.light_effect_running_forward_purple
        ModeId.RunningForwardWhite -> R.string.light_effect_running_forward_white
        ModeId.RunningForwardSevenColors -> R.string.light_effect_running_forward_seven_colors
        ModeId.RunningForwardRedBlueGreen -> R.string.light_effect_running_forward_red_blue_green
        ModeId.RunningForwardYellowPurpleCyan -> R.string.light_effect_running_forward_yellow_purple_cyan
        ModeId.RunningForwardBluePurpleCyanYellow -> R.string.light_effect_running_forward_blue_purple_cyan_yellow
        ModeId.RunningForwardBlueGreenCyanYellow -> R.string.light_effect_running_forward_blue_green_cyan_yellow
        ModeId.RunningForwardWhiteBackgroundRedDots -> R.string.light_effect_running_forward_white_background_red_dots
        ModeId.RunningForwardRedBackgroundGreenDots14 -> R.string.light_effect_running_forward_red_background_green_dots14
        ModeId.RunningForwardGreenBackgroundBlueDots -> R.string.light_effect_running_forward_green_background_blue_dots
        ModeId.RunningForwardBlueBackgroundYellowDots -> R.string.light_effect_running_forward_blue_background_yellow_dots
        ModeId.RunningForwardYellowBackgroundCyanDots17 -> R.string.light_effect_running_forward_yellow_background_cyan_dots17
        ModeId.RunningForwardCyanBackgroundPurpleDots -> R.string.light_effect_running_forward_cyan_background_purple_dots
        ModeId.RunningForwardPurpleBackgroundWhiteDots -> R.string.light_effect_running_forward_purple_background_white_dots
        ModeId.RunningForwardRedBackgroundWhiteDots -> R.string.light_effect_running_forward_red_background_white_dots
        ModeId.RunningForwardRedBackgroundSevenColors -> R.string.light_effect_running_forward_red_background_seven_colors
        ModeId.RunningForwardGreenBackgroundSevenColors -> R.string.light_effect_running_forward_green_background_seven_colors
        ModeId.RunningForwardBlueBackgroundSevenColors -> R.string.light_effect_running_forward_blue_background_seven_colors
        ModeId.RunningForwardYellowBackgroundSevenColors -> R.string.light_effect_running_forward_yellow_background_seven_colors
        ModeId.RunningForwardCyanBackgroundSevenColors -> R.string.light_effect_running_forward_cyan_background_seven_colors
        ModeId.RunningForwardPurpleBackgroundSevenColors -> R.string.light_effect_running_forward_purple_background_seven_colors
        ModeId.RunningForwardWhiteBackgroundSevenColors -> R.string.light_effect_running_forward_white_background_seven_colors
        ModeId.RunningForwardBlueBackgroundGreenDots -> R.string.light_effect_running_forward_blue_background_green_dots
        ModeId.RunningForwardRedBackgroundGreenDots -> R.string.light_effect_running_forward_red_background_green_dots
        ModeId.RunningForwardBlueBackgroundRedDots -> R.string.light_effect_running_forward_blue_background_red_dots
        ModeId.RunningForwardYellowBackgroundCyanDots -> R.string.light_effect_running_forward_yellow_background_cyan_dots
        ModeId.RunningForwardPurpleBackgroundYellowDots -> R.string.light_effect_running_forward_purple_background_yellow_dots
        ModeId.RunningForwardYellowBackgroundWhiteDots -> R.string.light_effect_running_forward_yellow_background_white_dots
        ModeId.RunningForwardWhiteBackgroundYellowDots -> R.string.light_effect_running_forward_white_background_yellow_dots

        // ========== 反向跑动效果 ==========
        ModeId.ReverseRunningRed -> R.string.light_effect_reverse_running_red
        ModeId.ReverseRunningGreen -> R.string.light_effect_reverse_running_green
        ModeId.ReverseRunningBlue -> R.string.light_effect_reverse_running_blue
        ModeId.ReverseRunningYellow -> R.string.light_effect_reverse_running_yellow
        ModeId.ReverseRunningCyan -> R.string.light_effect_reverse_running_cyan
        ModeId.ReverseRunningPurple -> R.string.light_effect_reverse_running_purple
        ModeId.ReverseRunningWhite -> R.string.light_effect_reverse_running_white
        ModeId.ReverseRunningSevenColors -> R.string.light_effect_reverse_running_seven_colors
        ModeId.ReverseRunningRedBlueGreen -> R.string.light_effect_reverse_running_red_blue_green
        ModeId.ReverseRunningYellowPurpleCyan -> R.string.light_effect_reverse_running_yellow_purple_cyan
        ModeId.ReverseRunningBluePurpleCyanYellow -> R.string.light_effect_reverse_running_blue_purple_cyan_yellow
        ModeId.ReverseRunningBlueGreenCyanYellow -> R.string.light_effect_reverse_running_blue_green_cyan_yellow
        ModeId.ReverseRunningWhiteBackgroundRedDots -> R.string.light_effect_reverse_running_white_background_red_dots
        ModeId.ReverseRunningRedBackgroundGreenDots14 -> R.string.light_effect_reverse_running_red_background_green_dots14
        ModeId.ReverseRunningGreenBackgroundBlueDots -> R.string.light_effect_reverse_running_green_background_blue_dots
        ModeId.ReverseRunningBlueBackgroundYellowDots -> R.string.light_effect_reverse_running_blue_background_yellow_dots
        ModeId.ReverseRunningYellowBackgroundCyanDots17 -> R.string.light_effect_reverse_running_yellow_background_cyan_dots17
        ModeId.ReverseRunningCyanBackgroundPurpleDots -> R.string.light_effect_reverse_running_cyan_background_purple_dots
        ModeId.ReverseRunningPurpleBackgroundWhiteDots -> R.string.light_effect_reverse_running_purple_background_white_dots
        ModeId.ReverseRunningRedBackgroundWhiteDots -> R.string.light_effect_reverse_running_red_background_white_dots
        ModeId.ReverseRunningRedBackgroundSevenColors -> R.string.light_effect_reverse_running_red_background_seven_colors
        ModeId.ReverseRunningGreenBackgroundSevenColors -> R.string.light_effect_reverse_running_green_background_seven_colors
        ModeId.ReverseRunningBlueBackgroundSevenColors -> R.string.light_effect_reverse_running_blue_background_seven_colors
        ModeId.ReverseRunningYellowBackgroundSevenColors -> R.string.light_effect_reverse_running_yellow_background_seven_colors
        ModeId.ReverseRunningCyanBackgroundSevenColors -> R.string.light_effect_reverse_running_cyan_background_seven_colors
        ModeId.ReverseRunningPurpleBackgroundSevenColors -> R.string.light_effect_reverse_running_purple_background_seven_colors
        ModeId.ReverseRunningWhiteBackgroundSevenColors -> R.string.light_effect_reverse_running_white_background_seven_colors
        ModeId.ReverseRunningBlueBackgroundGreenDots -> R.string.light_effect_reverse_running_blue_background_green_dots
        ModeId.ReverseRunningRedBackgroundGreenDots -> R.string.light_effect_reverse_running_red_background_green_dots
        ModeId.ReverseRunningBlueBackgroundRedDots -> R.string.light_effect_reverse_running_blue_background_red_dots
        ModeId.ReverseRunningYellowBackgroundCyanDots -> R.string.light_effect_reverse_running_yellow_background_cyan_dots
        ModeId.ReverseRunningPurpleBackgroundYellowDots -> R.string.light_effect_reverse_running_purple_background_yellow_dots
        ModeId.ReverseRunningYellowBackgroundWhiteDots -> R.string.light_effect_reverse_running_yellow_background_white_dots
        ModeId.ReverseRunningWhiteBackgroundYellowDots -> R.string.light_effect_reverse_running_white_background_yellow_dots

        // ========== 特殊效果/堆叠/过渡 ==========
        ModeId.SpecialSevenColorsOpening25PM -> R.string.light_effect_special_seven_colors_opening_25_pm
        ModeId.OrangeRedDarkBrightDarkFilteredFlowingWater -> R.string.light_effect_orange_red_dark_bright_dark_filtered_flowing_water
        ModeId.YellowGreenDarkBrightDarkFilteredFlowingWater -> R.string.light_effect_yellow_green_dark_bright_dark_filtered_flowing_water
        ModeId.GreenDarkBrightDarkFilteredFlowingWater -> R.string.light_effect_green_dark_bright_dark_filtered_flowing_water
        ModeId.CyanBlueDarkBrightDarkFilteredFlowingWater -> R.string.light_effect_cyan_blue_dark_bright_dark_filtered_flowing_water
        ModeId.BlueDarkBrightDarkFilteredFlowingWater -> R.string.light_effect_blue_dark_bright_dark_filtered_flowing_water
        ModeId.PurpleDarkBrightDarkFilteredFlowingWater -> R.string.light_effect_purple_dark_bright_dark_filtered_flowing_water
        ModeId.RedDarkBrightDarkFilteredFlowingWater -> R.string.light_effect_red_dark_bright_dark_filtered_flowing_water
        ModeId.SevenColorStacking -> R.string.light_effect_seven_color_stacking
        ModeId.OrangeStacking -> R.string.light_effect_orange_stacking
        ModeId.YellowGreenStacking -> R.string.light_effect_yellow_green_stacking
        ModeId.GreenStacking -> R.string.light_effect_green_stacking
        ModeId.CyanBlueStacking -> R.string.light_effect_cyan_blue_stacking
        ModeId.BlueStacking -> R.string.light_effect_blue_stacking
        ModeId.PurpleStacking -> R.string.light_effect_purple_stacking
        ModeId.RedStacking -> R.string.light_effect_red_stacking
        ModeId.ColorfulGradient -> R.string.light_effect_colorful_gradient
        ModeId.ColorfulTransition -> R.string.light_effect_colorful_transition
        ModeId.RedPurpleTransition -> R.string.light_effect_red_purple_transition
        ModeId.YellowWhiteTransition -> R.string.light_effect_yellow_white_transition
        ModeId.YellowOrangeTransition -> R.string.light_effect_yellow_orange_transition
    }
}