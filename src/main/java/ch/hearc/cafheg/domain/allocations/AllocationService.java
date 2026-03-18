package ch.hearc.cafheg.domain.allocations;

import ch.hearc.cafheg.infrastructure.persistence.AllocataireMapper;
import ch.hearc.cafheg.infrastructure.persistence.AllocationMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class AllocationService {

  private static final String PARENT_1 = "Parent1";
  private static final String PARENT_2 = "Parent2";

  private final AllocataireMapper allocataireMapper;
  private final AllocationMapper allocationMapper;

  public AllocationService(
      AllocataireMapper allocataireMapper,
      AllocationMapper allocationMapper) {
    this.allocataireMapper = allocataireMapper;
    this.allocationMapper = allocationMapper;
  }

  public List<Allocataire> findAllAllocataires(String likeNom) {
    System.out.println("Rechercher tous les allocataires");
    return allocataireMapper.findAll(likeNom);
  }

  public List<Allocation> findAllocationsActuelles() {
    return allocationMapper.findAll();
  }

  public String getParentDroitAllocation(DroitAllocationRequest request) {
    System.out.println("Déterminer quel parent a le droit aux allocations");
    Objects.requireNonNull(request, "La requete ne peut pas etre nulle");

    Boolean p1AL = Objects.requireNonNull(request.parent1ActiviteLucrative(),
                                          "parent1ActiviteLucrative est requis");
    Boolean p2AL = Objects.requireNonNull(request.parent2ActiviteLucrative(),
                                          "parent2ActiviteLucrative est requis");
    BigDecimal salaireP1 = Objects.requireNonNull(request.parent1Salaire(), "parent1Salaire est requis");
    BigDecimal salaireP2 = Objects.requireNonNull(request.parent2Salaire(), "parent2Salaire est requis");

    if (salaireP1.signum() < 0 || salaireP2.signum() < 0) {
      throw new IllegalArgumentException("Les salaires doivent etre positifs ou nuls");
    }

    if(p1AL && !p2AL) {
      return PARENT_1;
    }

    if(p2AL && !p1AL) {
      return PARENT_2;
    }

    return salaireP1.compareTo(salaireP2) > 0 ? PARENT_1 : PARENT_2;
  }
}
