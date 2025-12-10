package cz.intelis.legislativeenums.cuzk.unittype;

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
@DisplayName("UnitTypeApiController Unit Tests")
class UnitTypeApiControllerTest {

    @Mock
    private UnitTypeService service;

    @InjectMocks
    private UnitTypeApiController controller;

    private UnitTypeDTO testDTO;

    @BeforeEach
    void setUp() {
        testDTO = new UnitTypeDTO();
        testDTO.setId(1L);
        testDTO.setCode("1");
        testDTO.setNameCs("bytová jednotka");
        testDTO.setNameEn("residential unit");
        testDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("Should return all unit types")
    void shouldReturnAllUnitTypes() {
        // Given
        UnitTypeDTO dto2 = new UnitTypeDTO();
        dto2.setId(2L);
        dto2.setCode("2");
        dto2.setNameCs("nebytová jednotka");
        List<UnitTypeDTO> unitTypes = Arrays.asList(testDTO, dto2);
        when(service.findAll()).thenReturn(unitTypes);

        // When
        ResponseEntity<List<UnitTypeDTO>> response = controller.findAll(false);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getCode()).isEqualTo("1");
        assertThat(response.getBody().get(1).getCode()).isEqualTo("2");
        verify(service, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return only currently valid unit types when validOnly is true")
    void shouldReturnOnlyValidUnitTypesWhenValidOnlyIsTrue() {
        // Given
        List<UnitTypeDTO> validUnitTypes = Arrays.asList(testDTO);
        when(service.findAllCurrentlyValid()).thenReturn(validUnitTypes);

        // When
        ResponseEntity<List<UnitTypeDTO>> response = controller.findAll(true);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        verify(service, times(1)).findAllCurrentlyValid();
    }

    @Test
    @DisplayName("Should return unit type by ID")
    void shouldReturnUnitTypeById() {
        // Given
        when(service.findById(1L)).thenReturn(testDTO);

        // When
        ResponseEntity<UnitTypeDTO> response = controller.findById(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo("1");
        verify(service, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return unit type by code")
    void shouldReturnUnitTypeByCode() {
        // Given
        when(service.findByCode("1")).thenReturn(testDTO);

        // When
        ResponseEntity<UnitTypeDTO> response = controller.findByCode("1");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo("1");
        verify(service, times(1)).findByCode("1");
    }

    @Test
    @DisplayName("Should create new unit type")
    void shouldCreateNewUnitType() {
        // Given
        when(service.create(any(UnitTypeDTO.class))).thenReturn(testDTO);

        // When
        ResponseEntity<UnitTypeDTO> response = controller.create(testDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getCode()).isEqualTo("1");
        verify(service, times(1)).create(any(UnitTypeDTO.class));
    }

    @Test
    @DisplayName("Should update existing unit type")
    void shouldUpdateExistingUnitType() {
        // Given
        testDTO.setNameCs("aktualizovaná bytová jednotka");
        when(service.update(eq(1L), any(UnitTypeDTO.class))).thenReturn(testDTO);

        // When
        ResponseEntity<UnitTypeDTO> response = controller.update(1L, testDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getNameCs()).isEqualTo("aktualizovaná bytová jednotka");
        verify(service, times(1)).update(eq(1L), any(UnitTypeDTO.class));
    }

    @Test
    @DisplayName("Should delete unit type")
    void shouldDeleteUnitType() {
        // Given
        doNothing().when(service).delete(1L);

        // When
        ResponseEntity<Void> response = controller.delete(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(service, times(1)).delete(1L);
    }
}
