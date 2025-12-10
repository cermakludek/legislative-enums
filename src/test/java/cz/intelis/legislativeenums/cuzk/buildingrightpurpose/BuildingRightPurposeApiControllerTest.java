package cz.intelis.legislativeenums.cuzk.buildingrightpurpose;

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
@DisplayName("BuildingRightPurposeApiController Unit Tests")
class BuildingRightPurposeApiControllerTest {

    @Mock
    private BuildingRightPurposeService service;

    @InjectMocks
    private BuildingRightPurposeApiController controller;

    private BuildingRightPurposeDTO testDTO;

    @BeforeEach
    void setUp() {
        testDTO = new BuildingRightPurposeDTO();
        testDTO.setId(1L);
        testDTO.setCode("1");
        testDTO.setNameCs("bydlení");
        testDTO.setNameEn("residential");
        testDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("Should return all building right purposes")
    void shouldReturnAllBuildingRightPurposes() {
        // Given
        BuildingRightPurposeDTO dto2 = new BuildingRightPurposeDTO();
        dto2.setId(2L);
        dto2.setCode("2");
        dto2.setNameCs("podnikání");
        List<BuildingRightPurposeDTO> buildingRightPurposes = Arrays.asList(testDTO, dto2);
        when(service.findAll()).thenReturn(buildingRightPurposes);

        // When
        ResponseEntity<List<BuildingRightPurposeDTO>> response = controller.findAll(false);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getCode()).isEqualTo("1");
        assertThat(response.getBody().get(1).getCode()).isEqualTo("2");
        verify(service, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return only currently valid building right purposes when validOnly is true")
    void shouldReturnOnlyValidBuildingRightPurposesWhenValidOnlyIsTrue() {
        // Given
        List<BuildingRightPurposeDTO> validBuildingRightPurposes = Arrays.asList(testDTO);
        when(service.findAllCurrentlyValid()).thenReturn(validBuildingRightPurposes);

        // When
        ResponseEntity<List<BuildingRightPurposeDTO>> response = controller.findAll(true);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        verify(service, times(1)).findAllCurrentlyValid();
    }

    @Test
    @DisplayName("Should return building right purpose by ID")
    void shouldReturnBuildingRightPurposeById() {
        // Given
        when(service.findById(1L)).thenReturn(testDTO);

        // When
        ResponseEntity<BuildingRightPurposeDTO> response = controller.findById(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo("1");
        verify(service, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return building right purpose by code")
    void shouldReturnBuildingRightPurposeByCode() {
        // Given
        when(service.findByCode("1")).thenReturn(testDTO);

        // When
        ResponseEntity<BuildingRightPurposeDTO> response = controller.findByCode("1");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo("1");
        verify(service, times(1)).findByCode("1");
    }

    @Test
    @DisplayName("Should create new building right purpose")
    void shouldCreateNewBuildingRightPurpose() {
        // Given
        when(service.create(any(BuildingRightPurposeDTO.class))).thenReturn(testDTO);

        // When
        ResponseEntity<BuildingRightPurposeDTO> response = controller.create(testDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getCode()).isEqualTo("1");
        verify(service, times(1)).create(any(BuildingRightPurposeDTO.class));
    }

    @Test
    @DisplayName("Should update existing building right purpose")
    void shouldUpdateExistingBuildingRightPurpose() {
        // Given
        testDTO.setNameCs("aktualizované bydlení");
        when(service.update(eq(1L), any(BuildingRightPurposeDTO.class))).thenReturn(testDTO);

        // When
        ResponseEntity<BuildingRightPurposeDTO> response = controller.update(1L, testDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getNameCs()).isEqualTo("aktualizované bydlení");
        verify(service, times(1)).update(eq(1L), any(BuildingRightPurposeDTO.class));
    }

    @Test
    @DisplayName("Should delete building right purpose")
    void shouldDeleteBuildingRightPurpose() {
        // Given
        doNothing().when(service).delete(1L);

        // When
        ResponseEntity<Void> response = controller.delete(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(service, times(1)).delete(1L);
    }
}
