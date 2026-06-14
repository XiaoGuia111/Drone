package com.uav.system.service.api;

import com.uav.system.domain.dto.UavDTO;

/**
 * AI 无人机属性生成服务接口
 *
 * <p>模拟 AI 根据无人机名称或型号，自动生成合理的无人机属性配置。
 * 包括最大飞行高度、速度、重量、制造商、描述等。</p>
 */
public interface AiUavAttributeService {

    /**
     * 根据无人机名称和型号自动生成属性
     *
     * @param name  无人机名称
     * @param model 无人机型号
     * @return 包含 AI 生成属性的 UavDTO（仅填充 AI 可生成的字段）
     */
    UavDTO generateAttributes(String name, String model);
}
