package com.ledvance.domain.bean.command.giant.scenes

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/19/26 11:31
 * Describe : FloorScenes
 */
enum class FloorScenes(override val command: Byte) : Scene {
    Sunrise(0x01),                 // 日出
    Sunset(0x02),                  // 日落
    Birthday(0x03),                // 生日
    Candlelight(0x04),             // 烛光
    Fireworks(0x05),               // 烟花
    Party(0x06),                   // 聚会
    Appointment(0x07),             // 约会
    StarrySky(0x08),               // 星空
    Romantic(0x09),                // 浪漫
    Disco(0x0A),                   // 迪斯科
    Rainbow(0x0B),                 // 彩虹
    Film(0x0C),                    // 电影
    ChristmasEve(0x0D),            // 圣诞夜
    FlowingWater(0x0E),            // 流水
    Sleep(0x0F),                   // 睡眠
    Ocean(0x10),                   // 海洋
    Forest(0x11),                  // 森林
    Read(0x12),                    // 阅读
    Work(0x13),                    // 工作
    Colorful(0x14),                // 炫彩
    Soft(0x15),                    // 柔和
    WeddingDay(0x16),              // 结婚纪念日
    Snowflake(0x17),               // 雪花
    Flame(0x18),                   // 火焰
    Lightning(0x19),               // 闪电
    ValentinesDay(0x1A),           // 情人节
    Halloween(0x1B),               // 万圣节
    Alert(0x1C),                   // 警报
    TimeMachine(0x1D),             // 时光机
    TimeMachine2(0x1E),            // 时光机2
    Meteor(0x1F),                  // 流星
    Meteor2(0x20),                 // 流星2
    FireworksShow(0x21);           // 烟花秀

    companion object {
        fun getAllScene(): List<Scene> {
            return entries
        }
    }
}