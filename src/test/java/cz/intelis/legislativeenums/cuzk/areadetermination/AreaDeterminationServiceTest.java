package cz.intelis.legislativeenums.cuzk.areadetermination;

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
@DisplayName("AreaDeterminationService Unit Tests")
class AreaDeterminationServiceTest {

    @Mock
    private AreaDeterminationRepository repository;

    @Mock
    private CodelistEventPublisher eventPublisher;

    @InjectMocks
    private AreaDeterminationService areaDeterminationService;

    private AreaDetermination testAreaDetermination;
    private AreaDeterminationDTO testAreaDeterminationDTO;

    @BeforeEach
    void setUp() {
        testAreaDetermination = new AreaDetermination();
        testAreaDetermination.setId(1L);
        testAreaDetermination.setCode("0");
        testAreaDetermination.setNameCs("jiným číselným způsobem");
        testAreaDetermination.setNameEn("other numerical method");
        testAreaDetermination.setValidFrom(LocalDate.of(2000, 1, 1));
        testAreaDetermination.setSortOrder(1);

        testAreaDeterminationDTO = new AreaDeterminationDTO();
        testAreaDeterminationDTO.setId(1L);
        testAreaDeterminationDTO.setCode("0");
        testAreaDeterminationDTO.setNameCs("jiným číselným způsobem");
        testAreaDeterminationDTO.setNameEn("other numerical method");
        testAreaDeterminationDTO.setValidFrom(LocalDate.of(2000, 1, 1));
        testAreaDeterminationDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("Should find all area determinations")
    void shouldFindAllAreaDeterminations() {
        // Given
        AreaDetermination ad2 = new AreaDetermination();
        ad2.setId(2L);
        ad2.setCode("1");
        ad2.setNameCs("ze souřadnic v S-JTSK");
        when(repository.findAllOrdered()).thenReturn(Arrays.asList(testAreaDetermination, ad2));

        // When
        List<AreaDeterminationDTO> result = areaDeterminationService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCode()).isEqualTo("0");
        assertThat(result.get(1).getCode()).isEqualTo("1");
        verify(repository, times(1)).findAllOrdered();
    }

    @Test
    @DisplayName("Should find area determination by ID")
    void shouldFindAreaDeterminationById() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testAreaDetermination));

        // When
        AreaDeterminationDTO result = areaDeterminationService.findById(1L);

        // Then
        assertThat(result.getCode()).isEqualTo("0");
        assertThat(result.getNameCs()).isEqualTo("jiným číselným způsobem");
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when area determination not found by ID")
    void shouldThrowExceptionWhenAreaDeterminationNotFoundById() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> areaDeterminationService.findById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Area determination not found with id: 999");
    }

    @Test
    @DisplayName("Should find area determination by code")
    void shouldFindAreaDeterminationByCode() {
        // Given
        when(repository.findByCode("0")).thenReturn(Optional.of(testAreaDetermination));

        // When
        AreaDeterminationDTO result = areaDeterminationService.findByCode("0");

        // Then
        assertThat(result.getCode()).isEqualTo("0");
        verify(repository, times(1)).findByCode("0");
    }

    @Test
    @DisplayName("Should throw exception when area determination not found by code")
    void shouldThrowExceptionWhenAreaDeterminationNotFoundByCode() {
        // Given
        when(repository.findByCode("INVALID")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> areaDeterminationService.findByCode("INVALID"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Area determination not found with code: INVALID");
    }

    @Test
    @DisplayName("Should create new area determination")
    void shouldCreateNewAreaDetermination() {
        // Given
        when(repository.existsByCode(anyString())).thenReturn(false);
        when(repository.save(any(AreaDetermination.class))).thenAnswer(inv -> {
            AreaDetermination ad = inv.getArgument(0);
            ad.setId(1L);
            return ad;
        });

        // When
        AreaDeterminationDTO result = areaDeterminationService.create(testAreaDeterminationDTO);

        // Then
        assertThat(result.getCode()).isEqualTo("0");
        verify(repository, times(1)).save(any(AreaDetermination.class));
        verify(eventPublisher, times(1)).publishInsert(
                eq("Způsoby určení výměry"), eq("AREA_DETERMINATION"), eq(1L), eq("0"), eq("jiným číselným způsobem"));
    }

    @Test
    @DisplayName("Should throw exception when creating area determination with existing code")
    void shouldThrowExceptionWhenCreatingAreaDeterminationWithExistingCode() {
        // Given
        when(repository.existsByCode("0")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> areaDeterminationService.create(testAreaDeterminationDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Area determination with code 0 already exists");
    }

    @Test
    @DisplayName("Should update existing area determination")
    void shouldUpdateExistingAreaDetermination() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testAreaDetermination));
        when(repository.save(any(AreaDetermination.class))).thenReturn(testAreaDetermination);

        testAreaDeterminationDTO.setNameCs("aktualizovaný způsob");

        // When
        AreaDeterminationDTO result = areaDeterminationService.update(1L, testAreaDeterminationDTO);

        // Then
        verify(repository, times(1)).save(any(AreaDetermination.class));
        verify(eventPublisher, times(1)).publishUpdate(
                eq("Způsoby určení výměry"), eq("AREA_DETERMINATION"), eq(1L), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent area determination")
    void shouldThrowExceptionWhenUpdatingNonExistentAreaDetermination() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> areaDeterminationService.update(999L, testAreaDeterminationDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Area determination not found with id: 999");
    }

    @Test
    @DisplayName("Should delete area determination")
    void shouldDeleteAreaDetermination() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testAreaDetermination));

        // When
        areaDeterminationService.delete(1L);

        // Then
        verify(repository, times(1)).deleteById(1L);
        verify(eventPublisher, times(1)).publishDelete(
                eq("Způsoby určení výměry"), eq("AREA_DETERMINATION"), eq(1L), eq("0"), eq("jiným číselným způsobem"));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent area determination")
    void shouldThrowExceptionWhenDeletingNonExistentAreaDetermination() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> areaDeterminationService.delete(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Area determination not found with id: 999");
    }

}
