package com.uav.system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uav.system.config.WebMvcConfig;
import com.uav.system.domain.dto.UavDTO;

import com.uav.system.domain.vo.PageResult;
import com.uav.system.interceptor.LoggingInterceptor;
import com.uav.system.repository.mapper.UavMapper;
import com.uav.system.repository.mapper.UserMapper;
import com.uav.system.service.api.UavService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 无人机控制器（UavController）单元测试
 *
 * <p>使用 {@link WebMvcTest} 仅加载 Controller 层上下文，
 * 通过 {@link MockBean} 模拟 Service 层的依赖，测试各 API 端点的
 * HTTP 状态码和响应体 JSON 结构。</p>
 *
 * <p>测试覆盖：创建、查询（单条/分页）、更新、删除等所有 REST 端点。</p>
 *
 * @see UavController
 * @see UavService
 */
@WebMvcTest(value = UavController.class,
        excludeAutoConfiguration = MybatisAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {LoggingInterceptor.class, WebMvcConfig.class}
        ))
class UavControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UavService uavService;

    @MockBean
    private UavMapper uavMapper;

    @MockBean
    private UserMapper userMapper;

    private ObjectMapper objectMapper;
    private UavDTO testDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        testDto = new UavDTO();
        testDto.setId(1L);
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
    }

    @Test
    void createUav_Success() throws Exception {
        when(uavService.createUav(any(UavDTO.class))).thenReturn(testDto);

        mockMvc.perform(post("/api/uavs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("TestUAV"));
    }

    @Test
    void getUavById_Success() throws Exception {
        when(uavService.getUavById(1L)).thenReturn(testDto);

        mockMvc.perform(get("/api/uavs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void listUavs_Success() throws Exception {
        PageResult<UavDTO> pageResult = new PageResult<>(
                Arrays.asList(testDto), 1, 1, 10
        );
        when(uavService.listUavs(1, 10)).thenReturn(pageResult);

        mockMvc.perform(get("/api/uavs")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content[0].name").value("TestUAV"));
    }

    @Test
    void updateUav_Success() throws Exception {
        when(uavService.updateUav(eq(1L), any(UavDTO.class))).thenReturn(testDto);

        mockMvc.perform(put("/api/uavs/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void deleteUav_Success() throws Exception {
        mockMvc.perform(delete("/api/uavs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
