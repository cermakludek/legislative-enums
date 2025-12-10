package cz.intelis.legislativeenums.cuzk.propertyprotectiontype;

import cz.intelis.legislativeenums.notification.CodelistEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PropertyProtectionTypeService Unit Tests")
class PropertyProtectionTypeServiceTest {

    @Mock
    private PropertyProtectionTypeRepository repository;

    @Mock
    private CodelistEventPublisher eventPublisher;

    @InjectMocks
    private PropertyProtectionTypeService propertyProtectionTypeService;

    private PropertyProtectionType testPropertyProtectionType;
    private PropertyProtectionTypeDTO testPropertyProtectionTypeDTO;

    @BeforeEach
    void setUp() {
        testPropertyProtectionType = new PropertyProtectionType();
        testPropertyProtectionType.setId(1L);
        testPropertyProtectionType.setCode("PAM");
        testPropertyProtectionType.setNameCs("památková ochrana");
        testPropertyProtectionType.setNameEn("heritage protection");
        testPropertyProtectionType.setValidFrom(LocalDate.of(2000, 1, 1));
        testPropertyProtectionType.setSortOrder(1);

        testPropertyProtectionTypeDTO = new PropertyProtectionTypeDTO();
        testPropertyProtectionTypeDTO.setId(1L);
        testPropertyProtectionTypeDTO.setCode("PAM");
        testPropertyProtectionTypeDTO.setNameCs("památková ochrana");
        testPropertyProtectionTypeDTO.setNameEn("heritage protection");
        testPropertyProtectionTypeDTO.setValidFrom(LocalDate.of(2000, 1, 1));
        testPropertyProtectionTypeDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("Should find all property protection types")
    void shouldFindAllPropertyProtectionTypes() {
        // Given
        PropertyProtectionType ppt2 = new PropertyProtectionType();
        ppt2.setId(2L);
        ppt2.setCode("PRI");
        ppt2.setNameCs("přírodní ochrana");
        when(repository.findAllOrdered()).thenReturn(Arrays.asList(testPropertyProtectionType, ppt2));

        // When
        List<PropertyProtectionTypeDTO> result = propertyProtectionTypeService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCode()).isEqualTo("PAM");
        assertThat(result.get(1).getCode()).isEqualTo("PRI");
        verify(repository, times(1)).findAllOrdered();
    }

    @Test
    @DisplayName("Should find property protection type by ID")
    void shouldFindPropertyProtectionTypeById() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testPropertyProtectionType));

        // When
        PropertyProtectionTypeDTO result = propertyProtectionTypeService.findById(1L);

        // Then
        assertThat(result.getCode()).isEqualTo("PAM");
        assertThat(result.getNameCs()).isEqualTo("památková ochrana");
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when property protection type not found by ID")
    void shouldThrowExceptionWhenPropertyProtectionTypeNotFoundById() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> propertyProtectionTypeService.findById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Property protection type not found with id: 999");
    }

    @Test
    @DisplayName("Should find property protection type by code")
    void shouldFindPropertyProtectionTypeByCode() {
        // Given
        when(repository.findByCode("PAM")).thenReturn(Optional.of(testPropertyProtectionType));

        // When
        PropertyProtectionTypeDTO result = propertyProtectionTypeService.findByCode("PAM");

        // Then
        assertThat(result.getCode()).isEqualTo("PAM");
        verify(repository, times(1)).findByCode("PAM");
    }

    @Test
    @DisplayName("Should throw exception when property protection type not found by code")
    void shouldThrowExceptionWhenPropertyProtectionTypeNotFoundByCode() {
        // Given
        when(repository.findByCode("INVALID")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> propertyProtectionTypeService.findByCode("INVALID"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Property protection type not found with code: INVALID");
    }

    @Test
    @DisplayName("Should create new property protection type")
    void shouldCreateNewPropertyProtectionType() {
        // Given
        when(repository.existsByCode(anyString())).thenReturn(false);
        when(repository.save(any(PropertyProtectionType.class))).thenAnswer(inv -> {
            PropertyProtectionType ppt = inv.getArgument(0);
            ppt.setId(1L);
            return ppt;
        });

        // When
        PropertyProtectionTypeDTO result = propertyProtectionTypeService.create(testPropertyProtectionTypeDTO);

        // Then
        assertThat(result.getCode()).isEqualTo("PAM");
        verify(repository, times(1)).save(any(PropertyProtectionType.class));
        verify(eventPublisher, times(1)).publishInsert(
                eq("Typy ochrany nemovitosti"), eq("PROPERTY_PROTECTION_TYPE"), eq(1L), eq("PAM"), eq("památková ochrana"));
    }

    @Test
    @DisplayName("Should throw exception when creating property protection type with existing code")
    void shouldThrowExceptionWhenCreatingPropertyProtectionTypeWithExistingCode() {
        // Given
        when(repository.existsByCode("PAM")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> propertyProtectionTypeService.create(testPropertyProtectionTypeDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Property protection type with code PAM already exists");
    }

    @Test
    @DisplayName("Should update existing property protection type")
    void shouldUpdateExistingPropertyProtectionType() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testPropertyProtectionType));
        when(repository.save(any(PropertyProtectionType.class))).thenReturn(testPropertyProtectionType);

        testPropertyProtectionTypeDTO.setNameCs("aktualizovaná ochrana");

        // When
        PropertyProtectionTypeDTO result = propertyProtectionTypeService.update(1L, testPropertyProtectionTypeDTO);

        // Then
        verify(repository, times(1)).save(any(PropertyProtectionType.class));
        verify(eventPublisher, times(1)).publishUpdate(
                eq("Typy ochrany nemovitosti"), eq("PROPERTY_PROTECTION_TYPE"), eq(1L), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent property protection type")
    void shouldThrowExceptionWhenUpdatingNonExistentPropertyProtectionType() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> propertyProtectionTypeService.update(999L, testPropertyProtectionTypeDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Property protection type not found with id: 999");
    }

    @Test
    @DisplayName("Should delete property protection type")
    void shouldDeletePropertyProtectionType() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testPropertyProtectionType));

        // When
        propertyProtectionTypeService.delete(1L);

        // Then
        verify(repository, times(1)).deleteById(1L);
        verify(eventPublisher, times(1)).publishDelete(
                eq("Typy ochrany nemovitosti"), eq("PROPERTY_PROTECTION_TYPE"), eq(1L), eq("PAM"), eq("památková ochrana"));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent property protection type")
    void shouldThrowExceptionWhenDeletingNonExistentPropertyProtectionType() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> propertyProtectionTypeService.delete(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Property protection type not found with id: 999");
    }

}
