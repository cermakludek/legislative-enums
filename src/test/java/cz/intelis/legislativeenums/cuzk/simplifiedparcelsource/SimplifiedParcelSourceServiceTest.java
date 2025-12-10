package cz.intelis.legislativeenums.cuzk.simplifiedparcelsource;

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
@DisplayName("SimplifiedParcelSourceService Unit Tests")
class SimplifiedParcelSourceServiceTest {

    @Mock
    private SimplifiedParcelSourceRepository repository;

    @Mock
    private CodelistEventPublisher eventPublisher;

    @InjectMocks
    private SimplifiedParcelSourceService simplifiedParcelSourceService;

    private SimplifiedParcelSource testSimplifiedParcelSource;
    private SimplifiedParcelSourceDTO testSimplifiedParcelSourceDTO;

    @BeforeEach
    void setUp() {
        testSimplifiedParcelSource = new SimplifiedParcelSource();
        testSimplifiedParcelSource.setId(1L);
        testSimplifiedParcelSource.setCode("PK");
        testSimplifiedParcelSource.setNameCs("pozemkový katastr");
        testSimplifiedParcelSource.setNameEn("land cadastre");
        testSimplifiedParcelSource.setAbbreviation("PK");
        testSimplifiedParcelSource.setValidFrom(LocalDate.of(2000, 1, 1));
        testSimplifiedParcelSource.setSortOrder(1);

        testSimplifiedParcelSourceDTO = new SimplifiedParcelSourceDTO();
        testSimplifiedParcelSourceDTO.setId(1L);
        testSimplifiedParcelSourceDTO.setCode("PK");
        testSimplifiedParcelSourceDTO.setNameCs("pozemkový katastr");
        testSimplifiedParcelSourceDTO.setNameEn("land cadastre");
        testSimplifiedParcelSourceDTO.setAbbreviation("PK");
        testSimplifiedParcelSourceDTO.setValidFrom(LocalDate.of(2000, 1, 1));
        testSimplifiedParcelSourceDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("Should find all simplified parcel sources")
    void shouldFindAllSimplifiedParcelSources() {
        // Given
        SimplifiedParcelSource sps2 = new SimplifiedParcelSource();
        sps2.setId(2L);
        sps2.setCode("EN");
        sps2.setNameCs("evidence nemovitostí");
        when(repository.findAllOrdered()).thenReturn(Arrays.asList(testSimplifiedParcelSource, sps2));

        // When
        List<SimplifiedParcelSourceDTO> result = simplifiedParcelSourceService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCode()).isEqualTo("PK");
        assertThat(result.get(1).getCode()).isEqualTo("EN");
        verify(repository, times(1)).findAllOrdered();
    }

    @Test
    @DisplayName("Should find simplified parcel source by ID")
    void shouldFindSimplifiedParcelSourceById() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testSimplifiedParcelSource));

        // When
        SimplifiedParcelSourceDTO result = simplifiedParcelSourceService.findById(1L);

        // Then
        assertThat(result.getCode()).isEqualTo("PK");
        assertThat(result.getNameCs()).isEqualTo("pozemkový katastr");
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when simplified parcel source not found by ID")
    void shouldThrowExceptionWhenSimplifiedParcelSourceNotFoundById() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> simplifiedParcelSourceService.findById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Simplified parcel source not found with id: 999");
    }

    @Test
    @DisplayName("Should find simplified parcel source by code")
    void shouldFindSimplifiedParcelSourceByCode() {
        // Given
        when(repository.findByCode("PK")).thenReturn(Optional.of(testSimplifiedParcelSource));

        // When
        SimplifiedParcelSourceDTO result = simplifiedParcelSourceService.findByCode("PK");

        // Then
        assertThat(result.getCode()).isEqualTo("PK");
        verify(repository, times(1)).findByCode("PK");
    }

    @Test
    @DisplayName("Should throw exception when simplified parcel source not found by code")
    void shouldThrowExceptionWhenSimplifiedParcelSourceNotFoundByCode() {
        // Given
        when(repository.findByCode("INVALID")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> simplifiedParcelSourceService.findByCode("INVALID"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Simplified parcel source not found with code: INVALID");
    }

    @Test
    @DisplayName("Should create new simplified parcel source")
    void shouldCreateNewSimplifiedParcelSource() {
        // Given
        when(repository.existsByCode(anyString())).thenReturn(false);
        when(repository.save(any(SimplifiedParcelSource.class))).thenAnswer(inv -> {
            SimplifiedParcelSource sps = inv.getArgument(0);
            sps.setId(1L);
            return sps;
        });

        // When
        SimplifiedParcelSourceDTO result = simplifiedParcelSourceService.create(testSimplifiedParcelSourceDTO);

        // Then
        assertThat(result.getCode()).isEqualTo("PK");
        verify(repository, times(1)).save(any(SimplifiedParcelSource.class));
        verify(eventPublisher, times(1)).publishInsert(
                eq("Zdroje parcel zjednodušené evidence"), eq("SIMPLIFIED_PARCEL_SOURCE"), eq(1L), eq("PK"), eq("pozemkový katastr"));
    }

    @Test
    @DisplayName("Should throw exception when creating simplified parcel source with existing code")
    void shouldThrowExceptionWhenCreatingSimplifiedParcelSourceWithExistingCode() {
        // Given
        when(repository.existsByCode("PK")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> simplifiedParcelSourceService.create(testSimplifiedParcelSourceDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Simplified parcel source with code PK already exists");
    }

    @Test
    @DisplayName("Should update existing simplified parcel source")
    void shouldUpdateExistingSimplifiedParcelSource() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testSimplifiedParcelSource));
        when(repository.save(any(SimplifiedParcelSource.class))).thenReturn(testSimplifiedParcelSource);

        testSimplifiedParcelSourceDTO.setNameCs("aktualizovaný zdroj");

        // When
        SimplifiedParcelSourceDTO result = simplifiedParcelSourceService.update(1L, testSimplifiedParcelSourceDTO);

        // Then
        verify(repository, times(1)).save(any(SimplifiedParcelSource.class));
        verify(eventPublisher, times(1)).publishUpdate(
                eq("Zdroje parcel zjednodušené evidence"), eq("SIMPLIFIED_PARCEL_SOURCE"), eq(1L), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent simplified parcel source")
    void shouldThrowExceptionWhenUpdatingNonExistentSimplifiedParcelSource() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> simplifiedParcelSourceService.update(999L, testSimplifiedParcelSourceDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Simplified parcel source not found with id: 999");
    }

    @Test
    @DisplayName("Should delete simplified parcel source")
    void shouldDeleteSimplifiedParcelSource() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testSimplifiedParcelSource));

        // When
        simplifiedParcelSourceService.delete(1L);

        // Then
        verify(repository, times(1)).deleteById(1L);
        verify(eventPublisher, times(1)).publishDelete(
                eq("Zdroje parcel zjednodušené evidence"), eq("SIMPLIFIED_PARCEL_SOURCE"), eq(1L), eq("PK"), eq("pozemkový katastr"));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent simplified parcel source")
    void shouldThrowExceptionWhenDeletingNonExistentSimplifiedParcelSource() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> simplifiedParcelSourceService.delete(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Simplified parcel source not found with id: 999");
    }

}
