package cz.intelis.legislativeenums.cuzk.buildinguse;

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
@DisplayName("BuildingUseApiController Unit Tests")
class BuildingUseApiControllerTest {

    @Mock
    private BuildingUseService service;

    @InjectMocks
    private BuildingUseApiController controller;

    private BuildingUseDTO testDTO;

    @BeforeEach
    void setUp() {
        testDTO = new BuildingUseDTO();
        testDTO.setId(1L);
        testDTO.setCode("1");
        testDTO.setNameCs("bydlení");
        testDTO.setNameEn("residential");
        testDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("Should return all building uses")
    void shouldReturnAllBuildingUses() {
        // Given
        BuildingUseDTO dto2 = new BuildingUseDTO();
        dto2.setId(2L);
        dto2.setCode("2");
        dto2.setNameCs("obchod a služby");
        List<BuildingUseDTO> buildingUses = Arrays.asList(testDTO, dto2);
        when(service.findAll()).thenReturn(buildingUses);

        // When
        ResponseEntity<List<BuildingUseDTO>> response = controller.findAll(false);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getCode()).isEqualTo("1");
        assertThat(response.getBody().get(1).getCode()).isEqualTo("2");
        verify(service, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return only currently valid building uses when validOnly is true")
    void shouldReturnOnlyValidBuildingUsesWhenValidOnlyIsTrue() {
        // Given
        List<BuildingUseDTO> validBuildingUses = Arrays.asList(testDTO);
        when(service.findAllCurrentlyValid()).thenReturn(validBuildingUses);

        // When
        ResponseEntity<List<BuildingUseDTO>> response = controller.findAll(true);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        verify(service, times(1)).findAllCurrentlyValid();
    }

    @Test
    @DisplayName("Should return building use by ID")
    void shouldReturnBuildingUseById() {
        // Given
        when(service.findById(1L)).thenReturn(testDTO);

        // When
        ResponseEntity<BuildingUseDTO> response = controller.findById(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo("1");
        verify(service, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return building use by code")
    void shouldReturnBuildingUseByCode() {
        // Given
        when(service.findByCode("1")).thenReturn(testDTO);

        // When
        ResponseEntity<BuildingUseDTO> response = controller.findByCode("1");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo("1");
        verify(service, times(1)).findByCode("1");
    }

    @Test
    @DisplayName("Should create new building use")
    void shouldCreateNewBuildingUse() {
        // Given
        when(service.create(any(BuildingUseDTO.class))).thenReturn(testDTO);

        // When
        ResponseEntity<BuildingUseDTO> response = controller.create(testDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getCode()).isEqualTo("1");
        verify(service, times(1)).create(any(BuildingUseDTO.class));
    }

    @Test
    @DisplayName("Should update existing building use")
    void shouldUpdateExistingBuildingUse() {
        // Given
        testDTO.setNameCs("aktualizované bydlení");
        when(service.update(eq(1L), any(BuildingUseDTO.class))).thenReturn(testDTO);

        // When
        ResponseEntity<BuildingUseDTO> response = controller.update(1L, testDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getNameCs()).isEqualTo("aktualizované bydlení");
        verify(service, times(1)).update(eq(1L), any(BuildingUseDTO.class));
    }

    @Test
    @DisplayName("Should delete building use")
    void shouldDeleteBuildingUse() {
        // Given
        doNothing().when(service).delete(1L);

        // When
        ResponseEntity<Void> response = controller.delete(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(service, times(1)).delete(1L);
    }
}
