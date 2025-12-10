package cz.intelis.legislativeenums.cuzk.propertyprotectiontype;

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
@DisplayName("PropertyProtectionTypeApiController Unit Tests")
class PropertyProtectionTypeApiControllerTest {

    @Mock
    private PropertyProtectionTypeService service;

    @InjectMocks
    private PropertyProtectionTypeApiController controller;

    private PropertyProtectionTypeDTO testDTO;

    @BeforeEach
    void setUp() {
        testDTO = new PropertyProtectionTypeDTO();
        testDTO.setId(1L);
        testDTO.setCode("1");
        testDTO.setNameCs("ochrana památek");
        testDTO.setNameEn("monument protection");
        testDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("Should return all property protection types")
    void shouldReturnAllPropertyProtectionTypes() {
        // Given
        PropertyProtectionTypeDTO dto2 = new PropertyProtectionTypeDTO();
        dto2.setId(2L);
        dto2.setCode("2");
        dto2.setNameCs("ochrana přírody");
        List<PropertyProtectionTypeDTO> propertyProtectionTypes = Arrays.asList(testDTO, dto2);
        when(service.findAll()).thenReturn(propertyProtectionTypes);

        // When
        ResponseEntity<List<PropertyProtectionTypeDTO>> response = controller.findAll(false);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getCode()).isEqualTo("1");
        assertThat(response.getBody().get(1).getCode()).isEqualTo("2");
        verify(service, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return only currently valid property protection types when validOnly is true")
    void shouldReturnOnlyValidPropertyProtectionTypesWhenValidOnlyIsTrue() {
        // Given
        List<PropertyProtectionTypeDTO> validPropertyProtectionTypes = Arrays.asList(testDTO);
        when(service.findAllCurrentlyValid()).thenReturn(validPropertyProtectionTypes);

        // When
        ResponseEntity<List<PropertyProtectionTypeDTO>> response = controller.findAll(true);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        verify(service, times(1)).findAllCurrentlyValid();
    }

    @Test
    @DisplayName("Should return property protection type by ID")
    void shouldReturnPropertyProtectionTypeById() {
        // Given
        when(service.findById(1L)).thenReturn(testDTO);

        // When
        ResponseEntity<PropertyProtectionTypeDTO> response = controller.findById(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo("1");
        verify(service, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return property protection type by code")
    void shouldReturnPropertyProtectionTypeByCode() {
        // Given
        when(service.findByCode("1")).thenReturn(testDTO);

        // When
        ResponseEntity<PropertyProtectionTypeDTO> response = controller.findByCode("1");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo("1");
        verify(service, times(1)).findByCode("1");
    }

    @Test
    @DisplayName("Should create new property protection type")
    void shouldCreateNewPropertyProtectionType() {
        // Given
        when(service.create(any(PropertyProtectionTypeDTO.class))).thenReturn(testDTO);

        // When
        ResponseEntity<PropertyProtectionTypeDTO> response = controller.create(testDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getCode()).isEqualTo("1");
        verify(service, times(1)).create(any(PropertyProtectionTypeDTO.class));
    }

    @Test
    @DisplayName("Should update existing property protection type")
    void shouldUpdateExistingPropertyProtectionType() {
        // Given
        testDTO.setNameCs("aktualizovaná ochrana památek");
        when(service.update(eq(1L), any(PropertyProtectionTypeDTO.class))).thenReturn(testDTO);

        // When
        ResponseEntity<PropertyProtectionTypeDTO> response = controller.update(1L, testDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getNameCs()).isEqualTo("aktualizovaná ochrana památek");
        verify(service, times(1)).update(eq(1L), any(PropertyProtectionTypeDTO.class));
    }

    @Test
    @DisplayName("Should delete property protection type")
    void shouldDeletePropertyProtectionType() {
        // Given
        doNothing().when(service).delete(1L);

        // When
        ResponseEntity<Void> response = controller.delete(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(service, times(1)).delete(1L);
    }
}
