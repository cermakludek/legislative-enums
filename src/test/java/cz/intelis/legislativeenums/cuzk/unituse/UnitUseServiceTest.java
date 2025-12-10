package cz.intelis.legislativeenums.cuzk.unituse;

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
@DisplayName("UnitUseService Unit Tests")
class UnitUseServiceTest {

    @Mock
    private UnitUseRepository repository;

    @Mock
    private CodelistEventPublisher eventPublisher;

    @InjectMocks
    private UnitUseService unitUseService;

    private UnitUse testUnitUse;
    private UnitUseDTO testUnitUseDTO;

    @BeforeEach
    void setUp() {
        testUnitUse = new UnitUse();
        testUnitUse.setId(1L);
        testUnitUse.setCode("1");
        testUnitUse.setNameCs("bydlení");
        testUnitUse.setNameEn("housing");
        testUnitUse.setAbbreviation("BYD");
        testUnitUse.setValidFrom(LocalDate.of(2014, 1, 1));
        testUnitUse.setSortOrder(1);

        testUnitUseDTO = new UnitUseDTO();
        testUnitUseDTO.setId(1L);
        testUnitUseDTO.setCode("1");
        testUnitUseDTO.setNameCs("bydlení");
        testUnitUseDTO.setNameEn("housing");
        testUnitUseDTO.setAbbreviation("BYD");
        testUnitUseDTO.setValidFrom(LocalDate.of(2014, 1, 1));
        testUnitUseDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("Should find all unit uses")
    void shouldFindAllUnitUses() {
        // Given
        UnitUse uu2 = new UnitUse();
        uu2.setId(2L);
        uu2.setCode("2");
        uu2.setNameCs("rekreace");
        when(repository.findAllOrdered()).thenReturn(Arrays.asList(testUnitUse, uu2));

        // When
        List<UnitUseDTO> result = unitUseService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCode()).isEqualTo("1");
        assertThat(result.get(1).getCode()).isEqualTo("2");
        verify(repository, times(1)).findAllOrdered();
    }

    @Test
    @DisplayName("Should find unit use by ID")
    void shouldFindUnitUseById() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testUnitUse));

        // When
        UnitUseDTO result = unitUseService.findById(1L);

        // Then
        assertThat(result.getCode()).isEqualTo("1");
        assertThat(result.getNameCs()).isEqualTo("bydlení");
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when unit use not found by ID")
    void shouldThrowExceptionWhenUnitUseNotFoundById() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> unitUseService.findById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unit use not found with id: 999");
    }

    @Test
    @DisplayName("Should find unit use by code")
    void shouldFindUnitUseByCode() {
        // Given
        when(repository.findByCode("1")).thenReturn(Optional.of(testUnitUse));

        // When
        UnitUseDTO result = unitUseService.findByCode("1");

        // Then
        assertThat(result.getCode()).isEqualTo("1");
        verify(repository, times(1)).findByCode("1");
    }

    @Test
    @DisplayName("Should throw exception when unit use not found by code")
    void shouldThrowExceptionWhenUnitUseNotFoundByCode() {
        // Given
        when(repository.findByCode("INVALID")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> unitUseService.findByCode("INVALID"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unit use not found with code: INVALID");
    }

    @Test
    @DisplayName("Should create new unit use")
    void shouldCreateNewUnitUse() {
        // Given
        when(repository.existsByCode(anyString())).thenReturn(false);
        when(repository.save(any(UnitUse.class))).thenAnswer(inv -> {
            UnitUse uu = inv.getArgument(0);
            uu.setId(1L);
            return uu;
        });

        // When
        UnitUseDTO result = unitUseService.create(testUnitUseDTO);

        // Then
        assertThat(result.getCode()).isEqualTo("1");
        verify(repository, times(1)).save(any(UnitUse.class));
        verify(eventPublisher, times(1)).publishInsert(
                eq("Způsoby využití jednotky"), eq("UNIT_USE"), eq(1L), eq("1"), eq("bydlení"));
    }

    @Test
    @DisplayName("Should throw exception when creating unit use with existing code")
    void shouldThrowExceptionWhenCreatingUnitUseWithExistingCode() {
        // Given
        when(repository.existsByCode("1")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> unitUseService.create(testUnitUseDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unit use with code 1 already exists");
    }

    @Test
    @DisplayName("Should update existing unit use")
    void shouldUpdateExistingUnitUse() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testUnitUse));
        when(repository.save(any(UnitUse.class))).thenReturn(testUnitUse);

        testUnitUseDTO.setNameCs("aktualizované bydlení");

        // When
        UnitUseDTO result = unitUseService.update(1L, testUnitUseDTO);

        // Then
        verify(repository, times(1)).save(any(UnitUse.class));
        verify(eventPublisher, times(1)).publishUpdate(
                eq("Způsoby využití jednotky"), eq("UNIT_USE"), eq(1L), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent unit use")
    void shouldThrowExceptionWhenUpdatingNonExistentUnitUse() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> unitUseService.update(999L, testUnitUseDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unit use not found with id: 999");
    }

    @Test
    @DisplayName("Should delete unit use")
    void shouldDeleteUnitUse() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testUnitUse));

        // When
        unitUseService.delete(1L);

        // Then
        verify(repository, times(1)).deleteById(1L);
        verify(eventPublisher, times(1)).publishDelete(
                eq("Způsoby využití jednotky"), eq("UNIT_USE"), eq(1L), eq("1"), eq("bydlení"));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent unit use")
    void shouldThrowExceptionWhenDeletingNonExistentUnitUse() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> unitUseService.delete(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unit use not found with id: 999");
    }

}
