package com.uav.system.domain.dto;

//无人机组合查询条件 DTO
public class UavQueryDTO {

    /** 无人机名称（模糊匹配） */
    private String name;

    /** 型号（模糊匹配） */
    private String model;

    /** 序列号（模糊匹配） */
    private String serialNumber;

    /** 状态（精确匹配，如 ACTIVE / MAINTENANCE / SCRAPPED） */
    private String status;

    public UavQueryDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}