package cz.intelis.legislativeenums.cuzk.landtypeuse;

import cz.intelis.legislativeenums.notification.CodelistEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
@DisplayName("LandTypeUseService Unit Tests")
class LandTypeUseServiceTest {

    @Mock
    private LandTypeUseRepository repository;

    @Mock
    private CodelistEventPublisher eventPublisher;

    @InjectMocks
    private LandTypeUseService landTypeUseService;

    private LandTypeUse testLandTypeUse;
    private LandTypeUseDTO testLandTypeUseDTO;

    @BeforeEach
    void setUp() {
        testLandTypeUse = new LandTypeUse();
        testLandTypeUse.setId(1L);
        testLandTypeUse.setLandTypeCode("2");
        testLandTypeUse.setLandUseCode("1");
        testLandTypeUseDTO = new LandTypeUseDTO();
        testLandTypeUseDTO.setId(1L);
        testLandTypeUseDTO.setLandTypeCode("2");
        testLandTypeUseDTO.setLandUseCode("1");
    }

    @Test
    @DisplayName("Should find all land type uses")
    void shouldFindAllLandTypeUses() {
        // Given
        LandTypeUse ltu2 = new LandTypeUse();
        ltu2.setId(2L);
        ltu2.setLandTypeCode("3");
        ltu2.setLandUseCode("2");
        when(repository.findAllOrdered()).thenReturn(Arrays.asList(testLandTypeUse, ltu2));

        // When
        List<LandTypeUseDTO> result = landTypeUseService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getLandTypeCode()).isEqualTo("2");
        assertThat(result.get(1).getLandTypeCode()).isEqualTo("3");
        verify(repository, times(1)).findAllOrdered();
    }

    @Test
    @DisplayName("Should find land type use by ID")
    void shouldFindLandTypeUseById() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testLandTypeUse));

        // When
        LandTypeUseDTO result = landTypeUseService.findById(1L);

        // Then
        assertThat(result.getLandTypeCode()).isEqualTo("2");
        assertThat(result.getLandUseCode()).isEqualTo("1");
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when land type use not found by ID")
    void shouldThrowExceptionWhenLandTypeUseNotFoundById() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> landTypeUseService.findById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Land type use relation not found with id: 999");
    }

    @Test
    @DisplayName("Should find land type uses by land type code")
    void shouldFindLandTypeUsesByLandTypeCode() {
        // Given
        when(repository.findByLandTypeCode("2")).thenReturn(Arrays.asList(testLandTypeUse));

        // When
        List<LandTypeUseDTO> result = landTypeUseService.findByLandTypeCode("2");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLandTypeCode()).isEqualTo("2");
        verify(repository, times(1)).findByLandTypeCode("2");
    }

    @Test
    @DisplayName("Should find land type uses by land use code")
    void shouldFindLandTypeUsesByLandUseCode() {
        // Given
        when(repository.findByLandUseCode("1")).thenReturn(Arrays.asList(testLandTypeUse));

        // When
        List<LandTypeUseDTO> result = landTypeUseService.findByLandUseCode("1");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLandUseCode()).isEqualTo("1");
        verify(repository, times(1)).findByLandUseCode("1");
    }

    @Test
    @DisplayName("Should create new land type use")
    void shouldCreateNewLandTypeUse() {
        // Given
        when(repository.existsByLandTypeCodeAndLandUseCode(anyString(), anyString())).thenReturn(false);
        when(repository.save(any(LandTypeUse.class))).thenAnswer(inv -> {
            LandTypeUse ltu = inv.getArgument(0);
            ltu.setId(1L);
            return ltu;
        });

        // When
        LandTypeUseDTO result = landTypeUseService.create(testLandTypeUseDTO);

        // Then
        assertThat(result.getLandTypeCode()).isEqualTo("2");
        assertThat(result.getLandUseCode()).isEqualTo("1");
        verify(repository, times(1)).save(any(LandTypeUse.class));
        verify(eventPublisher, times(1)).publishInsert(
                eq("Vazby druh pozemku - využití"), eq("LAND_TYPE_USE"), eq(1L), eq("2-1"), eq("2-1"));
    }

    @Test
    @DisplayName("Should throw exception when creating land type use with existing relation")
    void shouldThrowExceptionWhenCreatingLandTypeUseWithExistingRelation() {
        // Given
        when(repository.existsByLandTypeCodeAndLandUseCode("2", "1")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> landTypeUseService.create(testLandTypeUseDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Land type use relation already exists for land type 2 and land use 1");
    }

    @Test
    @DisplayName("Should update existing land type use")
    void shouldUpdateExistingLandTypeUse() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testLandTypeUse));
        when(repository.save(any(LandTypeUse.class))).thenReturn(testLandTypeUse);
        // When
        LandTypeUseDTO result = landTypeUseService.update(1L, testLandTypeUseDTO);

        // Then
        verify(repository, times(1)).save(any(LandTypeUse.class));
        verify(eventPublisher, times(1)).publishUpdate(
                eq("Vazby druh pozemku - využití"), eq("LAND_TYPE_USE"), eq(1L), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent land type use")
    void shouldThrowExceptionWhenUpdatingNonExistentLandTypeUse() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> landTypeUseService.update(999L, testLandTypeUseDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Land type use relation not found with id: 999");
    }

    @Test
    @DisplayName("Should delete land type use")
    void shouldDeleteLandTypeUse() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testLandTypeUse));

        // When
        landTypeUseService.delete(1L);

        // Then
        verify(repository, times(1)).deleteById(1L);
        verify(eventPublisher, times(1)).publishDelete(
                eq("Vazby druh pozemku - využití"), eq("LAND_TYPE_USE"), eq(1L), eq("2-1"), eq("2-1"));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent land type use")
    void shouldThrowExceptionWhenDeletingNonExistentLandTypeUse() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> landTypeUseService.delete(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Land type use relation not found with id: 999");
    }

}
