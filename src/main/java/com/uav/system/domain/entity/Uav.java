package com.uav.system.domain.entity;


import java.time.LocalDate;
import java.time.LocalDateTime;

//无人机数据库实体
public class Uav {
    /** 主键 ID（数据库自增） */
    private Long id;
    /** 无人机名称 */
    private String name;
    /** 型号 */
    private String model;
    /** 唯一序列号 */
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
    /** 状态（ACTIVE / MAINTENANCE / SCRAPPED） */
    private String status;
    /** 描述备注 */
    private String description;
    /** 记录创建时间 */
    private LocalDateTime createdAt;
    /** 记录最后更新时间 */
    private LocalDateTime updatedAt;

    public Uav() {
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
