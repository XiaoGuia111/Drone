package com.uav.system.service.api;

import com.uav.system.domain.dto.UavDTO;
import com.uav.system.domain.dto.UavQueryDTO;
import com.uav.system.domain.vo.PageResult;

// 无人机管理服务接口
public interface UavService {

    /**
     * 创建无人机
     * <p>保存前会校验序列号是否已存在（唯一约束），避免重复录入。</p>
     *
     * @param dto 包含无人机各项属性的数据传输对象
     * @return 创建成功后的完整无人机 DTO（含数据库生成的主键 ID）
     * @throws com.uav.system.exception.DuplicateSerialNumberException 序列号已存在时抛出
     */
    UavDTO createUav(UavDTO dto);// 创建无人机

    /**
     * 更新无人机信息
     * <p>先检查目标 ID 是否存在，再校验序列号是否与其他记录冲突。</p>
     *
     * @param id  待更新无人机的数据库主键
     * @param dto 新的无人机属性
     * @return 更新后的完整无人机 DTO
     * @throws com.uav.system.exception.ResourceNotFoundException     无人机不存在时抛出
     * @throws com.uav.system.exception.DuplicateSerialNumberException 序列号冲突时抛出
     */
    UavDTO updateUav(Long id, UavDTO dto);// 更新无人机信息

    /**
     * 删除无人机
     *
     * @param id 待删除无人机的数据库主键
     * @throws com.uav.system.exception.ResourceNotFoundException 无人机不存在时抛出
     */
    void deleteUav(Long id);// 删除无人机

    /**
     * 根据主键查询无人机详情
     *
     * @param id 无人机数据库主键
     * @return 无人机 DTO
     * @throws com.uav.system.exception.ResourceNotFoundException 未找到时抛出
     */
    UavDTO getUavById(Long id);// 根据主键查询无人机详情

    /**
     * 根据序列号查询无人机
     *
     * @param sn 无人机唯一序列号
     * @return 无人机 DTO
     * @throws com.uav.system.exception.ResourceNotFoundException 未找到时抛出
     */
    UavDTO getUavBySerialNumber(String sn);// 根据序列号查询无人机

    /**
     * 分页查询全部无人机列表
     * <p>按创建时间倒序排列，适用于无搜索条件的列表展示。</p>
     *
     * @param page 页码（从 1 开始）
     * @param size 每页条数
     * @return 分页结果 {@link PageResult}
     */
    PageResult<UavDTO> listUavs(int page, int size);// 分页查询全部无人机列表

    /**
     * 多条件组合搜索无人机（分页）
     * <p>支持按名称、型号、序列号（模糊匹配）和状态（精确匹配）进行组合查询，
     * 条件之间为 AND 关系。所有参数均为可选，传入 null 时忽略该条件。</p>
     *
     * @param queryDTO 组合查询条件 DTO
     * @param page     页码（从 1 开始）
     * @param size     每页条数
     * @return 分页结果 {@link PageResult}
     */
    PageResult<UavDTO> searchUavs(UavQueryDTO queryDTO, int page, int size);// 多条件组合搜索无人机（分页）
}
