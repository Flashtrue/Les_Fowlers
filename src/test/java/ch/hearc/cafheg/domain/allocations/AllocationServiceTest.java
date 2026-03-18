package ch.hearc.cafheg.domain.allocations;

import ch.hearc.cafheg.domain.common.Montant;
import ch.hearc.cafheg.infrastructure.persistence.AllocataireMapper;
import ch.hearc.cafheg.infrastructure.persistence.AllocationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class AllocationServiceTest {

  private AllocationService allocationService;

  private AllocataireMapper allocataireMapper;
  private AllocationMapper allocationMapper;

  @BeforeEach
  void setUp() {
    allocataireMapper = Mockito.mock(AllocataireMapper.class);
    allocationMapper = Mockito.mock(AllocationMapper.class);

    allocationService = new AllocationService(allocataireMapper, allocationMapper);
  }

  @Test
  void findAllAllocataires_GivenEmptyAllocataires_ShouldBeEmpty() {
    Mockito.when(allocataireMapper.findAll("Geiser")).thenReturn(Collections.emptyList());
    List<Allocataire> all = allocationService.findAllAllocataires("Geiser");
    assertThat(all).isEmpty();
  }

  @Test
  void findAllAllocataires_Given2Geiser_ShouldBe2() {
    Mockito.when(allocataireMapper.findAll("Geiser"))
        .thenReturn(Arrays.asList(new Allocataire(new NoAVS("1000-2000"), "Geiser", "Arnaud"),
            new Allocataire(new NoAVS("1000-2001"), "Geiser", "Aurélie")));
    List<Allocataire> all = allocationService.findAllAllocataires("Geiser");
    assertAll(() -> assertThat(all.size()).isEqualTo(2),
        () -> assertThat(all.get(0).getNoAVS()).isEqualTo(new NoAVS("1000-2000")),
        () -> assertThat(all.get(0).getNom()).isEqualTo("Geiser"),
        () -> assertThat(all.get(0).getPrenom()).isEqualTo("Arnaud"),
        () -> assertThat(all.get(1).getNoAVS()).isEqualTo(new NoAVS("1000-2001")),
        () -> assertThat(all.get(1).getNom()).isEqualTo("Geiser"),
        () -> assertThat(all.get(1).getPrenom()).isEqualTo("Aurélie"));
  }

  @Test
  void findAllocationsActuelles() {
    Mockito.when(allocationMapper.findAll())
        .thenReturn(Arrays.asList(new Allocation(new Montant(new BigDecimal(1000)), Canton.NE,
                                                 LocalDate.now(), null), new Allocation(new Montant(new BigDecimal(2000)), Canton.FR,
            LocalDate.now(), null)));
    List<Allocation> all = allocationService.findAllocationsActuelles();
    assertAll(() -> assertThat(all.size()).isEqualTo(2),
        () -> assertThat(all.get(0).getMontant()).isEqualTo(new Montant(new BigDecimal(1000))),
        () -> assertThat(all.get(0).getCanton()).isEqualTo(Canton.NE),
        () -> assertThat(all.get(0).getDebut()).isEqualTo(LocalDate.now()),
        () -> assertThat(all.get(0).getFin()).isNull(),
        () -> assertThat(all.get(1).getMontant()).isEqualTo(new Montant(new BigDecimal(2000))),
        () -> assertThat(all.get(1).getCanton()).isEqualTo(Canton.FR),
        () -> assertThat(all.get(1).getDebut()).isEqualTo(LocalDate.now()),
        () -> assertThat(all.get(1).getFin()).isNull());
  }

  @Test
  void deleteAllocataire_WhenNoVersements_ShouldDeleteAndReturnTrue() {
    Mockito.when(allocataireMapper.hasVersements(42L)).thenReturn(false);
    Mockito.when(allocataireMapper.deleteById(42L)).thenReturn(true);

    boolean deleted = allocationService.deleteAllocataire(42L);

    assertThat(deleted).isTrue();
    Mockito.verify(allocataireMapper).hasVersements(42L);
    Mockito.verify(allocataireMapper).deleteById(42L);
  }

  @Test
  void deleteAllocataire_WhenHasVersements_ShouldThrowAndNotDelete() {
    Mockito.when(allocataireMapper.hasVersements(42L)).thenReturn(true);

    assertThatThrownBy(() -> allocationService.deleteAllocataire(42L))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("ne peut pas être supprimé");

    Mockito.verify(allocataireMapper).hasVersements(42L);
    Mockito.verify(allocataireMapper, Mockito.never()).deleteById(Mockito.anyLong());
  }

  @Test
  void deleteAllocataire_WhenNotFoundAndNoVersement_ShouldReturnFalse() {
    Mockito.when(allocataireMapper.hasVersements(99L)).thenReturn(false);
    Mockito.when(allocataireMapper.deleteById(99L)).thenReturn(false);

    boolean deleted = allocationService.deleteAllocataire(99L);

    assertThat(deleted).isFalse();
    Mockito.verify(allocataireMapper).hasVersements(99L);
    Mockito.verify(allocataireMapper).deleteById(99L);
  }

  @Nested
  @DisplayName("Tests pour getParentDroitAllocation")
  class GetParentDroitAllocationTests {

    private DroitAllocationRequest request(boolean p1Activite, boolean p2Activite, String salaireP1,
                                           String salaireP2) {
      return new DroitAllocationRequest(p1Activite, p2Activite,
                                        new BigDecimal(salaireP1),
                                        new BigDecimal(salaireP2));
    }

    @Test
    @DisplayName("Cas 1: Parent1 a une activité lucrative, Parent2 non -> Parent1")
    void parent1AvecActiviteLucrative_Parent2Sans_RetourneParent1() {
      String result = allocationService.getParentDroitAllocation(request(true, false, "5000", "0"));

      assertThat(result).isEqualTo("Parent1");
    }

    @Test
    @DisplayName("Cas 2: Parent2 a une activité lucrative, Parent1 non -> Parent2")
    void parent2AvecActiviteLucrative_Parent1Sans_RetourneParent2() {
      String result = allocationService.getParentDroitAllocation(request(false, true, "0", "5000"));

      assertThat(result).isEqualTo("Parent2");
    }

    @Test
    @DisplayName("Cas 3: Les deux ont une activité lucrative, Parent1 salaire supérieur -> Parent1")
    void deuxAvecActiviteLucrative_Parent1SalaireSuperieur_RetourneParent1() {
      String result = allocationService.getParentDroitAllocation(request(true, true, "6000", "5000"));

      assertThat(result).isEqualTo("Parent1");
    }

    @Test
    @DisplayName("Cas 4: Les deux ont une activité lucrative, Parent2 salaire supérieur -> Parent2")
    void deuxAvecActiviteLucrative_Parent2SalaireSuperieur_RetourneParent2() {
      String result = allocationService.getParentDroitAllocation(request(true, true, "4000", "5000"));

      assertThat(result).isEqualTo("Parent2");
    }

    @Test
    @DisplayName("Cas 5: Les deux ont une activité lucrative, salaires égaux -> Parent2")
    void deuxAvecActiviteLucrative_SalairesEgaux_RetourneParent2() {
      String result = allocationService.getParentDroitAllocation(request(true, true, "5000", "5000"));

      assertThat(result).isEqualTo("Parent2");
    }

    @Test
    @DisplayName("Cas 6: Aucun n'a d'activité lucrative, Parent1 salaire supérieur -> Parent1")
    void aucunAvecActiviteLucrative_Parent1SalaireSuperieur_RetourneParent1() {
      String result = allocationService.getParentDroitAllocation(request(false, false, "3000", "2000"));

      assertThat(result).isEqualTo("Parent1");
    }

    @Test
    @DisplayName("Cas 7: Aucun n'a d'activité lucrative, Parent2 salaire supérieur -> Parent2")
    void aucunAvecActiviteLucrative_Parent2SalaireSuperieur_RetourneParent2() {
      String result = allocationService.getParentDroitAllocation(request(false, false, "2000", "3000"));

      assertThat(result).isEqualTo("Parent2");
    }

    @Test
    @DisplayName("Cas 8: Aucun n'a d'activité lucrative, salaires égaux -> Parent2")
    void aucunAvecActiviteLucrative_SalairesEgaux_RetourneParent2() {
      String result = allocationService.getParentDroitAllocation(request(false, false, "2000", "2000"));

      assertThat(result).isEqualTo("Parent2");
    }

    @Test
    @DisplayName("Cas 9: Requête nulle -> exception explicite")
    void requeteNulle_LeveExceptionClaire() {
      assertThatThrownBy(() -> allocationService.getParentDroitAllocation(null))
          .isInstanceOf(NullPointerException.class)
          .hasMessage("La requete ne peut pas etre nulle");
    }

    @Test
    @DisplayName("Cas 10: Champ obligatoire manquant -> exception explicite")
    void champObligatoireManquant_LeveExceptionClaire() {
      DroitAllocationRequest request = new DroitAllocationRequest(true, false, null, new BigDecimal("0"));

      assertThatThrownBy(() -> allocationService.getParentDroitAllocation(request))
          .isInstanceOf(NullPointerException.class)
          .hasMessage("parent1Salaire est requis");
    }

    @Test
    @DisplayName("Cas 11: Salaire negatif -> IllegalArgumentException")
    void salaireNegatif_LeveIllegalArgumentException() {
      DroitAllocationRequest request = new DroitAllocationRequest(true, true,
                                                                  new BigDecimal("-1"),
                                                                  new BigDecimal("5000"));

      assertThatThrownBy(() -> allocationService.getParentDroitAllocation(request))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Les salaires doivent etre positifs ou nuls");
    }
  }

}