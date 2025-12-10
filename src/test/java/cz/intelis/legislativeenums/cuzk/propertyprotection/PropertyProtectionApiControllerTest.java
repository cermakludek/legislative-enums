package cz.intelis.legislativeenums.cuzk.propertyprotection;

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
@DisplayName("PropertyProtectionApiController Unit Tests")
class PropertyProtectionApiControllerTest {

    @Mock
    private PropertyProtectionService service;

    @InjectMocks
    private PropertyProtectionApiController controller;

    private PropertyProtectionDTO testDTO;

    @BeforeEach
    void setUp() {
        testDTO = new PropertyProtectionDTO();
        testDTO.setId(1L);
        testDTO.setCode("1");
        testDTO.setNameCs("kulturní památka");
        testDTO.setNameEn("cultural monument");
        testDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("Should return all property protections")
    void shouldReturnAllPropertyProtections() {
        // Given
        PropertyProtectionDTO dto2 = new PropertyProtectionDTO();
        dto2.setId(2L);
        dto2.setCode("2");
        dto2.setNameCs("národní kulturní památka");
        List<PropertyProtectionDTO> propertyProtections = Arrays.asList(testDTO, dto2);
        when(service.findAll()).thenReturn(propertyProtections);

        // When
        ResponseEntity<List<PropertyProtectionDTO>> response = controller.findAll(false);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getCode()).isEqualTo("1");
        assertThat(response.getBody().get(1).getCode()).isEqualTo("2");
        verify(service, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return only currently valid property protections when validOnly is true")
    void shouldReturnOnlyValidPropertyProtectionsWhenValidOnlyIsTrue() {
        // Given
        List<PropertyProtectionDTO> validPropertyProtections = Arrays.asList(testDTO);
        when(service.findAllCurrentlyValid()).thenReturn(validPropertyProtections);

        // When
        ResponseEntity<List<PropertyProtectionDTO>> response = controller.findAll(true);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        verify(service, times(1)).findAllCurrentlyValid();
    }

    @Test
    @DisplayName("Should return property protection by ID")
    void shouldReturnPropertyProtectionById() {
        // Given
        when(service.findById(1L)).thenReturn(testDTO);

        // When
        ResponseEntity<PropertyProtectionDTO> response = controller.findById(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo("1");
        verify(service, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return property protection by code")
    void shouldReturnPropertyProtectionByCode() {
        // Given
        when(service.findByCode("1")).thenReturn(testDTO);

        // When
        ResponseEntity<PropertyProtectionDTO> response = controller.findByCode("1");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo("1");
        verify(service, times(1)).findByCode("1");
    }

    @Test
    @DisplayName("Should return property protections by protection type code")
    void shouldReturnPropertyProtectionsByProtectionTypeCode() {
        // Given
        List<PropertyProtectionDTO> propertyProtections = Arrays.asList(testDTO);
        when(service.findByProtectionTypeCode("TYPE1")).thenReturn(propertyProtections);

        // When
        ResponseEntity<List<PropertyProtectionDTO>> response = controller.findByProtectionTypeCode("TYPE1");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        verify(service, times(1)).findByProtectionTypeCode("TYPE1");
    }

    @Test
    @DisplayName("Should create new property protection")
    void shouldCreateNewPropertyProtection() {
        // Given
        when(service.create(any(PropertyProtectionDTO.class))).thenReturn(testDTO);

        // When
        ResponseEntity<PropertyProtectionDTO> response = controller.create(testDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getCode()).isEqualTo("1");
        verify(service, times(1)).create(any(PropertyProtectionDTO.class));
    }

    @Test
    @DisplayName("Should update existing property protection")
    void shouldUpdateExistingPropertyProtection() {
        // Given
        testDTO.setNameCs("aktualizovaná kulturní památka");
        when(service.update(eq(1L), any(PropertyProtectionDTO.class))).thenReturn(testDTO);

        // When
        ResponseEntity<PropertyProtectionDTO> response = controller.update(1L, testDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getNameCs()).isEqualTo("aktualizovaná kulturní památka");
        verify(service, times(1)).update(eq(1L), any(PropertyProtectionDTO.class));
    }

    @Test
    @DisplayName("Should delete property protection")
    void shouldDeletePropertyProtection() {
        // Given
        doNothing().when(service).delete(1L);

        // When
        ResponseEntity<Void> response = controller.delete(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(service, times(1)).delete(1L);
    }
}
