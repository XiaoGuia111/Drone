package com.uav.system.domain.enums;

//无人机状态枚举
public enum UavStatus {
    /** 正常运行状态 */
    ACTIVE("正常"),
    /** 维护中状态 */
    MAINTENANCE("维护中"),
    /** 已报废状态 */
    SCRAPPED("已报废");

    private final String description;

    UavStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
