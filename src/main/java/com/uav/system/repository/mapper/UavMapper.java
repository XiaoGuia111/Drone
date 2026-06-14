package com.uav.system.repository.mapper;

import com.uav.system.domain.entity.Uav;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 无人机数据访问层（MyBatis Mapper）

@Mapper
public interface UavMapper {
    /** 新增无人机（自动回填自增 ID） */
    int insert(Uav uav);

    /** 更新无人机信息（按 ID 更新） */
    int update(Uav uav);

    /** 按 ID 删除无人机 */
    int deleteById(@Param("id") Long id);

    /** 按 ID 查询无人机 */
    Uav findById(@Param("id") Long id);

    /** 按序列号查询无人机（唯一约束） */
    Uav findBySerialNumber(@Param("serialNumber") String serialNumber);

    /** 分页查询全部无人机（按创建时间倒序） */
    List<Uav> findAll(@Param("offset") int offset, @Param("limit") int limit);

    /** 统计无人机总记录数 */
    long count();

    /**
     * 多条件组合搜索（分页）
     * <p>名称、型号、序列号使用 LIKE 模糊匹配，状态使用精确匹配。
     * 传入 null 或空字符串时跳过该条件。</p>
     *
     * @param name         名称（可选）
     * @param model        型号（可选）
     * @param serialNumber 序列号（可选）
     * @param status       状态（可选）
     * @param offset       偏移量
     * @param limit        每页大小
     * @return 匹配的无人机列表
     */
    List<Uav> search(@Param("name") String name,
                      @Param("model") String model,
                      @Param("serialNumber") String serialNumber,
                      @Param("status") String status,
                      @Param("offset") int offset,
                      @Param("limit") int limit);

    /**
     * 统计符合条件的搜索记录数
     * <p>与 search 方法使用相同的查询条件，仅返回 COUNT 结果。</p>
     *
     * @param name         名称（可选）
     * @param model        型号（可选）
     * @param serialNumber 序列号（可选）
     * @param status       状态（可选）
     * @return 匹配条件的总记录数
     */
    long searchCount(@Param("name") String name,
                     @Param("model") String model,
                     @Param("serialNumber") String serialNumber,
                     @Param("status") String status);
}
