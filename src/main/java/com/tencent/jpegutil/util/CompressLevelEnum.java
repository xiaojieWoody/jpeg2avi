package com.tencent.jpegutil.util;

/**
 * 分辨率对应的level
 */
public enum  CompressLevelEnum {

    // https://zh.wikipedia.org/wiki/%E6%98%BE%E7%A4%BA%E5%88%86%E8%BE%A8%E7%8E%87%E5%88%97%E8%A1%A8
    LEVEL_1("1","scale=1920:1080"),
    LEVEL_2("2","scale=1280:1024"),
    LEVEL_3("3","scale=960:540");

    private String level;
    private String content;

    CompressLevelEnum(String level, String content) {
        this.level = level;
        this.content = content;
    }

    public static String getContentByLevel(String level) {
        for (CompressLevelEnum value : CompressLevelEnum.values()) {
            if(value.level.equals(level)) {
                return value.content;
            }
        }
        return null;
    }

    public String getLevel() {
        return level;
    }

    public String getContent() {
        return content;
    }
}
