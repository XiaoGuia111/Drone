package com.uav.system.repository.mapper;

import com.uav.system.domain.entity.Uav;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("sqlite")
/**
 * 无人机 Mapper 层集成测试
 *
 * <p>使用 {@link SpringBootTest} 加载完整 Spring 上下文，
 * 连接真实的 SQLite 数据库（{@code test profile}）执行 CRUD 操作测试。
 * 测试用例覆盖：</p>
 * <ul>
 *   <li>新增 &amp; 按 ID/序列号查询</li>
 *   <li>更新 &amp; 删除</li>
 *   <li>分页查询（findAll + count）</li>
 *   <li>多条件组合搜索（search + searchCount）</li>
 * </ul>
 *
 * <p><b>注意：</b>测试方法之间相互独立，每次插入使用唯一的序列号（通过时间戳保证）。</p>
 *
 * @see UavMapper
 * @see Uav
 */
class UavMapperTest {

    @Autowired
    private UavMapper uavMapper;

    private Uav testUav;

    @BeforeEach
    void setUp() {
        testUav = new Uav();
        testUav.setName("TestUAV");
        testUav.setModel("Model-X");
        testUav.setSerialNumber("SN-UNIQUE-" + System.currentTimeMillis());
        testUav.setManufacturer("TestManufacturer");
        testUav.setMaxAltitude(1000.0);
        testUav.setMaxSpeed(50.0);
        testUav.setWeight(10.0);
        testUav.setStatus("ACTIVE");
        testUav.setDescription("Test Description");
        testUav.setCreatedAt(LocalDateTime.now());
        testUav.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void should_insertAndFindById() {
        int rows = uavMapper.insert(testUav);
        assertEquals(1, rows);
        assertNotNull(testUav.getId());

        Uav found = uavMapper.findById(testUav.getId());
        assertNotNull(found);
        assertEquals("TestUAV", found.getName());
        assertEquals(testUav.getSerialNumber(), found.getSerialNumber());
        assertEquals("ACTIVE", found.getStatus());
    }

    @Test
    void should_insertAndFindBySerialNumber() {
        uavMapper.insert(testUav);

        Uav found = uavMapper.findBySerialNumber(testUav.getSerialNumber());
        assertNotNull(found);
        assertEquals("TestUAV", found.getName());
    }

    @Test
    void should_returnNull_when_findByUnknownId() {
        Uav found = uavMapper.findById(999L);
        assertNull(found);
    }

    @Test
    void should_updateUav() {
        uavMapper.insert(testUav);

        testUav.setName("UpdatedUAV");
        testUav.setMaxAltitude(2000.0);
        int rows = uavMapper.update(testUav);
        assertEquals(1, rows);

        Uav updated = uavMapper.findById(testUav.getId());
        assertEquals("UpdatedUAV", updated.getName());
        assertEquals(2000.0, updated.getMaxAltitude(), 0.01);
    }

    @Test
    void should_deleteById() {
        uavMapper.insert(testUav);
        assertNotNull(uavMapper.findById(testUav.getId()));

        uavMapper.deleteById(testUav.getId());
        assertNull(uavMapper.findById(testUav.getId()));
    }

    @Test
    void should_findAll_withPagination() {
        uavMapper.insert(testUav);

        Uav uav2 = new Uav();
        uav2.setName("UAV-2");
        uav2.setModel("Model-Y");
        uav2.setSerialNumber("SN-UNIQUE-" + System.currentTimeMillis() + "-2");
        uav2.setStatus("MAINTENANCE");
        uav2.setCreatedAt(LocalDateTime.now());
        uav2.setUpdatedAt(LocalDateTime.now());
        uavMapper.insert(uav2);

        List<Uav> all = uavMapper.findAll(0, 10);
        assertTrue(all.size() >= 2);

        List<Uav> firstPage = uavMapper.findAll(0, 1);
        assertEquals(1, firstPage.size());
    }

    @Test
    void should_countAll() {
        long before = uavMapper.count();
        uavMapper.insert(testUav);
        long after = uavMapper.count();

        assertEquals(before + 1, after);
    }

    @Test
    void should_search_byName() {
        uavMapper.insert(testUav);

        List<Uav> results = uavMapper.search("TestUAV", null, null, null, 0, 10);
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(u -> u.getName().contains("TestUAV")));
    }

    @Test
    void should_search_byStatus() {
        uavMapper.insert(testUav);

        List<Uav> activeResults = uavMapper.search(null, null, null, "ACTIVE", 0, 10);
        assertFalse(activeResults.isEmpty());
        assertTrue(activeResults.stream().allMatch(u -> "ACTIVE".equals(u.getStatus())));
    }

    @Test
    void should_searchCount() {
        uavMapper.insert(testUav);

        long count = uavMapper.searchCount("TestUAV", null, null, null);
        assertTrue(count >= 1);
    }
}
