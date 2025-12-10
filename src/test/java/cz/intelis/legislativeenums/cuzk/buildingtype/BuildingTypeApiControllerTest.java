package cz.intelis.legislativeenums.cuzk.buildingtype;

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
@DisplayName("BuildingTypeApiController Unit Tests")
class BuildingTypeApiControllerTest {

    @Mock
    private BuildingTypeService service;

    @InjectMocks
    private BuildingTypeApiController controller;

    private BuildingTypeDTO testDTO;

    @BeforeEach
    void setUp() {
        testDTO = new BuildingTypeDTO();
        testDTO.setId(1L);
        testDTO.setCode("1");
        testDTO.setNameCs("budova");
        testDTO.setNameEn("building");
        testDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("Should return all building types")
    void shouldReturnAllBuildingTypes() {
        // Given
        BuildingTypeDTO dto2 = new BuildingTypeDTO();
        dto2.setId(2L);
        dto2.setCode("2");
        dto2.setNameCs("inženýrská stavba");
        List<BuildingTypeDTO> buildingTypes = Arrays.asList(testDTO, dto2);
        when(service.findAll()).thenReturn(buildingTypes);

        // When
        ResponseEntity<List<BuildingTypeDTO>> response = controller.findAll(false);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getCode()).isEqualTo("1");
        assertThat(response.getBody().get(1).getCode()).isEqualTo("2");
        verify(service, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return only currently valid building types when validOnly is true")
    void shouldReturnOnlyValidBuildingTypesWhenValidOnlyIsTrue() {
        // Given
        List<BuildingTypeDTO> validBuildingTypes = Arrays.asList(testDTO);
        when(service.findAllCurrentlyValid()).thenReturn(validBuildingTypes);

        // When
        ResponseEntity<List<BuildingTypeDTO>> response = controller.findAll(true);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        verify(service, times(1)).findAllCurrentlyValid();
    }

    @Test
    @DisplayName("Should return building type by ID")
    void shouldReturnBuildingTypeById() {
        // Given
        when(service.findById(1L)).thenReturn(testDTO);

        // When
        ResponseEntity<BuildingTypeDTO> response = controller.findById(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo("1");
        verify(service, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return building type by code")
    void shouldReturnBuildingTypeByCode() {
        // Given
        when(service.findByCode("1")).thenReturn(testDTO);

        // When
        ResponseEntity<BuildingTypeDTO> response = controller.findByCode("1");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo("1");
        verify(service, times(1)).findByCode("1");
    }

    @Test
    @DisplayName("Should create new building type")
    void shouldCreateNewBuildingType() {
        // Given
        when(service.create(any(BuildingTypeDTO.class))).thenReturn(testDTO);

        // When
        ResponseEntity<BuildingTypeDTO> response = controller.create(testDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getCode()).isEqualTo("1");
        verify(service, times(1)).create(any(BuildingTypeDTO.class));
    }

    @Test
    @DisplayName("Should update existing building type")
    void shouldUpdateExistingBuildingType() {
        // Given
        testDTO.setNameCs("aktualizovaná budova");
        when(service.update(eq(1L), any(BuildingTypeDTO.class))).thenReturn(testDTO);

        // When
        ResponseEntity<BuildingTypeDTO> response = controller.update(1L, testDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getNameCs()).isEqualTo("aktualizovaná budova");
        verify(service, times(1)).update(eq(1L), any(BuildingTypeDTO.class));
    }

    @Test
    @DisplayName("Should delete building type")
    void shouldDeleteBuildingType() {
        // Given
        doNothing().when(service).delete(1L);

        // When
        ResponseEntity<Void> response = controller.delete(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(service, times(1)).delete(1L);
    }
}
