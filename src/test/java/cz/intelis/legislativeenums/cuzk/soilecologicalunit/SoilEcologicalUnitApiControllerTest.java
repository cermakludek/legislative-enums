package cz.intelis.legislativeenums.cuzk.soilecologicalunit;

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
@DisplayName("SoilEcologicalUnitApiController Unit Tests")
class SoilEcologicalUnitApiControllerTest {

    @Mock
    private SoilEcologicalUnitService service;

    @InjectMocks
    private SoilEcologicalUnitApiController controller;

    private SoilEcologicalUnitDTO testDTO;

    @BeforeEach
    void setUp() {
        testDTO = new SoilEcologicalUnitDTO();
        testDTO.setId(1L);
        testDTO.setCode("1.01.10");
        testDTO.setNameCs("Černozem");
        testDTO.setNameEn("Chernozem");
        testDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("Should return all soil ecological units")
    void shouldReturnAllSoilEcologicalUnits() {
        // Given
        SoilEcologicalUnitDTO dto2 = new SoilEcologicalUnitDTO();
        dto2.setId(2L);
        dto2.setCode("1.02.10");
        dto2.setNameCs("Hnědozem");
        List<SoilEcologicalUnitDTO> soilEcologicalUnits = Arrays.asList(testDTO, dto2);
        when(service.findAll()).thenReturn(soilEcologicalUnits);

        // When
        ResponseEntity<List<SoilEcologicalUnitDTO>> response = controller.findAll(false);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getCode()).isEqualTo("1.01.10");
        assertThat(response.getBody().get(1).getCode()).isEqualTo("1.02.10");
        verify(service, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return only currently valid soil ecological units when validOnly is true")
    void shouldReturnOnlyValidSoilEcologicalUnitsWhenValidOnlyIsTrue() {
        // Given
        List<SoilEcologicalUnitDTO> validSoilEcologicalUnits = Arrays.asList(testDTO);
        when(service.findAllCurrentlyValid()).thenReturn(validSoilEcologicalUnits);

        // When
        ResponseEntity<List<SoilEcologicalUnitDTO>> response = controller.findAll(true);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        verify(service, times(1)).findAllCurrentlyValid();
    }

    @Test
    @DisplayName("Should return soil ecological unit by ID")
    void shouldReturnSoilEcologicalUnitById() {
        // Given
        when(service.findById(1L)).thenReturn(testDTO);

        // When
        ResponseEntity<SoilEcologicalUnitDTO> response = controller.findById(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo("1.01.10");
        verify(service, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return soil ecological unit by code")
    void shouldReturnSoilEcologicalUnitByCode() {
        // Given
        when(service.findByCode("1.01.10")).thenReturn(testDTO);

        // When
        ResponseEntity<SoilEcologicalUnitDTO> response = controller.findByCode("1.01.10");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo("1.01.10");
        verify(service, times(1)).findByCode("1.01.10");
    }

    @Test
    @DisplayName("Should create new soil ecological unit")
    void shouldCreateNewSoilEcologicalUnit() {
        // Given
        when(service.create(any(SoilEcologicalUnitDTO.class))).thenReturn(testDTO);

        // When
        ResponseEntity<SoilEcologicalUnitDTO> response = controller.create(testDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getCode()).isEqualTo("1.01.10");
        verify(service, times(1)).create(any(SoilEcologicalUnitDTO.class));
    }

    @Test
    @DisplayName("Should update existing soil ecological unit")
    void shouldUpdateExistingSoilEcologicalUnit() {
        // Given
        testDTO.setNameCs("aktualizovaný Černozem");
        when(service.update(eq(1L), any(SoilEcologicalUnitDTO.class))).thenReturn(testDTO);

        // When
        ResponseEntity<SoilEcologicalUnitDTO> response = controller.update(1L, testDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getNameCs()).isEqualTo("aktualizovaný Černozem");
        verify(service, times(1)).update(eq(1L), any(SoilEcologicalUnitDTO.class));
    }

    @Test
    @DisplayName("Should delete soil ecological unit")
    void shouldDeleteSoilEcologicalUnit() {
        // Given
        doNothing().when(service).delete(1L);

        // When
        ResponseEntity<Void> response = controller.delete(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(service, times(1)).delete(1L);
    }
}
