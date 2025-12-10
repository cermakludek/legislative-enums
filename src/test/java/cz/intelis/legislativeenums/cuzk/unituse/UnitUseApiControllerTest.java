package cz.intelis.legislativeenums.cuzk.unituse;

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
@DisplayName("UnitUseApiController Unit Tests")
class UnitUseApiControllerTest {

    @Mock
    private UnitUseService service;

    @InjectMocks
    private UnitUseApiController controller;

    private UnitUseDTO testDTO;

    @BeforeEach
    void setUp() {
        testDTO = new UnitUseDTO();
        testDTO.setId(1L);
        testDTO.setCode("1");
        testDTO.setNameCs("byt");
        testDTO.setNameEn("apartment");
        testDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("Should return all unit uses")
    void shouldReturnAllUnitUses() {
        // Given
        UnitUseDTO dto2 = new UnitUseDTO();
        dto2.setId(2L);
        dto2.setCode("2");
        dto2.setNameCs("kancelář");
        List<UnitUseDTO> unitUses = Arrays.asList(testDTO, dto2);
        when(service.findAll()).thenReturn(unitUses);

        // When
        ResponseEntity<List<UnitUseDTO>> response = controller.findAll(false);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getCode()).isEqualTo("1");
        assertThat(response.getBody().get(1).getCode()).isEqualTo("2");
        verify(service, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return only currently valid unit uses when validOnly is true")
    void shouldReturnOnlyValidUnitUsesWhenValidOnlyIsTrue() {
        // Given
        List<UnitUseDTO> validUnitUses = Arrays.asList(testDTO);
        when(service.findAllCurrentlyValid()).thenReturn(validUnitUses);

        // When
        ResponseEntity<List<UnitUseDTO>> response = controller.findAll(true);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        verify(service, times(1)).findAllCurrentlyValid();
    }

    @Test
    @DisplayName("Should return unit use by ID")
    void shouldReturnUnitUseById() {
        // Given
        when(service.findById(1L)).thenReturn(testDTO);

        // When
        ResponseEntity<UnitUseDTO> response = controller.findById(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo("1");
        verify(service, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return unit use by code")
    void shouldReturnUnitUseByCode() {
        // Given
        when(service.findByCode("1")).thenReturn(testDTO);

        // When
        ResponseEntity<UnitUseDTO> response = controller.findByCode("1");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo("1");
        verify(service, times(1)).findByCode("1");
    }

    @Test
    @DisplayName("Should create new unit use")
    void shouldCreateNewUnitUse() {
        // Given
        when(service.create(any(UnitUseDTO.class))).thenReturn(testDTO);

        // When
        ResponseEntity<UnitUseDTO> response = controller.create(testDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getCode()).isEqualTo("1");
        verify(service, times(1)).create(any(UnitUseDTO.class));
    }

    @Test
    @DisplayName("Should update existing unit use")
    void shouldUpdateExistingUnitUse() {
        // Given
        testDTO.setNameCs("aktualizovaný byt");
        when(service.update(eq(1L), any(UnitUseDTO.class))).thenReturn(testDTO);

        // When
        ResponseEntity<UnitUseDTO> response = controller.update(1L, testDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getNameCs()).isEqualTo("aktualizovaný byt");
        verify(service, times(1)).update(eq(1L), any(UnitUseDTO.class));
    }

    @Test
    @DisplayName("Should delete unit use")
    void shouldDeleteUnitUse() {
        // Given
        doNothing().when(service).delete(1L);

        // When
        ResponseEntity<Void> response = controller.delete(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(service, times(1)).delete(1L);
    }
}
