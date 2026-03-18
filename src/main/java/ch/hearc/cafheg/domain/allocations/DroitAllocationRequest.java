package ch.hearc.cafheg.domain.allocations;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Contrat explicite pour determiner quel parent a droit aux allocations.
 */
public record DroitAllocationRequest(
    @NotNull Boolean parent1ActiviteLucrative,
    @NotNull Boolean parent2ActiviteLucrative,
    @NotNull @DecimalMin("0.0") BigDecimal parent1Salaire,
    @NotNull @DecimalMin("0.0") BigDecimal parent2Salaire
) {
}

