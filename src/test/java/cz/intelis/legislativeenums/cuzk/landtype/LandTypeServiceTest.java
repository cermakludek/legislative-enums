package cz.intelis.legislativeenums.cuzk.landtype;

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
@DisplayName("LandTypeService Unit Tests")
class LandTypeServiceTest {

    @Mock
    private LandTypeRepository repository;

    @Mock
    private CodelistEventPublisher eventPublisher;

    @InjectMocks
    private LandTypeService landTypeService;

    private LandType testLandType;
    private LandTypeDTO testLandTypeDTO;

    @BeforeEach
    void setUp() {
        testLandType = new LandType();
        testLandType.setId(1L);
        testLandType.setCode("2");
        testLandType.setNameCs("orná půda");
        testLandType.setNameEn("arable land");
        testLandType.setAbbreviation("OP");
        testLandType.setAgriculturalLand(true);
        testLandType.setLandParcelTypeCode("PKN");
        testLandType.setBuildingParcel(false);
        testLandType.setMandatoryLandProtection(true);
        testLandType.setMandatoryLandUse(true);
        testLandType.setValidFrom(LocalDate.of(2000, 1, 1));
        testLandType.setSortOrder(1);

        testLandTypeDTO = new LandTypeDTO();
        testLandTypeDTO.setId(1L);
        testLandTypeDTO.setCode("2");
        testLandTypeDTO.setNameCs("orná půda");
        testLandTypeDTO.setNameEn("arable land");
        testLandTypeDTO.setAbbreviation("OP");
        testLandTypeDTO.setAgriculturalLand(true);
        testLandTypeDTO.setLandParcelTypeCode("PKN");
        testLandTypeDTO.setBuildingParcel(false);
        testLandTypeDTO.setMandatoryLandProtection(true);
        testLandTypeDTO.setMandatoryLandUse(true);
        testLandTypeDTO.setValidFrom(LocalDate.of(2000, 1, 1));
        testLandTypeDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("Should find all land types")
    void shouldFindAllLandTypes() {
        // Given
        LandType lt2 = new LandType();
        lt2.setId(2L);
        lt2.setCode("3");
        lt2.setNameCs("chmelnice");

        when(repository.findAllOrdered()).thenReturn(Arrays.asList(testLandType, lt2));

        // When
        List<LandTypeDTO> result = landTypeService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCode()).isEqualTo("2");
        assertThat(result.get(1).getCode()).isEqualTo("3");
        verify(repository, times(1)).findAllOrdered();
    }

    @Test
    @DisplayName("Should find land type by ID")
    void shouldFindLandTypeById() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testLandType));

        // When
        LandTypeDTO result = landTypeService.findById(1L);

        // Then
        assertThat(result.getCode()).isEqualTo("2");
        assertThat(result.getNameCs()).isEqualTo("orná půda");
        assertThat(result.getAgriculturalLand()).isTrue();
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when land type not found by ID")
    void shouldThrowExceptionWhenLandTypeNotFoundById() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> landTypeService.findById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Land type not found with id: 999");
    }

    @Test
    @DisplayName("Should find land type by code")
    void shouldFindLandTypeByCode() {
        // Given
        when(repository.findByCode("2")).thenReturn(Optional.of(testLandType));

        // When
        LandTypeDTO result = landTypeService.findByCode("2");

        // Then
        assertThat(result.getCode()).isEqualTo("2");
        verify(repository, times(1)).findByCode("2");
    }

    @Test
    @DisplayName("Should throw exception when land type not found by code")
    void shouldThrowExceptionWhenLandTypeNotFoundByCode() {
        // Given
        when(repository.findByCode("INVALID")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> landTypeService.findByCode("INVALID"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Land type not found with code: INVALID");
    }

    @Test
    @DisplayName("Should create new land type")
    void shouldCreateNewLandType() {
        // Given
        when(repository.existsByCode(anyString())).thenReturn(false);
        when(repository.save(any(LandType.class))).thenAnswer(inv -> {
            LandType lt = inv.getArgument(0);
            lt.setId(1L);
            return lt;
        });

        // When
        LandTypeDTO result = landTypeService.create(testLandTypeDTO);

        // Then
        assertThat(result.getCode()).isEqualTo("2");
        verify(repository, times(1)).save(any(LandType.class));
        verify(eventPublisher, times(1)).publishInsert(
                eq("Druhy pozemků"), eq("LAND_TYPE"), eq(1L), eq("2"), eq("orná půda"));
    }

    @Test
    @DisplayName("Should throw exception when creating land type with existing code")
    void shouldThrowExceptionWhenCreatingLandTypeWithExistingCode() {
        // Given
        when(repository.existsByCode("2")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> landTypeService.create(testLandTypeDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Land type with code 2 already exists");
    }

    @Test
    @DisplayName("Should update existing land type")
    void shouldUpdateExistingLandType() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testLandType));
        when(repository.save(any(LandType.class))).thenReturn(testLandType);

        testLandTypeDTO.setNameCs("aktualizovaná orná půda");

        // When
        LandTypeDTO result = landTypeService.update(1L, testLandTypeDTO);

        // Then
        verify(repository, times(1)).save(any(LandType.class));
        verify(eventPublisher, times(1)).publishUpdate(
                eq("Druhy pozemků"), eq("LAND_TYPE"), eq(1L), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent land type")
    void shouldThrowExceptionWhenUpdatingNonExistentLandType() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> landTypeService.update(999L, testLandTypeDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Land type not found with id: 999");
    }

    @Test
    @DisplayName("Should throw exception when updating land type with existing code")
    void shouldThrowExceptionWhenUpdatingLandTypeWithExistingCode() {
        // Given
        testLandTypeDTO.setCode("NEWCODE");
        when(repository.findById(1L)).thenReturn(Optional.of(testLandType));
        when(repository.existsByCode("NEWCODE")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> landTypeService.update(1L, testLandTypeDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Land type with code NEWCODE already exists");
    }

    @Test
    @DisplayName("Should delete land type")
    void shouldDeleteLandType() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testLandType));

        // When
        landTypeService.delete(1L);

        // Then
        verify(repository, times(1)).deleteById(1L);
        verify(eventPublisher, times(1)).publishDelete(
                eq("Druhy pozemků"), eq("LAND_TYPE"), eq(1L), eq("2"), eq("orná půda"));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent land type")
    void shouldThrowExceptionWhenDeletingNonExistentLandType() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> landTypeService.delete(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Land type not found with id: 999");
    }

    @Test
    @DisplayName("Should preserve ČÚZK specific fields when creating land type")
    void shouldPreserveCuzkSpecificFieldsWhenCreatingLandType() {
        // Given
        when(repository.existsByCode(anyString())).thenReturn(false);
        when(repository.save(any(LandType.class))).thenAnswer(inv -> {
            LandType lt = inv.getArgument(0);
            lt.setId(1L);
            return lt;
        });

        // When
        landTypeService.create(testLandTypeDTO);

        // Then
        ArgumentCaptor<LandType> captor = ArgumentCaptor.forClass(LandType.class);
        verify(repository).save(captor.capture());
        LandType saved = captor.getValue();
        assertThat(saved.getAbbreviation()).isEqualTo("OP");
        assertThat(saved.getAgriculturalLand()).isTrue();
        assertThat(saved.getLandParcelTypeCode()).isEqualTo("PKN");
        assertThat(saved.getBuildingParcel()).isFalse();
        assertThat(saved.getMandatoryLandProtection()).isTrue();
        assertThat(saved.getMandatoryLandUse()).isTrue();
        assertThat(saved.getValidFrom()).isEqualTo(LocalDate.of(2000, 1, 1));
    }
}
