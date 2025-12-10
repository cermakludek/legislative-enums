package cz.intelis.legislativeenums.cuzk.landuse;

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
@DisplayName("LandUseApiController Unit Tests")
class LandUseApiControllerTest {

    @Mock
    private LandUseService service;

    @InjectMocks
    private LandUseApiController controller;

    private LandUseDTO testDTO;

    @BeforeEach
    void setUp() {
        testDTO = new LandUseDTO();
        testDTO.setId(1L);
        testDTO.setCode("1");
        testDTO.setNameCs("zemědělský půdní fond");
        testDTO.setNameEn("agricultural land fund");
        testDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("Should return all land uses")
    void shouldReturnAllLandUses() {
        // Given
        LandUseDTO dto2 = new LandUseDTO();
        dto2.setId(2L);
        dto2.setCode("2");
        dto2.setNameCs("lesní půdní fond");
        List<LandUseDTO> landUses = Arrays.asList(testDTO, dto2);
        when(service.findAll()).thenReturn(landUses);

        // When
        ResponseEntity<List<LandUseDTO>> response = controller.findAll(false);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getCode()).isEqualTo("1");
        assertThat(response.getBody().get(1).getCode()).isEqualTo("2");
        verify(service, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return only currently valid land uses when validOnly is true")
    void shouldReturnOnlyValidLandUsesWhenValidOnlyIsTrue() {
        // Given
        List<LandUseDTO> validLandUses = Arrays.asList(testDTO);
        when(service.findAllCurrentlyValid()).thenReturn(validLandUses);

        // When
        ResponseEntity<List<LandUseDTO>> response = controller.findAll(true);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        verify(service, times(1)).findAllCurrentlyValid();
    }

    @Test
    @DisplayName("Should return land use by ID")
    void shouldReturnLandUseById() {
        // Given
        when(service.findById(1L)).thenReturn(testDTO);

        // When
        ResponseEntity<LandUseDTO> response = controller.findById(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo("1");
        verify(service, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return land use by code")
    void shouldReturnLandUseByCode() {
        // Given
        when(service.findByCode("1")).thenReturn(testDTO);

        // When
        ResponseEntity<LandUseDTO> response = controller.findByCode("1");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo("1");
        verify(service, times(1)).findByCode("1");
    }

    @Test
    @DisplayName("Should create new land use")
    void shouldCreateNewLandUse() {
        // Given
        when(service.create(any(LandUseDTO.class))).thenReturn(testDTO);

        // When
        ResponseEntity<LandUseDTO> response = controller.create(testDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getCode()).isEqualTo("1");
        verify(service, times(1)).create(any(LandUseDTO.class));
    }

    @Test
    @DisplayName("Should update existing land use")
    void shouldUpdateExistingLandUse() {
        // Given
        testDTO.setNameCs("aktualizovaný zemědělský půdní fond");
        when(service.update(eq(1L), any(LandUseDTO.class))).thenReturn(testDTO);

        // When
        ResponseEntity<LandUseDTO> response = controller.update(1L, testDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getNameCs()).isEqualTo("aktualizovaný zemědělský půdní fond");
        verify(service, times(1)).update(eq(1L), any(LandUseDTO.class));
    }

    @Test
    @DisplayName("Should delete land use")
    void shouldDeleteLandUse() {
        // Given
        doNothing().when(service).delete(1L);

        // When
        ResponseEntity<Void> response = controller.delete(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(service, times(1)).delete(1L);
    }
}
