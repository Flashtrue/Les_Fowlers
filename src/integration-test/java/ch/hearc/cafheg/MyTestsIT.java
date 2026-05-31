package ch.hearc.cafheg;

import ch.hearc.cafheg.domain.allocations.AllocationService;
import ch.hearc.cafheg.domain.allocations.Allocataire;
import ch.hearc.cafheg.infrastructure.persistence.AllocataireMapper;
import ch.hearc.cafheg.infrastructure.persistence.AllocationMapper;
import ch.hearc.cafheg.infrastructure.persistence.Database;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Statement;

import static ch.hearc.cafheg.infrastructure.persistence.Database.inTransaction;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MyTestsIT {

    private static DataSource dataSource;
    private AllocationService allocationService;

    @BeforeAll
    static void setUpDatabase() throws Exception {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false");
        config.setUsername("sa");
        config.setPassword("");
        config.setDriverClassName("org.h2.Driver");
        config.setMaximumPoolSize(5);
        dataSource = new HikariDataSource(config);

        // Injection du DataSource H2 dans Database via reflection
        Field field = Database.class.getDeclaredField("dataSource");
        field.setAccessible(true);
        field.set(null, dataSource);

        // Création du schéma minimal (ALLOCATAIRES + VERSEMENTS)
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS ALLOCATAIRES (" +
                "  NUMERO BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "  NO_AVS VARCHAR(200) UNIQUE," +
                "  PRENOM VARCHAR(200) NOT NULL," +
                "  NOM VARCHAR(200) NOT NULL" +
                ")"
            );
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS VERSEMENTS (" +
                "  NUMERO BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL," +
                "  FK_ALLOCATAIRES BIGINT NOT NULL," +
                "  DATE_VERSEMENT DATE NOT NULL," +
                "  MOIS_VERSEMENT DATE NOT NULL" +
                ")"
            );
            stmt.execute(
                "ALTER TABLE VERSEMENTS ADD CONSTRAINT IF NOT EXISTS FK_VERSEMENTS1 " +
                "FOREIGN KEY (FK_ALLOCATAIRES) REFERENCES ALLOCATAIRES(NUMERO)"
            );
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        allocationService = new AllocationService(
            new AllocataireMapper(),
            Mockito.mock(AllocationMapper.class)
        );

        // Peuplement de la base via DBUnit
        try (Connection conn = dataSource.getConnection()) {
            IDatabaseConnection dbConnection = new DatabaseConnection(conn);
            IDataSet dataSet = new FlatXmlDataSetBuilder()
                .build(getClass().getResourceAsStream("/dataset.xml"));
            DatabaseOperation.CLEAN_INSERT.execute(dbConnection, dataSet);
        }
    }

    // Partie 2 – test simple toujours passant
    @Test
    void simpleTest_AlwaysPass() {
        assertThat(1).isEqualTo(1);
    }

    // Partie 4 – Suppression d'un allocataire sans versement (doit réussir)
    @Test
    void deleteAllocataire_SansVersement_DoitRetournerTrue() {
        boolean result = inTransaction(() -> allocationService.deleteAllocataire(2L));
        assertThat(result).isTrue();
    }

    // Partie 4 – Suppression d'un allocataire avec versement (doit échouer)
    @Test
    void deleteAllocataire_AvecVersement_DoitLeverUneException() {
        assertThatThrownBy(() -> inTransaction(() -> allocationService.deleteAllocataire(1L)))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("versements");
    }

    // Partie 4 – Modification nom et prénom différents (doit réussir)
    @Test
    void updateAllocataire_NomPrenomDifferents_DoitRetournerAllocataireModifie() {
        Allocataire updated = inTransaction(() ->
            allocationService.updateAllocataire(1L, "NouveauNom", "NouveauPrenom"));

        assertThat(updated).isNotNull();
        assertThat(updated.getNom()).isEqualTo("NouveauNom");
        assertThat(updated.getPrenom()).isEqualTo("NouveauPrenom");
        // Le numéro AVS ne doit pas changer
        assertThat(updated.getNoAVS().getValue()).isEqualTo("756.1558.5343.97");
    }

    // Partie 4 – Modification avec même nom et prénom (doit échouer)
    @Test
    void updateAllocataire_MemeNomPrenom_DoitLeverUneException() {
        // Allocataire 1 : NOM=Deguzman, PRENOM=Kendrick
        assertThatThrownBy(() -> inTransaction(() ->
            allocationService.updateAllocataire(1L, "Deguzman", "Kendrick")))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("aucune modification");
    }
}
