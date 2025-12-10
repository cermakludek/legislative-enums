package cz.intelis.legislativeenums.voltagelevel;

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
@DisplayName("VoltageLevelService Unit Tests")
class VoltageLevelServiceTest {

    @Mock
    private VoltageLevelRepository voltageLevelRepository;

    @Mock
    private CodelistEventPublisher eventPublisher;

    @InjectMocks
    private VoltageLevelService voltageLevelService;

    private VoltageLevel testVoltageLevel;
    private VoltageLevelDTO testVoltageLevelDTO;

    @BeforeEach
    void setUp() {
        testVoltageLevel = new VoltageLevel();
        testVoltageLevel.setId(1L);
        testVoltageLevel.setCode("MN");
        testVoltageLevel.setNameCs("malé napětí");
        testVoltageLevel.setNameEn("extra-low voltage");
        testVoltageLevel.setVoltageRangeCs("do 50 V AC / 120 V DC");
        testVoltageLevel.setVoltageRangeEn("up to 50 V AC / 120 V DC");
        testVoltageLevel.setSortOrder(1);

        testVoltageLevelDTO = new VoltageLevelDTO();
        testVoltageLevelDTO.setId(1L);
        testVoltageLevelDTO.setCode("MN");
        testVoltageLevelDTO.setNameCs("malé napětí");
        testVoltageLevelDTO.setNameEn("extra-low voltage");
        testVoltageLevelDTO.setVoltageRangeCs("do 50 V AC / 120 V DC");
        testVoltageLevelDTO.setVoltageRangeEn("up to 50 V AC / 120 V DC");
        testVoltageLevelDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("Should find all voltage levels")
    void shouldFindAllVoltageLevels() {
        // Given
        VoltageLevel vl2 = new VoltageLevel();
        vl2.setId(2L);
        vl2.setCode("NN");
        vl2.setNameCs("nízké napětí");

        when(voltageLevelRepository.findAllOrdered()).thenReturn(Arrays.asList(testVoltageLevel, vl2));

        // When
        List<VoltageLevelDTO> result = voltageLevelService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCode()).isEqualTo("MN");
        assertThat(result.get(1).getCode()).isEqualTo("NN");
        verify(voltageLevelRepository, times(1)).findAllOrdered();
    }

    @Test
    @DisplayName("Should find voltage level by ID")
    void shouldFindVoltageLevelById() {
        // Given
        when(voltageLevelRepository.findById(1L)).thenReturn(Optional.of(testVoltageLevel));

        // When
        VoltageLevelDTO result = voltageLevelService.findById(1L);

        // Then
        assertThat(result.getCode()).isEqualTo("MN");
        assertThat(result.getNameCs()).isEqualTo("malé napětí");
        verify(voltageLevelRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when voltage level not found by ID")
    void shouldThrowExceptionWhenVoltageLevelNotFoundById() {
        // Given
        when(voltageLevelRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> voltageLevelService.findById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Voltage level not found with id: 999");
    }

    @Test
    @DisplayName("Should find voltage level by code")
    void shouldFindVoltageLevelByCode() {
        // Given
        when(voltageLevelRepository.findByCode("MN")).thenReturn(Optional.of(testVoltageLevel));

        // When
        VoltageLevelDTO result = voltageLevelService.findByCode("MN");

        // Then
        assertThat(result.getCode()).isEqualTo("MN");
        verify(voltageLevelRepository, times(1)).findByCode("MN");
    }

    @Test
    @DisplayName("Should throw exception when voltage level not found by code")
    void shouldThrowExceptionWhenVoltageLevelNotFoundByCode() {
        // Given
        when(voltageLevelRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> voltageLevelService.findByCode("INVALID"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Voltage level not found with code: INVALID");
    }

    @Test
    @DisplayName("Should create new voltage level")
    void shouldCreateNewVoltageLevel() {
        // Given
        when(voltageLevelRepository.existsByCode(anyString())).thenReturn(false);
        when(voltageLevelRepository.save(any(VoltageLevel.class))).thenAnswer(inv -> {
            VoltageLevel vl = inv.getArgument(0);
            vl.setId(1L);
            return vl;
        });

        // When
        VoltageLevelDTO result = voltageLevelService.create(testVoltageLevelDTO);

        // Then
        assertThat(result.getCode()).isEqualTo("MN");
        verify(voltageLevelRepository, times(1)).save(any(VoltageLevel.class));
        verify(eventPublisher, times(1)).publishInsert(
                eq("Úrovně napětí"), eq("VOLTAGE_LEVEL"), eq(1L), eq("MN"), eq("malé napětí"));
    }

    @Test
    @DisplayName("Should throw exception when creating voltage level with existing code")
    void shouldThrowExceptionWhenCreatingVoltageLevelWithExistingCode() {
        // Given
        when(voltageLevelRepository.existsByCode("MN")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> voltageLevelService.create(testVoltageLevelDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Voltage level with code MN already exists");
    }

    @Test
    @DisplayName("Should update existing voltage level")
    void shouldUpdateExistingVoltageLevel() {
        // Given
        when(voltageLevelRepository.findById(1L)).thenReturn(Optional.of(testVoltageLevel));
        when(voltageLevelRepository.save(any(VoltageLevel.class))).thenReturn(testVoltageLevel);

        testVoltageLevelDTO.setNameCs("aktualizované napětí");

        // When
        VoltageLevelDTO result = voltageLevelService.update(1L, testVoltageLevelDTO);

        // Then
        verify(voltageLevelRepository, times(1)).save(any(VoltageLevel.class));
        verify(eventPublisher, times(1)).publishUpdate(
                eq("Úrovně napětí"), eq("VOLTAGE_LEVEL"), eq(1L), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent voltage level")
    void shouldThrowExceptionWhenUpdatingNonExistentVoltageLevel() {
        // Given
        when(voltageLevelRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> voltageLevelService.update(999L, testVoltageLevelDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Voltage level not found with id: 999");
    }

    @Test
    @DisplayName("Should delete voltage level")
    void shouldDeleteVoltageLevel() {
        // Given
        when(voltageLevelRepository.findById(1L)).thenReturn(Optional.of(testVoltageLevel));

        // When
        voltageLevelService.delete(1L);

        // Then
        verify(voltageLevelRepository, times(1)).deleteById(1L);
        verify(eventPublisher, times(1)).publishDelete(
                eq("Úrovně napětí"), eq("VOLTAGE_LEVEL"), eq(1L), eq("MN"), eq("malé napětí"));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent voltage level")
    void shouldThrowExceptionWhenDeletingNonExistentVoltageLevel() {
        // Given
        when(voltageLevelRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> voltageLevelService.delete(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Voltage level not found with id: 999");
    }

}
