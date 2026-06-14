package com.uav.system.service;

import com.uav.system.domain.dto.UavDTO;
import com.uav.system.domain.entity.Uav;
import com.uav.system.domain.vo.PageResult;
import com.uav.system.exception.DuplicateSerialNumberException;
import com.uav.system.exception.ResourceNotFoundException;
import com.uav.system.repository.mapper.UavMapper;
import com.uav.system.service.impl.UavServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 无人机服务层（UavServiceImpl）单元测试
 *
 * <p>使用 {@link MockitoExtension} 纯 Mock 方式测试，
 * 通过 Mock {@link UavMapper} 的行为来验证服务层的业务逻辑。</p>
 *
 * <p>测试覆盖：</p>
 * <ul>
 *   <li>序列号唯一性校验（重复时抛出 {@link DuplicateSerialNumberException}）</li>
 *   <li>资源存在性校验（ID 不存在时抛出 {@link ResourceNotFoundException}）</li>
 *   <li>正常的 CRUD 业务流程</li>
 *   <li>分页查询结果组装</li>
 * </ul>
 *
 * @see UavServiceImpl
 * @see UavMapper
 */
@ExtendWith(MockitoExtension.class)
class UavServiceTest {

    @Mock
    private UavMapper uavMapper;

    @InjectMocks
    private UavServiceImpl uavService;

    private UavDTO testDto;
    private Uav testUav;

    @BeforeEach
    void setUp() {
        testDto = new UavDTO();
        testDto.setName("TestUAV");
        testDto.setModel("Model-X");
        testDto.setSerialNumber("SN12345");
        testDto.setManufacturer("TestManufacturer");
        testDto.setManufactureDate(LocalDate.now());
        testDto.setMaxAltitude(1000.0);
        testDto.setMaxSpeed(50.0);
        testDto.setWeight(10.0);
        testDto.setStatus("ACTIVE");
        testDto.setDescription("Test Description");

        testUav = new Uav();
        testUav.setId(1L);
        testUav.setName("TestUAV");
        testUav.setModel("Model-X");
        testUav.setSerialNumber("SN12345");
        testUav.setManufacturer("TestManufacturer");
        testUav.setManufactureDate(LocalDate.now());
        testUav.setMaxAltitude(1000.0);
        testUav.setMaxSpeed(50.0);
        testUav.setWeight(10.0);
        testUav.setStatus("ACTIVE");
        testUav.setDescription("Test Description");
        testUav.setCreatedAt(LocalDateTime.now());
        testUav.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void createUav_Success() {
        when(uavMapper.findBySerialNumber(testDto.getSerialNumber())).thenReturn(null);
        when(uavMapper.insert(any(Uav.class))).thenReturn(1);

        UavDTO result = uavService.createUav(testDto);

        assertNotNull(result);
        assertEquals(testDto.getName(), result.getName());
        assertEquals(testDto.getSerialNumber(), result.getSerialNumber());
        verify(uavMapper).insert(any(Uav.class));
    }

    @Test
    void createUav_DuplicateSerialNumber() {
        when(uavMapper.findBySerialNumber(testDto.getSerialNumber())).thenReturn(testUav);

        assertThrows(DuplicateSerialNumberException.class, () -> uavService.createUav(testDto));

        verify(uavMapper, never()).insert(any(Uav.class));
    }

    @Test
    void getUavById_Success() {
        when(uavMapper.findById(1L)).thenReturn(testUav);

        UavDTO result = uavService.getUavById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(testUav.getName(), result.getName());
    }

    @Test
    void getUavById_NotFound() {
        when(uavMapper.findById(999L)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> uavService.getUavById(999L));
    }

    @Test
    void updateUav_Success() {
        when(uavMapper.findById(1L)).thenReturn(testUav);
        when(uavMapper.findBySerialNumber(testDto.getSerialNumber())).thenReturn(testUav);
        when(uavMapper.update(any(Uav.class))).thenReturn(1);

        UavDTO result = uavService.updateUav(1L, testDto);

        assertNotNull(result);
        assertEquals(testDto.getName(), result.getName());
        verify(uavMapper).update(any(Uav.class));
    }

    @Test
    void deleteUav_Success() {
        when(uavMapper.findById(1L)).thenReturn(testUav);
        when(uavMapper.deleteById(1L)).thenReturn(1);

        assertDoesNotThrow(() -> uavService.deleteUav(1L));

        verify(uavMapper).deleteById(1L);
    }

    @Test
    void listUavs_Success() {
        List<Uav> uavs = Arrays.asList(testUav);
        when(uavMapper.findAll(0, 10)).thenReturn(uavs);
        when(uavMapper.count()).thenReturn(1L);

        PageResult<UavDTO> result = uavService.listUavs(1, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getPage());
        assertEquals(10, result.getSize());
    }
}
