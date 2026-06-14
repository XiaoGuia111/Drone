package com.uav.system.service.impl;

import com.uav.system.domain.dto.UavDTO;
import com.uav.system.service.api.AiUavAttributeService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * AI 无人机属性生成服务实现
 *
 * <p>基于规则引擎模拟 AI，根据无人机名称/型号自动生成合理的技术参数。
 * 实际生产环境可对接大模型 API（如 OpenAI、文心一言）生成更真实的属性。</p>
 */
@Service
public class AiUavAttributeServiceImpl implements AiUavAttributeService {

    private static final Random RANDOM = ThreadLocalRandom.current();

    /** 常见无人机制造商 */
    private static final List<String> MANUFACTURERS = Arrays.asList(
            "DJI", "Autel Robotics", "Parrot", "Yuneec", "Skydio",
            "EHang", "PowerVision", "ZeroTech", "Hubsan", "Syma"
    );

    /** 无人机用途描述模板 */
    private static final List<String> DESCRIPTION_TEMPLATES = Arrays.asList(
            "专业级航拍无人机，搭载高性能相机，支持4K视频录制，适用于影视制作和测绘",
            "工业级巡检无人机，具备抗风防水能力，适用于电力巡检和管道监测",
            "消费级便携无人机，轻巧折叠设计，适合户外旅行和日常拍摄",
            "农业植保无人机，大容量药箱，智能路径规划，高效喷洒作业",
            "物流配送无人机，大载重长航时，适用于最后一公里配送",
            "测绘勘察无人机，高精度RTK定位，支持三维建模和地形测绘",
            "消防救援无人机，搭载热成像相机，可穿透烟雾定位火源",
            "环保监测无人机，多光谱传感器，用于水质监测和环境评估"
    );

    @Override
    public UavDTO generateAttributes(String name, String model) {
        UavDTO dto = new UavDTO();
        dto.setName(name);
        dto.setModel(model);

        // 生成唯一的序列号（SN + 时间戳 + 随机数）
        String serialNumber = "SN-" + System.currentTimeMillis() + "-" + RANDOM.nextInt(9999);
        dto.setSerialNumber(serialNumber);

        // 随机选择制造商
        dto.setManufacturer(MANUFACTURERS.get(RANDOM.nextInt(MANUFACTURERS.size())));

        // 生产日期：过去1年内
        dto.setManufactureDate(LocalDate.now().minusDays(RANDOM.nextInt(365)));

        // 根据型号关键词生成合理参数区间
        String modelLower = model != null ? model.toLowerCase() : "";
        double altitudeBase, speedBase, weightBase;
        String status;

        if (modelLower.contains("pro") || modelLower.contains("industry")) {
            // 专业/工业级：高海拔、高速度、较重
            altitudeBase = 5000 + RANDOM.nextInt(5000);
            speedBase = 60 + RANDOM.nextInt(60);
            weightBase = 3.0 + RANDOM.nextDouble() * 7.0;
        } else if (modelLower.contains("mini") || modelLower.contains("lite") || modelLower.contains("nano")) {
            // 迷你/轻量级：低海拔、低速度、轻
            altitudeBase = 500 + RANDOM.nextInt(1500);
            speedBase = 30 + RANDOM.nextInt(30);
            weightBase = 0.25 + RANDOM.nextDouble() * 0.75;
            status = "ACTIVE";
            dto.setStatus(status);
        } else if (modelLower.contains("agri") || modelLower.contains("ag") || modelLower.contains("agri")) {
            // 农业级：中海拔、中速度、重载
            altitudeBase = 1000 + RANDOM.nextInt(2000);
            speedBase = 40 + RANDOM.nextInt(30);
            weightBase = 10.0 + RANDOM.nextDouble() * 20.0;
        } else {
            // 消费级默认
            altitudeBase = 2000 + RANDOM.nextInt(3000);
            speedBase = 45 + RANDOM.nextInt(40);
            weightBase = 1.0 + RANDOM.nextDouble() * 2.0;
        }

        dto.setMaxAltitude(Math.round(altitudeBase * 100.0) / 100.0);
        dto.setMaxSpeed(Math.round(speedBase * 100.0) / 100.0);
        dto.setWeight(Math.round(weightBase * 100.0) / 100.0);
        dto.setStatus("ACTIVE");

        // 生成描述
        int descIndex = Math.abs(name.hashCode()) % DESCRIPTION_TEMPLATES.size();
        String description = DESCRIPTION_TEMPLATES.get(descIndex);
        if (dto.getManufacturer() != null) {
            description = "【" + dto.getManufacturer() + "】" + description;
        }
        dto.setDescription(description);

        return dto;
    }
}
