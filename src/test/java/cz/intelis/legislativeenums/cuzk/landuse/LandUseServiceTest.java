package cz.intelis.legislativeenums.cuzk.landuse;

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
@DisplayName("LandUseService Unit Tests")
class LandUseServiceTest {

    @Mock
    private LandUseRepository repository;

    @Mock
    private CodelistEventPublisher eventPublisher;

    @InjectMocks
    private LandUseService landUseService;

    private LandUse testLandUse;
    private LandUseDTO testLandUseDTO;

    @BeforeEach
    void setUp() {
        testLandUse = new LandUse();
        testLandUse.setId(1L);
        testLandUse.setCode("1");
        testLandUse.setNameCs("skleník, pařeniště");
        testLandUse.setNameEn("greenhouse, hotbed");
        testLandUse.setAbbreviation("SK");
        testLandUse.setLandParcelTypeCode("PKN");
        testLandUse.setValidFrom(LocalDate.of(2000, 1, 1));
        testLandUse.setSortOrder(1);

        testLandUseDTO = new LandUseDTO();
        testLandUseDTO.setId(1L);
        testLandUseDTO.setCode("1");
        testLandUseDTO.setNameCs("skleník, pařeniště");
        testLandUseDTO.setNameEn("greenhouse, hotbed");
        testLandUseDTO.setAbbreviation("SK");
        testLandUseDTO.setLandParcelTypeCode("PKN");
        testLandUseDTO.setValidFrom(LocalDate.of(2000, 1, 1));
        testLandUseDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("Should find all land uses")
    void shouldFindAllLandUses() {
        // Given
        LandUse lu2 = new LandUse();
        lu2.setId(2L);
        lu2.setCode("2");
        lu2.setNameCs("školka");
        when(repository.findAllOrdered()).thenReturn(Arrays.asList(testLandUse, lu2));

        // When
        List<LandUseDTO> result = landUseService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCode()).isEqualTo("1");
        assertThat(result.get(1).getCode()).isEqualTo("2");
        verify(repository, times(1)).findAllOrdered();
    }

    @Test
    @DisplayName("Should find land use by ID")
    void shouldFindLandUseById() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testLandUse));

        // When
        LandUseDTO result = landUseService.findById(1L);

        // Then
        assertThat(result.getCode()).isEqualTo("1");
        assertThat(result.getNameCs()).isEqualTo("skleník, pařeniště");
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when land use not found by ID")
    void shouldThrowExceptionWhenLandUseNotFoundById() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> landUseService.findById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Land use not found with id: 999");
    }

    @Test
    @DisplayName("Should find land use by code")
    void shouldFindLandUseByCode() {
        // Given
        when(repository.findByCode("1")).thenReturn(Optional.of(testLandUse));

        // When
        LandUseDTO result = landUseService.findByCode("1");

        // Then
        assertThat(result.getCode()).isEqualTo("1");
        verify(repository, times(1)).findByCode("1");
    }

    @Test
    @DisplayName("Should throw exception when land use not found by code")
    void shouldThrowExceptionWhenLandUseNotFoundByCode() {
        // Given
        when(repository.findByCode("INVALID")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> landUseService.findByCode("INVALID"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Land use not found with code: INVALID");
    }

    @Test
    @DisplayName("Should create new land use")
    void shouldCreateNewLandUse() {
        // Given
        when(repository.existsByCode(anyString())).thenReturn(false);
        when(repository.save(any(LandUse.class))).thenAnswer(inv -> {
            LandUse lu = inv.getArgument(0);
            lu.setId(1L);
            return lu;
        });

        // When
        LandUseDTO result = landUseService.create(testLandUseDTO);

        // Then
        assertThat(result.getCode()).isEqualTo("1");
        verify(repository, times(1)).save(any(LandUse.class));
        verify(eventPublisher, times(1)).publishInsert(
                eq("Způsoby využití pozemku"), eq("LAND_USE"), eq(1L), eq("1"), eq("skleník, pařeniště"));
    }

    @Test
    @DisplayName("Should throw exception when creating land use with existing code")
    void shouldThrowExceptionWhenCreatingLandUseWithExistingCode() {
        // Given
        when(repository.existsByCode("1")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> landUseService.create(testLandUseDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Land use with code 1 already exists");
    }

    @Test
    @DisplayName("Should update existing land use")
    void shouldUpdateExistingLandUse() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testLandUse));
        when(repository.save(any(LandUse.class))).thenReturn(testLandUse);

        testLandUseDTO.setNameCs("aktualizovaný způsob");

        // When
        LandUseDTO result = landUseService.update(1L, testLandUseDTO);

        // Then
        verify(repository, times(1)).save(any(LandUse.class));
        verify(eventPublisher, times(1)).publishUpdate(
                eq("Způsoby využití pozemku"), eq("LAND_USE"), eq(1L), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent land use")
    void shouldThrowExceptionWhenUpdatingNonExistentLandUse() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> landUseService.update(999L, testLandUseDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Land use not found with id: 999");
    }

    @Test
    @DisplayName("Should delete land use")
    void shouldDeleteLandUse() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testLandUse));

        // When
        landUseService.delete(1L);

        // Then
        verify(repository, times(1)).deleteById(1L);
        verify(eventPublisher, times(1)).publishDelete(
                eq("Způsoby využití pozemku"), eq("LAND_USE"), eq(1L), eq("1"), eq("skleník, pařeniště"));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent land use")
    void shouldThrowExceptionWhenDeletingNonExistentLandUse() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> landUseService.delete(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Land use not found with id: 999");
    }

}
