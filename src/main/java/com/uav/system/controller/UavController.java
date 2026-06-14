package com.uav.system.controller;

import com.uav.system.common.Result;
import com.uav.system.domain.dto.UavDTO;
import com.uav.system.domain.dto.UavQueryDTO;
import com.uav.system.domain.vo.PageResult;
import com.uav.system.service.api.AiUavAttributeService;
import com.uav.system.service.api.UavService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * 无人机信息管理 REST 控制器
 *
 * <p>提供无人机信息的 CRUD 操作和 AI 属性生成接口。</p>
 */
@RestController
@RequestMapping("/api/uavs")
public class UavController {

    private final UavService uavService;
    private final AiUavAttributeService aiUavAttributeService;

    public UavController(UavService uavService, AiUavAttributeService aiUavAttributeService) {
        this.uavService = uavService;
        this.aiUavAttributeService = aiUavAttributeService;
    }

    /**
     * AI 自动生成无人机属性
     *
     * <p>根据无人机名称和型号，通过规则引擎模拟 AI 自动生成：
     * 序列号、制造商、技术参数、描述等属性，辅助用户快速录入。</p>
     */
    @PostMapping("/ai-generate")
    public Result<UavDTO> aiGenerate(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String model = request.get("model");
        UavDTO generated = aiUavAttributeService.generateAttributes(name, model);
        return Result.success(generated);
    }

    @PostMapping
    public Result<UavDTO> createUav(@Valid @RequestBody UavDTO dto) {
        UavDTO created = uavService.createUav(dto);
        return Result.success(created);
    }

    @PutMapping("/{id}")
    public Result<UavDTO> updateUav(@PathVariable Long id, @Valid @RequestBody UavDTO dto) {
        UavDTO updated = uavService.updateUav(id, dto);
        return Result.success(updated);
    }

    @GetMapping("/{id}")
    public Result<UavDTO> getUavById(@PathVariable Long id) {
        UavDTO dto = uavService.getUavById(id);
        return Result.success(dto);
    }

    @GetMapping
    public Result<PageResult<UavDTO>> listUavs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String serialNumber,
            @RequestParam(required = false) String status) {

        boolean hasQuery = (name != null && !name.isEmpty()) ||
                          (model != null && !model.isEmpty()) ||
                          (serialNumber != null && !serialNumber.isEmpty()) ||
                          (status != null && !status.isEmpty());

        PageResult<UavDTO> result;
        if (hasQuery) {
            UavQueryDTO queryDTO = new UavQueryDTO();
            queryDTO.setName(name);
            queryDTO.setModel(model);
            queryDTO.setSerialNumber(serialNumber);
            queryDTO.setStatus(status);
            result = uavService.searchUavs(queryDTO, page, size);
        } else {
            result = uavService.listUavs(page, size);
        }
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteUav(@PathVariable Long id) {
        uavService.deleteUav(id);
        return Result.success();
    }
}
