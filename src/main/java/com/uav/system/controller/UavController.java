package com.uav.system.controller;

import com.uav.system.common.Result;
import com.uav.system.domain.dto.UavDTO;
import com.uav.system.domain.dto.UavQueryDTO;
import com.uav.system.domain.vo.PageResult;
import com.uav.system.service.api.UavService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

// 无人机信息管理 REST 控制器
@Tag(name = "UAV Management", description = "UAV information management APIs")
@RestController// 标识为 RESTful 控制器，处理 HTTP 请求，告诉spring这是接口类，返回 JSON
@RequestMapping("/api/uavs")// 映射路径，/api/uavs 开头的请求都由该控制器处理
public class UavController {

    private final UavService uavService;// 无人机服务接口，用于调用业务逻辑

    public UavController(UavService uavService) {// 构造函数，注入无人机服务接口
        this.uavService = uavService;// 初始化无人机服务接口
    }
    
    // 创建无人机
    @Operation(summary = "Create a new UAV")// 定义 API 操作，描述为创建新无人机
    @PostMapping// 映射 POST 请求，用于创建新无人机
    public Result<UavDTO> createUav(@Valid @RequestBody UavDTO dto) {
        UavDTO created = uavService.createUav(dto);// 调用无人机服务创建新无人机
        return Result.success(created);// 返回创建成功的无人机信息
    }
    // 更新无人机
    @Operation(summary = "Update UAV by ID")// 定义 API 操作，描述为更新无人机信息
    @PutMapping("/{id}")// 映射 PUT 请求，用于更新无人机信息
    public Result<UavDTO> updateUav(@PathVariable Long id, @Valid @RequestBody UavDTO dto) {
        UavDTO updated = uavService.updateUav(id, dto);// 调用无人机服务更新无人机信息
        return Result.success(updated);// 返回更新后的无人机信息
    }
    // 查询无人机详情
    @Operation(summary = "Get UAV by ID")// 定义 API 操作，描述为根据无人机 ID查询无人机详情
    @GetMapping("/{id}")// 映射 GET 请求，用于根据无人机 ID查询无人机详情
    public Result<UavDTO> getUavById(@PathVariable Long id) {
        UavDTO dto = uavService.getUavById(id);
        return Result.success(dto);
    }
    // 查询所有无人机（分页）
    @Operation(summary = "List all UAVs with pagination and search")// 定义 API 操作，描述为查询所有无人机（分页）
    @GetMapping// 映射 GET 请求，用于查询所有无人机（分页）信息
    public Result<PageResult<UavDTO>> listUavs(
            @RequestParam(defaultValue = "1") int page,// 分页参数码，默认值为 1
            @RequestParam(defaultValue = "10") int size,// 分页参数大小，默认值为 10
            @RequestParam(required = false) String name,// 查询参数，无人机名称
            @RequestParam(required = false) String model,// 查询参数，无人机型号
            @RequestParam(required = false) String serialNumber,// 查询参数，无人机序列号
            @RequestParam(required = false) String status) {// 查询参数，无人机状态

        // 判断是否有查询参数，有则走搜索，无则返回全部列表
        boolean hasQuery = (name != null && !name.isEmpty()) ||
                          (model != null && !model.isEmpty()) ||
                          (serialNumber != null && !serialNumber.isEmpty()) ||
                          (status != null && !status.isEmpty());

        PageResult<UavDTO> result;// 分页结果，用于存储查询到的无人机信息
        // 调用无人机服务查询无人机信息
        if (hasQuery) {
            UavQueryDTO queryDTO = new UavQueryDTO();
            queryDTO.setName(name);
            queryDTO.setModel(model);
            queryDTO.setSerialNumber(serialNumber);
            queryDTO.setStatus(status);
            result = uavService.searchUavs(queryDTO, page, size);// 调用无人机服务根据查询参数查询无人机信息
        } else {
            result = uavService.listUavs(page, size);//没有查询参数，返回全部列表（分页）信息
        }
        return Result.success(result);
    }
    // 删除无人机
    @Operation(summary = "Delete UAV by ID")// 定义 API 操作，描述为根据无人机 ID删除无人机
    @DeleteMapping("/{id}")// 映射 DELETE 请求，用于根据无人机 ID删除无人机
    public Result<Void> deleteUav(@PathVariable Long id) {
        uavService.deleteUav(id);
        return Result.success();
    }
}
