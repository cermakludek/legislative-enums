package cz.intelis.legislativeenums.cuzk.propertyprotection;

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
@DisplayName("PropertyProtectionService Unit Tests")
class PropertyProtectionServiceTest {

    @Mock
    private PropertyProtectionRepository repository;

    @Mock
    private CodelistEventPublisher eventPublisher;

    @InjectMocks
    private PropertyProtectionService propertyProtectionService;

    private PropertyProtection testPropertyProtection;
    private PropertyProtectionDTO testPropertyProtectionDTO;

    @BeforeEach
    void setUp() {
        testPropertyProtection = new PropertyProtection();
        testPropertyProtection.setId(1L);
        testPropertyProtection.setCode("1");
        testPropertyProtection.setNameCs("památková rezervace");
        testPropertyProtection.setNameEn("heritage reservation");
        testPropertyProtection.setProtectionTypeCode("PAM");
        testPropertyProtection.setAppliesToLand(true);
        testPropertyProtection.setAppliesToBuilding(true);
        testPropertyProtection.setAppliesToUnit(false);
        testPropertyProtection.setAppliesToBuildingRight(false);
        testPropertyProtection.setValidFrom(LocalDate.of(2000, 1, 1));
        testPropertyProtection.setSortOrder(1);

        testPropertyProtectionDTO = new PropertyProtectionDTO();
        testPropertyProtectionDTO.setId(1L);
        testPropertyProtectionDTO.setCode("1");
        testPropertyProtectionDTO.setNameCs("památková rezervace");
        testPropertyProtectionDTO.setNameEn("heritage reservation");
        testPropertyProtectionDTO.setProtectionTypeCode("PAM");
        testPropertyProtectionDTO.setAppliesToLand(true);
        testPropertyProtectionDTO.setAppliesToBuilding(true);
        testPropertyProtectionDTO.setAppliesToUnit(false);
        testPropertyProtectionDTO.setAppliesToBuildingRight(false);
        testPropertyProtectionDTO.setValidFrom(LocalDate.of(2000, 1, 1));
        testPropertyProtectionDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("Should find all property protections")
    void shouldFindAllPropertyProtections() {
        // Given
        PropertyProtection pp2 = new PropertyProtection();
        pp2.setId(2L);
        pp2.setCode("2");
        pp2.setNameCs("památková zóna");
        when(repository.findAllOrdered()).thenReturn(Arrays.asList(testPropertyProtection, pp2));

        // When
        List<PropertyProtectionDTO> result = propertyProtectionService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCode()).isEqualTo("1");
        assertThat(result.get(1).getCode()).isEqualTo("2");
        verify(repository, times(1)).findAllOrdered();
    }

    @Test
    @DisplayName("Should find property protection by ID")
    void shouldFindPropertyProtectionById() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testPropertyProtection));

        // When
        PropertyProtectionDTO result = propertyProtectionService.findById(1L);

        // Then
        assertThat(result.getCode()).isEqualTo("1");
        assertThat(result.getNameCs()).isEqualTo("památková rezervace");
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when property protection not found by ID")
    void shouldThrowExceptionWhenPropertyProtectionNotFoundById() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> propertyProtectionService.findById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Property protection not found with id: 999");
    }

    @Test
    @DisplayName("Should find property protection by code")
    void shouldFindPropertyProtectionByCode() {
        // Given
        when(repository.findByCode("1")).thenReturn(Optional.of(testPropertyProtection));

        // When
        PropertyProtectionDTO result = propertyProtectionService.findByCode("1");

        // Then
        assertThat(result.getCode()).isEqualTo("1");
        verify(repository, times(1)).findByCode("1");
    }

    @Test
    @DisplayName("Should throw exception when property protection not found by code")
    void shouldThrowExceptionWhenPropertyProtectionNotFoundByCode() {
        // Given
        when(repository.findByCode("INVALID")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> propertyProtectionService.findByCode("INVALID"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Property protection not found with code: INVALID");
    }

    @Test
    @DisplayName("Should find property protections by protection type code")
    void shouldFindPropertyProtectionsByProtectionTypeCode() {
        // Given
        when(repository.findByProtectionTypeCode("PAM")).thenReturn(Arrays.asList(testPropertyProtection));

        // When
        List<PropertyProtectionDTO> result = propertyProtectionService.findByProtectionTypeCode("PAM");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProtectionTypeCode()).isEqualTo("PAM");
        verify(repository, times(1)).findByProtectionTypeCode("PAM");
    }

    @Test
    @DisplayName("Should create new property protection")
    void shouldCreateNewPropertyProtection() {
        // Given
        when(repository.existsByCode(anyString())).thenReturn(false);
        when(repository.save(any(PropertyProtection.class))).thenAnswer(inv -> {
            PropertyProtection pp = inv.getArgument(0);
            pp.setId(1L);
            return pp;
        });

        // When
        PropertyProtectionDTO result = propertyProtectionService.create(testPropertyProtectionDTO);

        // Then
        assertThat(result.getCode()).isEqualTo("1");
        verify(repository, times(1)).save(any(PropertyProtection.class));
        verify(eventPublisher, times(1)).publishInsert(
                eq("Způsoby ochrany nemovitosti"), eq("PROPERTY_PROTECTION"), eq(1L), eq("1"), eq("památková rezervace"));
    }

    @Test
    @DisplayName("Should throw exception when creating property protection with existing code")
    void shouldThrowExceptionWhenCreatingPropertyProtectionWithExistingCode() {
        // Given
        when(repository.existsByCode("1")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> propertyProtectionService.create(testPropertyProtectionDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Property protection with code 1 already exists");
    }

    @Test
    @DisplayName("Should update existing property protection")
    void shouldUpdateExistingPropertyProtection() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testPropertyProtection));
        when(repository.save(any(PropertyProtection.class))).thenReturn(testPropertyProtection);

        testPropertyProtectionDTO.setNameCs("aktualizovaná ochrana");

        // When
        PropertyProtectionDTO result = propertyProtectionService.update(1L, testPropertyProtectionDTO);

        // Then
        verify(repository, times(1)).save(any(PropertyProtection.class));
        verify(eventPublisher, times(1)).publishUpdate(
                eq("Způsoby ochrany nemovitosti"), eq("PROPERTY_PROTECTION"), eq(1L), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent property protection")
    void shouldThrowExceptionWhenUpdatingNonExistentPropertyProtection() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> propertyProtectionService.update(999L, testPropertyProtectionDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Property protection not found with id: 999");
    }

    @Test
    @DisplayName("Should delete property protection")
    void shouldDeletePropertyProtection() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testPropertyProtection));

        // When
        propertyProtectionService.delete(1L);

        // Then
        verify(repository, times(1)).deleteById(1L);
        verify(eventPublisher, times(1)).publishDelete(
                eq("Způsoby ochrany nemovitosti"), eq("PROPERTY_PROTECTION"), eq(1L), eq("1"), eq("památková rezervace"));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent property protection")
    void shouldThrowExceptionWhenDeletingNonExistentPropertyProtection() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> propertyProtectionService.delete(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Property protection not found with id: 999");
    }

    @Test
    @DisplayName("Should preserve ČÚZK specific fields when creating property protection")
    void shouldPreserveCuzkSpecificFieldsWhenCreatingPropertyProtection() {
        // Given
        when(repository.existsByCode(anyString())).thenReturn(false);
        when(repository.save(any(PropertyProtection.class))).thenAnswer(inv -> {
            PropertyProtection pp = inv.getArgument(0);
            pp.setId(1L);
            return pp;
        });

        // When
        propertyProtectionService.create(testPropertyProtectionDTO);

        // Then
        ArgumentCaptor<PropertyProtection> captor = ArgumentCaptor.forClass(PropertyProtection.class);
        verify(repository).save(captor.capture());
        PropertyProtection saved = captor.getValue();
        assertThat(saved.getProtectionTypeCode()).isEqualTo("PAM");
        assertThat(saved.getAppliesToLand()).isTrue();
        assertThat(saved.getAppliesToBuilding()).isTrue();
        assertThat(saved.getAppliesToUnit()).isFalse();
        assertThat(saved.getAppliesToBuildingRight()).isFalse();
    }
}
