package com.ledvance.domain.bean.command.scenes

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/19/26 11:31
 * Describe : TableScenes
 */
enum class TableScenes(override val title: String, override val command: Byte) : Scene {
    SpringBreeze("Spring Breeze", 0x01),       // 春风
    Sky("Sky", 0x02),                         // 天空
    Firefly("Firefly", 0x03),                 // 萤火虫
    CherryBlossom("Cherry Blossom", 0x04),    // 樱花
    MountainForest("Mountain Forest", 0x05),  // 山林
    Lake("Lake", 0x07),                       // 湖
    Forest("Forest", 0x0e),                   // 森林
    Aurora("Aurora", 0x0f),                   // 极光
    Flame("Flame", 0x10),                     // 火焰
    Rainbow("Rainbow", 0x12),                 // 彩虹
    StarrySky("Starry Sky", 0x13),            // 星空
    Lightning("Lightning", 0x14),             // 闪电
    Meteor("Meteor", 0x16),                   // 流星
    SeaWave("Sea Wave", 0x17),                // 海浪
    RipplingWheat("Rippling Wheat", 0x18),    // 麦浪
    Snowflake("Snowflake", 0x19),             // 雪花
    Ripples("Ripples", 0x2a),                 // 涟漪
    Ocean("Ocean", 0x2b),                     // 海洋
    Rain("Rain", 0x00),                       // 雨

    Christmas("Christmas", 0x1a),             // 圣诞节
    Halloween("Halloween", 0x1b),             // 万圣节
    Easter("Easter", 0x1c),                   // 复活节
    ValentinesDay("Valentine's Day", 0x1d),   // 情人节（修正笔误的0x21d为0x1d）
    Carnival("Carnival", 0x1e),               // 狂欢节

    Sunrise("Sunrise", 0x1f),                 // 日出
    Sunset("Sunset", 0x20),                   // 日落
    Candlelight("Candlelight", 0x21),         // 烛光
    Romantic("Romantic", 0x22),               // 浪漫
    Soft("Soft", 0x23),                       // 柔和
    Dreamy("Dreamy", 0x09),                   // 梦幻
    Accompany("Accompany", 0x0c),             // 陪伴
    Heal("Heal", 0x0d),                       // 治愈
    ComfortAble("ComfortAble", 0x0b),         // 安慰的
    Feather("Feather", 0x06),                 // 羽毛

    Work("Work", 0x24),                       // 工作
    Read("Read", 0x25),                       // 阅读
    Sleep("Sleep", 0x26),                     // 睡眠
    Happy("Happy", 0x0a),                     // 快乐
    Colorful("Colorful", 0x27),               // 炫彩
    Party("Party", 0x28),                     // 聚会
    Disco("Disco", 0x15),                     // 迪斯科
    WeddingDay("Wedding Day", 0x29),          // 结婚纪念日（对照表标注为结婚纪念日，与WeddingDay对应）
    KitchenAroma("Kitchen Aroma", 0x08);      // 厨房香气

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