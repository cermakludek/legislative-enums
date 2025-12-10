package cz.intelis.legislativeenums.voltagelevel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VoltageLevelApiController Unit Tests")
class VoltageLevelApiControllerTest {

    @Mock
    private VoltageLevelService voltageLevelService;

    @InjectMocks
    private VoltageLevelApiController controller;

    private VoltageLevelDTO testVoltageLevelDTO;

    @BeforeEach
    void setUp() {
        testVoltageLevelDTO = new VoltageLevelDTO();
        testVoltageLevelDTO.setId(1L);
        testVoltageLevelDTO.setCode("NN");
        testVoltageLevelDTO.setNameCs("Nízké napětí");
        testVoltageLevelDTO.setNameEn("Low voltage");
        testVoltageLevelDTO.setVoltageRangeCs("do 1 kV");
        testVoltageLevelDTO.setVoltageRangeEn("up to 1 kV");
        testVoltageLevelDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("Should return all voltage levels")
    void shouldReturnAllVoltageLevels() {
        // Given
        VoltageLevelDTO dto2 = new VoltageLevelDTO();
        dto2.setId(2L);
        dto2.setCode("VN");
        dto2.setNameCs("Vysoké napětí");
        List<VoltageLevelDTO> voltageLevels = Arrays.asList(testVoltageLevelDTO, dto2);
        when(voltageLevelService.findAll()).thenReturn(voltageLevels);

        // When
        ResponseEntity<List<VoltageLevelDTO>> response = controller.findAll(false);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getCode()).isEqualTo("NN");
        verify(voltageLevelService, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return only currently valid voltage levels when validOnly is true")
    void shouldReturnOnlyValidVoltageLevelsWhenValidOnlyIsTrue() {
        // Given
        List<VoltageLevelDTO> validVoltageLevels = Arrays.asList(testVoltageLevelDTO);
        when(voltageLevelService.findAllCurrentlyValid()).thenReturn(validVoltageLevels);

        // When
        ResponseEntity<List<VoltageLevelDTO>> response = controller.findAll(true);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        verify(voltageLevelService, times(1)).findAllCurrentlyValid();
    }

    @Test
    @DisplayName("Should return voltage level by ID")
    void shouldReturnVoltageLevelById() {
        // Given
        when(voltageLevelService.findById(1L)).thenReturn(testVoltageLevelDTO);

        // When
        ResponseEntity<VoltageLevelDTO> response = controller.findById(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo("NN");
        verify(voltageLevelService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return voltage level by code")
    void shouldReturnVoltageLevelByCode() {
        // Given
        when(voltageLevelService.findByCode("NN")).thenReturn(testVoltageLevelDTO);

        // When
        ResponseEntity<VoltageLevelDTO> response = controller.findByCode("NN");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo("NN");
        verify(voltageLevelService, times(1)).findByCode("NN");
    }

    @Test
    @DisplayName("Should create new voltage level")
    void shouldCreateNewVoltageLevel() {
        // Given
        when(voltageLevelService.create(any(VoltageLevelDTO.class))).thenReturn(testVoltageLevelDTO);

        // When
        ResponseEntity<VoltageLevelDTO> response = controller.create(testVoltageLevelDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getCode()).isEqualTo("NN");
        verify(voltageLevelService, times(1)).create(any(VoltageLevelDTO.class));
    }

    @Test
    @DisplayName("Should update existing voltage level")
    void shouldUpdateExistingVoltageLevel() {
        // Given
        testVoltageLevelDTO.setNameCs("Aktualizované nízké napětí");
        when(voltageLevelService.update(eq(1L), any(VoltageLevelDTO.class))).thenReturn(testVoltageLevelDTO);

        // When
        ResponseEntity<VoltageLevelDTO> response = controller.update(1L, testVoltageLevelDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getNameCs()).isEqualTo("Aktualizované nízké napětí");
        verify(voltageLevelService, times(1)).update(eq(1L), any(VoltageLevelDTO.class));
    }

    @Test
    @DisplayName("Should delete voltage level")
    void shouldDeleteVoltageLevel() {
        // Given
        doNothing().when(voltageLevelService).delete(1L);

        // When
        ResponseEntity<Void> response = controller.delete(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(voltageLevelService, times(1)).delete(1L);
    }
}
