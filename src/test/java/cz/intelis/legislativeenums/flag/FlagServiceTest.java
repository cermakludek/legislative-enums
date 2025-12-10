package cz.intelis.legislativeenums.flag;

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
@DisplayName("FlagService Unit Tests")
class FlagServiceTest {

    @Mock
    private FlagRepository flagRepository;

    @Mock
    private CodelistEventPublisher eventPublisher;

    @InjectMocks
    private FlagService flagService;

    private Flag testFlag;
    private FlagDTO testFlagDTO;

    @BeforeEach
    void setUp() {
        testFlag = new Flag();
        testFlag.setId(1L);
        testFlag.setCode("TEST");
        testFlag.setNameCs("Testovací");
        testFlag.setNameEn("Test");
        testFlag.setDescriptionCs("Popis česky");
        testFlag.setDescriptionEn("Description English");
        testFlag.setColor("#FF0000");
        testFlag.setIconClass("bi-flag");
        testFlag.setActive(true);
        testFlag.setSortOrder(1);

        testFlagDTO = new FlagDTO();
        testFlagDTO.setId(1L);
        testFlagDTO.setCode("TEST");
        testFlagDTO.setNameCs("Testovací");
        testFlagDTO.setNameEn("Test");
        testFlagDTO.setDescriptionCs("Popis česky");
        testFlagDTO.setDescriptionEn("Description English");
        testFlagDTO.setColor("#FF0000");
        testFlagDTO.setIconClass("bi-flag");
        testFlagDTO.setActive(true);
        testFlagDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("Should find all flags")
    void shouldFindAllFlags() {
        // Given
        Flag flag2 = new Flag();
        flag2.setId(2L);
        flag2.setCode("FLAG2");
        flag2.setNameCs("Druhý");
        flag2.setActive(true);

        when(flagRepository.findAllOrdered()).thenReturn(Arrays.asList(testFlag, flag2));

        // When
        List<FlagDTO> result = flagService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCode()).isEqualTo("TEST");
        assertThat(result.get(1).getCode()).isEqualTo("FLAG2");
        verify(flagRepository, times(1)).findAllOrdered();
    }

    @Test
    @DisplayName("Should find all active flags")
    void shouldFindAllActiveFlags() {
        // Given
        when(flagRepository.findByActiveTrueOrderBySortOrderAscCodeAsc()).thenReturn(Arrays.asList(testFlag));

        // When
        List<FlagDTO> result = flagService.findAllActive();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getActive()).isTrue();
        verify(flagRepository, times(1)).findByActiveTrueOrderBySortOrderAscCodeAsc();
    }

    @Test
    @DisplayName("Should find flag by ID")
    void shouldFindFlagById() {
        // Given
        when(flagRepository.findById(1L)).thenReturn(Optional.of(testFlag));

        // When
        FlagDTO result = flagService.findById(1L);

        // Then
        assertThat(result.getCode()).isEqualTo("TEST");
        assertThat(result.getNameCs()).isEqualTo("Testovací");
        verify(flagRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when flag not found by ID")
    void shouldThrowExceptionWhenFlagNotFoundById() {
        // Given
        when(flagRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> flagService.findById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Flag not found with id: 999");
    }

    @Test
    @DisplayName("Should find flag by code")
    void shouldFindFlagByCode() {
        // Given
        when(flagRepository.findByCode("TEST")).thenReturn(Optional.of(testFlag));

        // When
        FlagDTO result = flagService.findByCode("TEST");

        // Then
        assertThat(result.getCode()).isEqualTo("TEST");
        verify(flagRepository, times(1)).findByCode("TEST");
    }

    @Test
    @DisplayName("Should throw exception when flag not found by code")
    void shouldThrowExceptionWhenFlagNotFoundByCode() {
        // Given
        when(flagRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> flagService.findByCode("INVALID"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Flag not found with code: INVALID");
    }

    @Test
    @DisplayName("Should create new flag")
    void shouldCreateNewFlag() {
        // Given
        when(flagRepository.existsByCode(anyString())).thenReturn(false);
        when(flagRepository.save(any(Flag.class))).thenAnswer(inv -> {
            Flag f = inv.getArgument(0);
            f.setId(1L);
            return f;
        });

        // When
        FlagDTO result = flagService.create(testFlagDTO);

        // Then
        assertThat(result.getCode()).isEqualTo("TEST");
        verify(flagRepository, times(1)).save(any(Flag.class));
        verify(eventPublisher, times(1)).publishInsert(
                eq("Příznaky"), eq("FLAG"), eq(1L), eq("TEST"), eq("Testovací"));
    }

    @Test
    @DisplayName("Should throw exception when creating flag with existing code")
    void shouldThrowExceptionWhenCreatingFlagWithExistingCode() {
        // Given
        when(flagRepository.existsByCode("TEST")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> flagService.create(testFlagDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Flag with code TEST already exists");
    }

    @Test
    @DisplayName("Should update existing flag")
    void shouldUpdateExistingFlag() {
        // Given
        when(flagRepository.findById(1L)).thenReturn(Optional.of(testFlag));
        when(flagRepository.save(any(Flag.class))).thenReturn(testFlag);

        testFlagDTO.setNameCs("Aktualizovaný");

        // When
        FlagDTO result = flagService.update(1L, testFlagDTO);

        // Then
        verify(flagRepository, times(1)).save(any(Flag.class));
        verify(eventPublisher, times(1)).publishUpdate(
                eq("Příznaky"), eq("FLAG"), eq(1L), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent flag")
    void shouldThrowExceptionWhenUpdatingNonExistentFlag() {
        // Given
        when(flagRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> flagService.update(999L, testFlagDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Flag not found with id: 999");
    }

    @Test
    @DisplayName("Should throw exception when updating flag with existing code")
    void shouldThrowExceptionWhenUpdatingFlagWithExistingCode() {
        // Given
        testFlagDTO.setCode("NEWCODE");
        when(flagRepository.findById(1L)).thenReturn(Optional.of(testFlag));
        when(flagRepository.existsByCode("NEWCODE")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> flagService.update(1L, testFlagDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Flag with code NEWCODE already exists");
    }

    @Test
    @DisplayName("Should allow updating flag keeping same code")
    void shouldAllowUpdatingFlagKeepingSameCode() {
        // Given
        when(flagRepository.findById(1L)).thenReturn(Optional.of(testFlag));
        when(flagRepository.save(any(Flag.class))).thenReturn(testFlag);

        // Code remains "TEST" which is the same as existing
        testFlagDTO.setNameCs("Aktualizovaný název");

        // When
        FlagDTO result = flagService.update(1L, testFlagDTO);

        // Then - should not throw exception
        verify(flagRepository, times(1)).save(any(Flag.class));
    }

    @Test
    @DisplayName("Should delete flag")
    void shouldDeleteFlag() {
        // Given
        when(flagRepository.findById(1L)).thenReturn(Optional.of(testFlag));

        // When
        flagService.delete(1L);

        // Then
        verify(flagRepository, times(1)).deleteById(1L);
        verify(eventPublisher, times(1)).publishDelete(
                eq("Příznaky"), eq("FLAG"), eq(1L), eq("TEST"), eq("Testovací"));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent flag")
    void shouldThrowExceptionWhenDeletingNonExistentFlag() {
        // Given
        when(flagRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> flagService.delete(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Flag not found with id: 999");
    }

    @Test
    @DisplayName("Should set default active to true when creating flag")
    void shouldSetDefaultActiveToTrueWhenCreatingFlag() {
        // Given
        testFlagDTO.setActive(null);
        when(flagRepository.existsByCode(anyString())).thenReturn(false);
        when(flagRepository.save(any(Flag.class))).thenAnswer(inv -> {
            Flag f = inv.getArgument(0);
            f.setId(1L);
            return f;
        });

        // When
        flagService.create(testFlagDTO);

        // Then
        ArgumentCaptor<Flag> captor = ArgumentCaptor.forClass(Flag.class);
        verify(flagRepository).save(captor.capture());
        assertThat(captor.getValue().getActive()).isTrue();
    }

    @Test
    @DisplayName("Should preserve all fields when updating flag")
    void shouldPreserveAllFieldsWhenUpdatingFlag() {
        // Given
        when(flagRepository.findById(1L)).thenReturn(Optional.of(testFlag));
        when(flagRepository.save(any(Flag.class))).thenAnswer(inv -> inv.getArgument(0));

        testFlagDTO.setColor("#00FF00");
        testFlagDTO.setIconClass("bi-star");
        testFlagDTO.setSortOrder(5);

        // When
        flagService.update(1L, testFlagDTO);

        // Then
        ArgumentCaptor<Flag> captor = ArgumentCaptor.forClass(Flag.class);
        verify(flagRepository).save(captor.capture());
        Flag saved = captor.getValue();
        assertThat(saved.getColor()).isEqualTo("#00FF00");
        assertThat(saved.getIconClass()).isEqualTo("bi-star");
        assertThat(saved.getSortOrder()).isEqualTo(5);
    }
}
