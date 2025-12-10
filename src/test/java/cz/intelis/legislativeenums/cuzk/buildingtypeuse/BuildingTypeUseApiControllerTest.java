package cz.intelis.legislativeenums.cuzk.buildingtypeuse;

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
@DisplayName("BuildingTypeUseApiController Unit Tests")
class BuildingTypeUseApiControllerTest {

    @Mock
    private BuildingTypeUseService service;

    @InjectMocks
    private BuildingTypeUseApiController controller;

    private BuildingTypeUseDTO testDTO;

    @BeforeEach
    void setUp() {
        testDTO = new BuildingTypeUseDTO();
        testDTO.setId(1L);
        testDTO.setBuildingTypeCode("1");
        testDTO.setBuildingUseCode("1");
    }

    @Test
    @DisplayName("Should return all building type uses")
    void shouldReturnAllBuildingTypeUses() {
        // Given
        BuildingTypeUseDTO dto2 = new BuildingTypeUseDTO();
        dto2.setId(2L);
        dto2.setBuildingTypeCode("2");
        dto2.setBuildingUseCode("2");
        List<BuildingTypeUseDTO> buildingTypeUses = Arrays.asList(testDTO, dto2);
        when(service.findAll()).thenReturn(buildingTypeUses);

        // When
        ResponseEntity<List<BuildingTypeUseDTO>> response = controller.findAll(false);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getBuildingTypeCode()).isEqualTo("1");
        assertThat(response.getBody().get(1).getBuildingTypeCode()).isEqualTo("2");
        verify(service, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return only currently valid building type uses when validOnly is true")
    void shouldReturnOnlyValidBuildingTypeUsesWhenValidOnlyIsTrue() {
        // Given
        List<BuildingTypeUseDTO> validBuildingTypeUses = Arrays.asList(testDTO);
        when(service.findAllCurrentlyValid()).thenReturn(validBuildingTypeUses);

        // When
        ResponseEntity<List<BuildingTypeUseDTO>> response = controller.findAll(true);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        verify(service, times(1)).findAllCurrentlyValid();
    }

    @Test
    @DisplayName("Should return building type use by ID")
    void shouldReturnBuildingTypeUseById() {
        // Given
        when(service.findById(1L)).thenReturn(testDTO);

        // When
        ResponseEntity<BuildingTypeUseDTO> response = controller.findById(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getBuildingTypeCode()).isEqualTo("1");
        verify(service, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return building type uses by building type code")
    void shouldReturnBuildingTypeUsesByBuildingTypeCode() {
        // Given
        List<BuildingTypeUseDTO> buildingTypeUses = Arrays.asList(testDTO);
        when(service.findByBuildingTypeCode("1")).thenReturn(buildingTypeUses);

        // When
        ResponseEntity<List<BuildingTypeUseDTO>> response = controller.findByBuildingTypeCode("1");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        verify(service, times(1)).findByBuildingTypeCode("1");
    }

    @Test
    @DisplayName("Should return building type uses by building use code")
    void shouldReturnBuildingTypeUsesByBuildingUseCode() {
        // Given
        List<BuildingTypeUseDTO> buildingTypeUses = Arrays.asList(testDTO);
        when(service.findByBuildingUseCode("1")).thenReturn(buildingTypeUses);

        // When
        ResponseEntity<List<BuildingTypeUseDTO>> response = controller.findByBuildingUseCode("1");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        verify(service, times(1)).findByBuildingUseCode("1");
    }

    @Test
    @DisplayName("Should create new building type use")
    void shouldCreateNewBuildingTypeUse() {
        // Given
        when(service.create(any(BuildingTypeUseDTO.class))).thenReturn(testDTO);

        // When
        ResponseEntity<BuildingTypeUseDTO> response = controller.create(testDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getBuildingTypeCode()).isEqualTo("1");
        verify(service, times(1)).create(any(BuildingTypeUseDTO.class));
    }

    @Test
    @DisplayName("Should update existing building type use")
    void shouldUpdateExistingBuildingTypeUse() {
        // Given
        testDTO.setBuildingUseCode("2");
        when(service.update(eq(1L), any(BuildingTypeUseDTO.class))).thenReturn(testDTO);

        // When
        ResponseEntity<BuildingTypeUseDTO> response = controller.update(1L, testDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getBuildingUseCode()).isEqualTo("2");
        verify(service, times(1)).update(eq(1L), any(BuildingTypeUseDTO.class));
    }

    @Test
    @DisplayName("Should delete building type use")
    void shouldDeleteBuildingTypeUse() {
        // Given
        doNothing().when(service).delete(1L);

        // When
        ResponseEntity<Void> response = controller.delete(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(service, times(1)).delete(1L);
    }
}
