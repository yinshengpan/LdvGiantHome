package com.ledvance.light.bean

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2026/3/22 20:50
 * Describe : SceneResource
 */
import com.ledvance.domain.bean.command.scenes.FloorScenes
import com.ledvance.domain.bean.command.scenes.Scene
import com.ledvance.domain.bean.command.scenes.TableScenes
import com.ledvance.ui.R

class SceneResource {
}

fun Scene.getSceneIcon(): Int {
    return when (this) {
        is TableScenes -> {
            when (this) {
                TableScenes.SpringBreeze -> R.mipmap.spring_breeze_icon
                TableScenes.Sky -> R.mipmap.sky_icon
                TableScenes.Firefly -> R.mipmap.firefly_icon
                TableScenes.CherryBlossom -> R.mipmap.cherry_blossom_icon
                TableScenes.MountainForest -> R.mipmap.mountain_forest_icon
                TableScenes.Lake -> R.mipmap.lake_icon
                TableScenes.Forest -> R.mipmap.forest_icon
                TableScenes.Aurora -> R.mipmap.aurora_icon
                TableScenes.Flame -> R.mipmap.flame_icon
                TableScenes.Rainbow -> R.mipmap.rainbow_icon
                TableScenes.StarrySky -> R.mipmap.starry_sky_icon
                TableScenes.Lightning -> R.mipmap.lightning_icon
                TableScenes.Meteor -> R.mipmap.meteor_icon
                TableScenes.SeaWave -> R.mipmap.sea_wave_icon
                TableScenes.RipplingWheat -> R.mipmap.rippling_wheat_icon
                TableScenes.Snowflake -> R.mipmap.snowflake_icon
                TableScenes.Ripples -> R.mipmap.ripples_icon
                TableScenes.Ocean -> R.mipmap.ocean_icon
                TableScenes.Rain -> R.mipmap.rain_icon
                TableScenes.Christmas -> R.mipmap.christmas_eve_icon
                TableScenes.Halloween -> R.mipmap.halloween_icon
                TableScenes.Easter -> R.mipmap.easter_icon
                TableScenes.ValentinesDay -> R.mipmap.valentines_day_icon
                TableScenes.Carnival -> R.mipmap.carnival_icon
                TableScenes.Sunrise -> R.mipmap.sunrise_icon
                TableScenes.Sunset -> R.mipmap.sunset_icon
                TableScenes.Candlelight -> R.mipmap.candlelight_icon
                TableScenes.Romantic -> R.mipmap.romantic_icon
                TableScenes.Soft -> R.mipmap.soft_icon
                TableScenes.Dreamy -> R.mipmap.dreamy_icon
                TableScenes.Accompany -> R.mipmap.accompany_icon
                TableScenes.Heal -> R.mipmap.heal_icon
                TableScenes.ComfortAble -> R.mipmap.comfortanle_icon
                TableScenes.Feather -> R.mipmap.feather_icon
                TableScenes.Work -> R.mipmap.work_icon
                TableScenes.Read -> R.mipmap.read_icon
                TableScenes.Sleep -> R.mipmap.sleep_icon
                TableScenes.Happy -> R.mipmap.happy_icon
                TableScenes.Colorful -> R.mipmap.colorful_icon
                TableScenes.Party -> R.mipmap.party_icon
                TableScenes.Disco -> R.mipmap.disco_icon
                TableScenes.WeddingDay -> R.mipmap.wedding_day_icon
                TableScenes.KitchenAroma -> R.mipmap.kitchen_aroma_icon
            }
        }

        is FloorScenes -> {
            when (this) {
                FloorScenes.Sunrise -> R.mipmap.trio_sunrise
                FloorScenes.Sunset -> R.mipmap.trio_sunset
                FloorScenes.Birthday -> R.mipmap.trio_birthday
                FloorScenes.Candlelight -> R.mipmap.trio_candlelight
                FloorScenes.Fireworks -> R.mipmap.fireworks_icon
                FloorScenes.Party -> R.mipmap.trio_party
                FloorScenes.Appointment -> R.mipmap.appointment_icon
                FloorScenes.StarrySky -> R.mipmap.starry_sky_icon
                FloorScenes.Romantic -> R.mipmap.trio_romantic
                FloorScenes.Disco -> R.mipmap.trio_disco
                FloorScenes.Rainbow -> R.mipmap.trio_rainbow
                FloorScenes.Film -> R.mipmap.trio_film
                FloorScenes.ChristmasEve -> R.mipmap.christmas_eve_icon
                FloorScenes.FlowingWater -> R.mipmap.flowing_water_icon
                FloorScenes.Sleep -> R.mipmap.trio_sleep
                FloorScenes.Ocean -> R.mipmap.trio_ocean
                FloorScenes.Forest -> R.mipmap.trio_forest
                FloorScenes.Read -> R.mipmap.trio_read
                FloorScenes.Work -> R.mipmap.trio_work
                FloorScenes.Colorful -> R.mipmap.colorful_icon
                FloorScenes.Soft -> R.mipmap.trio_soft
                FloorScenes.WeddingDay -> R.mipmap.trio_wedding_day
                FloorScenes.Snowflake -> R.mipmap.trio_snowflake
                FloorScenes.Flame -> R.mipmap.trio_flame
                FloorScenes.Lightning -> R.mipmap.trio_lightning
                FloorScenes.ValentinesDay -> R.mipmap.trio_valentines_day
                FloorScenes.Halloween -> R.mipmap.trio_halloween
                FloorScenes.Alert -> R.mipmap.alert_icon
                FloorScenes.TimeMachine -> R.mipmap.time_machine_icon
                FloorScenes.TimeMachine2 -> R.mipmap.time_machine_2_icon
                FloorScenes.Meteor -> R.mipmap.trio_meteor
                FloorScenes.Meteor2 -> R.mipmap.meteor_2_icon
                FloorScenes.FireworksShow -> R.mipmap.fireworks_show_icon
            }
        }

        else -> 0
    }
}