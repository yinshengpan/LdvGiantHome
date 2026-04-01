package com.ledvance.domain.bean.command.giant.scenes

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/19/26 11:31
 * Describe : TableScenes
 */
enum class TableScenes(override val command: Byte) : Scene {
    // 自然类场景
    SpringBreeze(0x01),         // 春风
    Sky(0x02),                  // 天空
    Firefly(0x03),              // 萤火虫
    CherryBlossom(0x04),        // 樱花
    MountainForest(0x05),       // 山林
    Lake(0x07),                 // 湖
    Forest(0x0e),               // 森林
    Aurora(0x0f),               // 极光
    Flame(0x10),                // 火焰
    Rainbow(0x12),              // 彩虹
    StarrySky(0x13),            // 星空
    Lightning(0x14),            // 闪电
    Meteor(0x16),               // 流星
    SeaWave(0x17),              // 海浪
    RipplingWheat(0x18),        // 麦浪
    Snowflake(0x19),            // 雪花
    Ripples(0x2a),              // 涟漪
    Ocean(0x2b),                // 海洋
    Rain(0x00),                 // 雨

    // 节日类场景
    Christmas(0x1a),            // 圣诞节
    Halloween(0x1b),            // 万圣节
    Easter(0x1c),               // 复活节
    ValentinesDay(0x1d),        // 情人节
    Carnival(0x1e),             // 狂欢节

    // 氛围类场景
    Sunrise(0x1f),              // 日出
    Sunset(0x20),               // 日落
    Candlelight(0x21),          // 烛光
    Romantic(0x22),             // 浪漫
    Soft(0x23),                 // 柔和
    Dreamy(0x09),               // 梦幻
    Accompany(0x0c),            // 陪伴
    Heal(0x0d),                 // 治愈
    ComfortAble(0x0b),          // 安慰的
    Feather(0x06),              // 羽毛

    // 生活类场景
    Work(0x24),                 // 工作
    Read(0x25),                 // 阅读
    Sleep(0x26),                // 睡眠
    Happy(0x0a),                // 快乐
    Colorful(0x27),             // 炫彩
    Party(0x28),                // 聚会
    Disco(0x15),                // 迪斯科
    WeddingDay(0x29),           // 结婚纪念日
    KitchenAroma(0x08);         // 厨房香气

    companion object {

        val tableScenes = mapOf(
            TableSceneType.Natural to getNaturalScenes(),
            TableSceneType.Festival to getFestivalScenes(),
            TableSceneType.Soothing to getSoothingScenes(),
            TableSceneType.Life to getLifeScenes()
        )

        private fun getFestivalScenes(): List<Scene> {
            return listOf(
                Christmas,
                Halloween,
                Easter,
                ValentinesDay,
                Carnival
            )
        }

        private fun getLifeScenes(): List<Scene> {
            return listOf(
                Work,
                Read,
                Sleep,
                Happy,
                Colorful,
                Party,
                Disco,
                WeddingDay,
                KitchenAroma
            )
        }

        private fun getNaturalScenes(): List<Scene> {
            return listOf(
                SpringBreeze,
                Forest,
                Sky,
                Flame,
                Rainbow,
                MountainForest,
                StarrySky,
                Aurora,
                Ripples,
                Lake,
                SeaWave,
                Lightning,
                Firefly,
                CherryBlossom,
                RipplingWheat,
                Snowflake,
                Meteor,
                Ocean,
                Rain
            )
        }

        private fun getSoothingScenes(): List<Scene> {
            return listOf(
                Sunrise,
                Sunset,
                Candlelight,
                Romantic,
                Soft,
                Dreamy,
                Accompany,
                Heal,
                ComfortAble,
                Feather,
            )
        }
    }
}