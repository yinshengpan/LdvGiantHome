package com.ledvance.domain.bean.command.scenes

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/19/26 11:31
 * Describe : FloorScenes
 */
enum class FloorScenes(override val title: String, override val command: Byte) : Scene {
    Sunrise("Sunrise", 0x01),                 // 日出
    Sunset("Sunset", 0x02),                   // 日落
    Birthday("Birthday", 0x03),               // 生日
    Candlelight("Candlelight", 0x04),         // 烛光
    Fireworks("Fireworks", 0x05),             // 烟花
    Party("Party", 0x06),                     // 聚会
    Appointment("Appointment", 0x07),         // 约会
    StarrySky("Starry Sky", 0x08),            // 星空
    Romantic("Romantic", 0x09),               // 浪漫
    Disco("Disco", 0x0A),                     // 迪斯科
    Rainbow("Rainbow", 0x0B),                 // 彩虹
    Film("Film", 0x0C),                       // 电影
    ChristmasEve("Christmas Eve", 0x0D),      // 圣诞夜
    FlowingWater("Flowing Water", 0x0E),      // 流水
    Sleep("Sleep", 0x0F),                     // 睡眠
    Ocean("Ocean", 0x10),                     // 海洋
    Forest("Forest", 0x11),                   // 森林
    Read("Read", 0x12),                       // 阅读
    Work("Work", 0x13),                       // 工作
    Colorful("Colorful", 0x14),               // 炫彩
    Soft("Soft", 0x15),                       // 柔和
    WeddingDay("Wedding Day", 0x16),          // 结婚纪念日
    Snowflake("Snowflake", 0x17),             // 雪花
    Flame("Flame", 0x18),                     // 火焰
    Lightning("Lightning", 0x19),             // 闪电
    ValentinesDay("Valentine's Day", 0x1A),   // 情人节
    Halloween("Halloween", 0x1B),             // 万圣节
    Alert("Alert", 0x1C),                     // 警报
    TimeMachine("Time Machine", 0x1D),        // 时光机
    TimeMachine2("Time Machine 2", 0x1E),     // 时光机2
    Meteor("Meteor", 0x1F),                   // 流星
    Meteor2("Meteor 2", 0x20),                // 流星2
    FireworksShow("Fireworks Show", 0x21);    // 烟花秀

    companion object {
        fun getAllScene(): List<Scene> {
            return entries
        }
    }
}