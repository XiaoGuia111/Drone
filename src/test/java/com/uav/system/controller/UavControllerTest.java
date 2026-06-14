package com.uav.system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uav.system.domain.dto.UavDTO;
import com.uav.system.domain.vo.PageResult;
import com.uav.system.service.api.AiUavAttributeService;
import com.uav.system.service.api.UavService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 无人机控制器（UavController）单元测试
 *
 * <p>
 * 使用 MockMvc 独立测试 Controller 层，
 * 通过 Mockito 模拟所有 Service 依赖。
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class UavControllerTest {

        private MockMvc mockMvc;

        @Mock
        private UavService uavService;

        @Mock
        private AiUavAttributeService aiUavAttributeService;

        @InjectMocks
        private UavController uavController;

        private ObjectMapper objectMapper;
        private UavDTO testDto;

        @BeforeEach
        void setUp() {
                mockMvc = MockMvcBuilders.standaloneSetup(uavController).build();
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
        void shouldReturnUavList() throws Exception {
                PageResult<UavDTO> pageResult = new PageResult<>(
                                Arrays.asList(testDto), 1L, 1, 10);
                when(uavService.listUavs(eq(1), eq(10))).thenReturn(pageResult);

                mockMvc.perform(get("/api/uavs")
                                .param("page", "1")
                                .param("size", "10"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.data.content[0].name").value("TestUAV"));
        }

        @Test
        void shouldReturnUavById() throws Exception {
                when(uavService.getUavById(1L)).thenReturn(testDto);

                mockMvc.perform(get("/api/uavs/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.data.name").value("TestUAV"));
        }

        @Test
        void shouldCreateUav() throws Exception {
                when(uavService.createUav(any(UavDTO.class))).thenReturn(testDto);

                mockMvc.perform(post("/api/uavs")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testDto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        void shouldUpdateUav() throws Exception {
                when(uavService.updateUav(eq(1L), any(UavDTO.class))).thenReturn(testDto);

                mockMvc.perform(put("/api/uavs/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testDto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        void shouldDeleteUav() throws Exception {
                mockMvc.perform(delete("/api/uavs/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        void shouldGenerateAiAttributes() throws Exception {
                when(aiUavAttributeService.generateAttributes("TestUAV", "Model-X"))
                                .thenReturn(testDto);

                Map<String, String> request = new HashMap<>();
                request.put("name", "TestUAV");
                request.put("model", "Model-X");

                mockMvc.perform(post("/api/uavs/ai-generate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200));
        }
}
