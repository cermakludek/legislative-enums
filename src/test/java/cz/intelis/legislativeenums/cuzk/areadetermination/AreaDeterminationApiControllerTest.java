package cz.intelis.legislativeenums.cuzk.areadetermination;

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
@DisplayName("AreaDeterminationApiController Unit Tests")
class AreaDeterminationApiControllerTest {

    @Mock
    private AreaDeterminationService service;

    @InjectMocks
    private AreaDeterminationApiController controller;

    private AreaDeterminationDTO testDTO;

    @BeforeEach
    void setUp() {
        testDTO = new AreaDeterminationDTO();
        testDTO.setId(1L);
        testDTO.setCode("1");
        testDTO.setNameCs("ze souřadnic");
        testDTO.setNameEn("from coordinates");
        testDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("Should return all area determinations")
    void shouldReturnAllAreaDeterminations() {
        // Given
        AreaDeterminationDTO dto2 = new AreaDeterminationDTO();
        dto2.setId(2L);
        dto2.setCode("2");
        dto2.setNameCs("z geodetických údajů");
        List<AreaDeterminationDTO> areaDeterminations = Arrays.asList(testDTO, dto2);
        when(service.findAll()).thenReturn(areaDeterminations);

        // When
        ResponseEntity<List<AreaDeterminationDTO>> response = controller.findAll(false);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getCode()).isEqualTo("1");
        assertThat(response.getBody().get(1).getCode()).isEqualTo("2");
        verify(service, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return only currently valid area determinations when validOnly is true")
    void shouldReturnOnlyValidAreaDeterminationsWhenValidOnlyIsTrue() {
        // Given
        List<AreaDeterminationDTO> validAreaDeterminations = Arrays.asList(testDTO);
        when(service.findAllCurrentlyValid()).thenReturn(validAreaDeterminations);

        // When
        ResponseEntity<List<AreaDeterminationDTO>> response = controller.findAll(true);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        verify(service, times(1)).findAllCurrentlyValid();
    }

    @Test
    @DisplayName("Should return area determination by ID")
    void shouldReturnAreaDeterminationById() {
        // Given
        when(service.findById(1L)).thenReturn(testDTO);

        // When
        ResponseEntity<AreaDeterminationDTO> response = controller.findById(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo("1");
        verify(service, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return area determination by code")
    void shouldReturnAreaDeterminationByCode() {
        // Given
        when(service.findByCode("1")).thenReturn(testDTO);

        // When
        ResponseEntity<AreaDeterminationDTO> response = controller.findByCode("1");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo("1");
        verify(service, times(1)).findByCode("1");
    }

    @Test
    @DisplayName("Should create new area determination")
    void shouldCreateNewAreaDetermination() {
        // Given
        when(service.create(any(AreaDeterminationDTO.class))).thenReturn(testDTO);

        // When
        ResponseEntity<AreaDeterminationDTO> response = controller.create(testDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getCode()).isEqualTo("1");
        verify(service, times(1)).create(any(AreaDeterminationDTO.class));
    }

    @Test
    @DisplayName("Should update existing area determination")
    void shouldUpdateExistingAreaDetermination() {
        // Given
        testDTO.setNameCs("aktualizováno ze souřadnic");
        when(service.update(eq(1L), any(AreaDeterminationDTO.class))).thenReturn(testDTO);

        // When
        ResponseEntity<AreaDeterminationDTO> response = controller.update(1L, testDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getNameCs()).isEqualTo("aktualizováno ze souřadnic");
        verify(service, times(1)).update(eq(1L), any(AreaDeterminationDTO.class));
    }

    @Test
    @DisplayName("Should delete area determination")
    void shouldDeleteAreaDetermination() {
        // Given
        doNothing().when(service).delete(1L);

        // When
        ResponseEntity<Void> response = controller.delete(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(service, times(1)).delete(1L);
    }
}
