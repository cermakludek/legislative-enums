package cz.intelis.legislativeenums.cuzk.landtype;

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
@DisplayName("LandTypeApiController Unit Tests")
class LandTypeApiControllerTest {

    @Mock
    private LandTypeService service;

    @InjectMocks
    private LandTypeApiController controller;

    private LandTypeDTO testDTO;

    @BeforeEach
    void setUp() {
        testDTO = new LandTypeDTO();
        testDTO.setId(1L);
        testDTO.setCode("2");
        testDTO.setNameCs("orná půda");
        testDTO.setNameEn("arable land");
        testDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("Should return all land types")
    void shouldReturnAllLandTypes() {
        // Given
        LandTypeDTO dto2 = new LandTypeDTO();
        dto2.setId(2L);
        dto2.setCode("3");
        dto2.setNameCs("chmelnice");
        List<LandTypeDTO> landTypes = Arrays.asList(testDTO, dto2);
        when(service.findAll()).thenReturn(landTypes);

        // When
        ResponseEntity<List<LandTypeDTO>> response = controller.findAll(false);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getCode()).isEqualTo("2");
        assertThat(response.getBody().get(1).getCode()).isEqualTo("3");
        verify(service, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return only currently valid land types when validOnly is true")
    void shouldReturnOnlyValidLandTypesWhenValidOnlyIsTrue() {
        // Given
        List<LandTypeDTO> validLandTypes = Arrays.asList(testDTO);
        when(service.findAllCurrentlyValid()).thenReturn(validLandTypes);

        // When
        ResponseEntity<List<LandTypeDTO>> response = controller.findAll(true);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        verify(service, times(1)).findAllCurrentlyValid();
    }

    @Test
    @DisplayName("Should return land type by ID")
    void shouldReturnLandTypeById() {
        // Given
        when(service.findById(1L)).thenReturn(testDTO);

        // When
        ResponseEntity<LandTypeDTO> response = controller.findById(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo("2");
        verify(service, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return land type by code")
    void shouldReturnLandTypeByCode() {
        // Given
        when(service.findByCode("2")).thenReturn(testDTO);

        // When
        ResponseEntity<LandTypeDTO> response = controller.findByCode("2");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo("2");
        verify(service, times(1)).findByCode("2");
    }

    @Test
    @DisplayName("Should create new land type")
    void shouldCreateNewLandType() {
        // Given
        when(service.create(any(LandTypeDTO.class))).thenReturn(testDTO);

        // When
        ResponseEntity<LandTypeDTO> response = controller.create(testDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getCode()).isEqualTo("2");
        verify(service, times(1)).create(any(LandTypeDTO.class));
    }

    @Test
    @DisplayName("Should update existing land type")
    void shouldUpdateExistingLandType() {
        // Given
        testDTO.setNameCs("aktualizovaná orná půda");
        when(service.update(eq(1L), any(LandTypeDTO.class))).thenReturn(testDTO);

        // When
        ResponseEntity<LandTypeDTO> response = controller.update(1L, testDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getNameCs()).isEqualTo("aktualizovaná orná půda");
        verify(service, times(1)).update(eq(1L), any(LandTypeDTO.class));
    }

    @Test
    @DisplayName("Should delete land type")
    void shouldDeleteLandType() {
        // Given
        doNothing().when(service).delete(1L);

        // When
        ResponseEntity<Void> response = controller.delete(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(service, times(1)).delete(1L);
    }
}
