package com.uav.system.service.impl;

import com.uav.system.domain.dto.UavDTO;
import com.uav.system.domain.dto.UavQueryDTO;
import com.uav.system.domain.entity.Uav;
import com.uav.system.domain.vo.PageResult;
import com.uav.system.exception.DuplicateSerialNumberException;
import com.uav.system.exception.ResourceNotFoundException;
import com.uav.system.repository.mapper.UavMapper;
import com.uav.system.service.api.UavService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

// 无人机管理服务实现

@Service
public class UavServiceImpl implements UavService {

    private final UavMapper uavMapper;// 无人机数据访问层

       public UavServiceImpl(UavMapper uavMapper) {
        this.uavMapper = uavMapper;
    }// 构造函数，注入无人机数据访问层

    // 创建无人机
    @Override
    public UavDTO createUav(UavDTO dto) {
        // 步骤 1：检查序列号是否与其他记录冲突
        Uav existing = uavMapper.findBySerialNumber(dto.getSerialNumber());
        // 步骤 2：如果序列号已存在，抛出异常
        if (existing != null) {
            throw new DuplicateSerialNumberException("Serial number already exists: " + dto.getSerialNumber());
        }// 步骤 3：如果序列号不存在，继续创建无人机记录

        Uav uav = convertToEntity(dto);// 转换为实体
        uav.setCreatedAt(LocalDateTime.now());// 设置创建时间
        uav.setUpdatedAt(LocalDateTime.now());// 设置更新时间
        uavMapper.insert(uav);// 插入数据库

        return convertToDTO(uav);// 转换为DTO并返回
    }

    // 更新无人机
    @Override
    public UavDTO updateUav(Long id, UavDTO dto) {
        // 步骤 1：检查待更新的无人机是否存在
        Uav existing = uavMapper.findById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("UAV not found with id: " + id);
        }

        // 步骤 2：检查序列号是否与其他记录冲突（排除自身）
        // id != null 的判断确保：如果是自己原有的序列号则不报冲突
        Uav uavWithSameSn = uavMapper.findBySerialNumber(dto.getSerialNumber());
        if (uavWithSameSn != null && !uavWithSameSn.getId().equals(id)) {
            throw new DuplicateSerialNumberException("Serial number already exists: " + dto.getSerialNumber());
        }

        Uav uav = convertToEntity(dto);
        uav.setId(id);
        uav.setCreatedAt(existing.getCreatedAt()); // 保留原始创建时间
        uav.setUpdatedAt(LocalDateTime.now());
        uavMapper.update(uav);

        return convertToDTO(uav);
    }

    // 删除无人机
    @Override
    public void deleteUav(Long id) {
        Uav existing = uavMapper.findById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("UAV not found with id: " + id);
        }
        uavMapper.deleteById(id);
    }

    // 查询无人机详情
    @Override
    public UavDTO getUavById(Long id) {
        Uav uav = uavMapper.findById(id);
        if (uav == null) {
            throw new ResourceNotFoundException("UAV not found with id: " + id);
        }
        return convertToDTO(uav);
    }

    // 查询无人机详情（根据序列号）
    @Override
    public UavDTO getUavBySerialNumber(String sn) {
        Uav uav = uavMapper.findBySerialNumber(sn);
        if (uav == null) {
            throw new ResourceNotFoundException("UAV not found with serial number: " + sn);
        }
        return convertToDTO(uav);
    }

    // 查询所有无人机（分页）
    @Override
    public PageResult<UavDTO> listUavs(int page, int size) {
        // 计算 OFFSET（页码从 1 开始）
        // 注意：OFFSET 分页在大数据量时性能退化明显，建议大数据场景改用 keyset pagination
        int offset = (page - 1) * size;
        List<Uav> uavs = uavMapper.findAll(offset, size);
        long total = uavMapper.count();

        // 实体列表 → DTO 列表转换
        List<UavDTO> dtos = uavs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageResult<>(dtos, total, page, size);
    }
    
    // 查询无人机（分页）
    @Override
    public PageResult<UavDTO> searchUavs(UavQueryDTO queryDTO, int page, int size) {
        int offset = (page - 1) * size;
        List<Uav> uavs = uavMapper.search(
                queryDTO.getName(),
                queryDTO.getModel(),
                queryDTO.getSerialNumber(),
                queryDTO.getStatus(),
                offset,
                size
        );
        long total = uavMapper.searchCount(
                queryDTO.getName(),
                queryDTO.getModel(),
                queryDTO.getSerialNumber(),
                queryDTO.getStatus()
        );

        List<UavDTO> dtos = uavs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageResult<>(dtos, total, page, size);
    }

    /**
     * 将数据传输对象（DTO）转换为数据库实体对象（Entity）
     * <p>适用于新增和更新操作，ID 和审计时间字段由调用方单独设置。</p>
     *
     * @param dto 无人机 DTO
     * @return 无人机 Entity
     */
    private Uav convertToEntity(UavDTO dto) {
        Uav uav = new Uav();
        uav.setId(dto.getId());
        uav.setName(dto.getName());
        uav.setModel(dto.getModel());
        uav.setSerialNumber(dto.getSerialNumber());
        uav.setManufacturer(dto.getManufacturer());
        uav.setManufactureDate(dto.getManufactureDate());
        uav.setMaxAltitude(dto.getMaxAltitude());
        uav.setMaxSpeed(dto.getMaxSpeed());
        uav.setWeight(dto.getWeight());
        uav.setStatus(dto.getStatus());
        uav.setDescription(dto.getDescription());
        uav.setCreatedAt(dto.getCreatedAt());
        uav.setUpdatedAt(dto.getUpdatedAt());
        return uav;
    }

    /**
     * 将数据库实体对象（Entity）转换为数据传输对象（DTO）
     * <p>适用于返回给前端的数据组装，隐藏实体层敏感或冗余字段。</p>
     *
     * @param uav 无人机 Entity
     * @return 无人机 DTO
     */
    private UavDTO convertToDTO(Uav uav) {
        UavDTO dto = new UavDTO();
        dto.setId(uav.getId());
        dto.setName(uav.getName());
        dto.setModel(uav.getModel());
        dto.setSerialNumber(uav.getSerialNumber());
        dto.setManufacturer(uav.getManufacturer());
        dto.setManufactureDate(uav.getManufactureDate());
        dto.setMaxAltitude(uav.getMaxAltitude());
        dto.setMaxSpeed(uav.getMaxSpeed());
        dto.setWeight(uav.getWeight());
        dto.setStatus(uav.getStatus());
        dto.setDescription(uav.getDescription());
        dto.setCreatedAt(uav.getCreatedAt());
        dto.setUpdatedAt(uav.getUpdatedAt());
        return dto;
    }
}
