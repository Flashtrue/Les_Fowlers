package ch.hearc.cafheg.domain.allocations;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class CantonTest {

  @Test
  void fromValue_GivenFR_ShouldBeFR() {
    assertThat(Canton.fromValue("FR")).isEqualTo(Canton.FR);
  }

  @Test
  void fromValue_GivenMM_ShouldBeNull() {
    assertThat(Canton.fromValue("MM")).isNull();
  }

}