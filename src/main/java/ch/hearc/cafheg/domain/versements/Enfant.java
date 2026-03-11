package ch.hearc.cafheg.domain.versements;

import ch.hearc.cafheg.domain.allocations.NoAVS;

public class Enfant {

  private final NoAVS noAVS;
  private final String nom;
  private final String prenom;

  public Enfant(NoAVS noAVS, String nom, String prenom) {
    this.noAVS = noAVS;
    this.nom = nom;
    this.prenom = prenom;
  }

  public NoAVS getNoAVS() {
    return noAVS;
  }

  public String getNom() {
    return nom;
  }

  public String getPrenom() {
    return prenom;
  }
}
