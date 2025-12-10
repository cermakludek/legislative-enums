package cz.intelis.legislativeenums.cuzk.landtypeuse;

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
@DisplayName("LandTypeUseApiController Unit Tests")
class LandTypeUseApiControllerTest {

    @Mock
    private LandTypeUseService service;

    @InjectMocks
    private LandTypeUseApiController controller;

    private LandTypeUseDTO testDTO;

    @BeforeEach
    void setUp() {
        testDTO = new LandTypeUseDTO();
        testDTO.setId(1L);
        testDTO.setLandTypeCode("2");
        testDTO.setLandUseCode("1");
    }

    @Test
    @DisplayName("Should return all land type uses")
    void shouldReturnAllLandTypeUses() {
        // Given
        LandTypeUseDTO dto2 = new LandTypeUseDTO();
        dto2.setId(2L);
        dto2.setLandTypeCode("3");
        dto2.setLandUseCode("2");
        List<LandTypeUseDTO> landTypeUses = Arrays.asList(testDTO, dto2);
        when(service.findAll()).thenReturn(landTypeUses);

        // When
        ResponseEntity<List<LandTypeUseDTO>> response = controller.findAll(false);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getLandTypeCode()).isEqualTo("2");
        assertThat(response.getBody().get(1).getLandTypeCode()).isEqualTo("3");
        verify(service, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return only currently valid land type uses when validOnly is true")
    void shouldReturnOnlyValidLandTypeUsesWhenValidOnlyIsTrue() {
        // Given
        List<LandTypeUseDTO> validLandTypeUses = Arrays.asList(testDTO);
        when(service.findAllCurrentlyValid()).thenReturn(validLandTypeUses);

        // When
        ResponseEntity<List<LandTypeUseDTO>> response = controller.findAll(true);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        verify(service, times(1)).findAllCurrentlyValid();
    }

    @Test
    @DisplayName("Should return land type use by ID")
    void shouldReturnLandTypeUseById() {
        // Given
        when(service.findById(1L)).thenReturn(testDTO);

        // When
        ResponseEntity<LandTypeUseDTO> response = controller.findById(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getLandTypeCode()).isEqualTo("2");
        verify(service, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return land type uses by land type code")
    void shouldReturnLandTypeUsesByLandTypeCode() {
        // Given
        List<LandTypeUseDTO> landTypeUses = Arrays.asList(testDTO);
        when(service.findByLandTypeCode("2")).thenReturn(landTypeUses);

        // When
        ResponseEntity<List<LandTypeUseDTO>> response = controller.findByLandTypeCode("2");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        verify(service, times(1)).findByLandTypeCode("2");
    }

    @Test
    @DisplayName("Should return land type uses by land use code")
    void shouldReturnLandTypeUsesByLandUseCode() {
        // Given
        List<LandTypeUseDTO> landTypeUses = Arrays.asList(testDTO);
        when(service.findByLandUseCode("1")).thenReturn(landTypeUses);

        // When
        ResponseEntity<List<LandTypeUseDTO>> response = controller.findByLandUseCode("1");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        verify(service, times(1)).findByLandUseCode("1");
    }

    @Test
    @DisplayName("Should create new land type use")
    void shouldCreateNewLandTypeUse() {
        // Given
        when(service.create(any(LandTypeUseDTO.class))).thenReturn(testDTO);

        // When
        ResponseEntity<LandTypeUseDTO> response = controller.create(testDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getLandTypeCode()).isEqualTo("2");
        verify(service, times(1)).create(any(LandTypeUseDTO.class));
    }

    @Test
    @DisplayName("Should update existing land type use")
    void shouldUpdateExistingLandTypeUse() {
        // Given
        testDTO.setLandUseCode("2");
        when(service.update(eq(1L), any(LandTypeUseDTO.class))).thenReturn(testDTO);

        // When
        ResponseEntity<LandTypeUseDTO> response = controller.update(1L, testDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getLandUseCode()).isEqualTo("2");
        verify(service, times(1)).update(eq(1L), any(LandTypeUseDTO.class));
    }

    @Test
    @DisplayName("Should delete land type use")
    void shouldDeleteLandTypeUse() {
        // Given
        doNothing().when(service).delete(1L);

        // When
        ResponseEntity<Void> response = controller.delete(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(service, times(1)).delete(1L);
    }
}
