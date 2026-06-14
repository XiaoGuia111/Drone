package com.uav.system.domain.dto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;

//无人机数据传输对象（DTO）
public class UavDTO {
    /** 主键 ID（新增时不传，更新时必传） */
    private Long id;

    /** 无人机名称（必填） */
    @NotBlank(message = "UAV name is required")
    private String name;

    /** 型号（必填） */
    @NotBlank(message = "Model is required")
    private String model;

    /** 序列号（必填，全局唯一） */
    @NotBlank(message = "Serial number is required")
    private String serialNumber;

    /** 制造商 */
    private String manufacturer;

    /** 生产日期 */
    private LocalDate manufactureDate;

    /** 最大飞行高度（单位：米） */
    private Double maxAltitude;

    /** 最大飞行速度（单位：km/h） */
    private Double maxSpeed;

    /** 重量（单位：kg） */
    private Double weight;

    /** 状态（必填，ACTIVE / MAINTENANCE / SCRAPPED） */
    @NotBlank(message = "Status is required")
    private String status;

    /** 描述备注 */
    private String description;

    /** 创建时间（由服务端自动填充） */
    private LocalDateTime createdAt;

    /** 更新时间（由服务端自动填充） */
    private LocalDateTime updatedAt;

    public UavDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public LocalDate getManufactureDate() {
        return manufactureDate;
    }

    public void setManufactureDate(LocalDate manufactureDate) {
        this.manufactureDate = manufactureDate;
    }

    public Double getMaxAltitude() {
        return maxAltitude;
    }

    public void setMaxAltitude(Double maxAltitude) {
        this.maxAltitude = maxAltitude;
    }

    public Double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(Double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
