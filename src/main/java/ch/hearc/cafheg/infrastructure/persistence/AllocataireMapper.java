package ch.hearc.cafheg.infrastructure.persistence;

import ch.hearc.cafheg.domain.allocations.Allocataire;
import ch.hearc.cafheg.domain.allocations.NoAVS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AllocataireMapper extends Mapper {

  private static final String QUERY_FIND_ALL = "SELECT NOM,PRENOM,NO_AVS FROM ALLOCATAIRES";
  private static final String QUERY_FIND_WHERE_NOM_LIKE = "SELECT NOM,PRENOM,NO_AVS FROM ALLOCATAIRES WHERE NOM LIKE ?";
  private static final String QUERY_FIND_WHERE_NUMERO = "SELECT NO_AVS, NOM, PRENOM FROM ALLOCATAIRES WHERE NUMERO=?";
  private static final String QUERY_EXISTS_VERSEMENT_FOR_ALLOCATAIRE = "SELECT EXISTS(SELECT 1 FROM VERSEMENTS WHERE FK_ALLOCATAIRES = ?)";
  private static final String QUERY_DELETE_BY_NUMERO = "DELETE FROM ALLOCATAIRES WHERE NUMERO=?";
  private static final String QUERY_UPDATE_BY_NUMERO = "UPDATE ALLOCATAIRES SET NOM=?, PRENOM=? WHERE NUMERO=?";
  private static final Logger logger = LoggerFactory.getLogger(AllocataireMapper.class);

  public List<Allocataire> findAll(String likeNom) {
    logger.debug("findAll() " + likeNom);
    Connection connection = activeJDBCConnection();
    try {
      PreparedStatement preparedStatement;
      if (likeNom == null) {
        logger.debug("SQL: " + QUERY_FIND_ALL);
        preparedStatement = connection
            .prepareStatement(QUERY_FIND_ALL);
      } else {

        logger.debug("SQL: " + QUERY_FIND_WHERE_NOM_LIKE);
        preparedStatement = connection
            .prepareStatement(QUERY_FIND_WHERE_NOM_LIKE);
        preparedStatement.setString(1, likeNom + "%");
      }
      logger.debug("Allocation d'un nouveau tableau");
      List<Allocataire> allocataires = new ArrayList<>();

      logger.debug("Exécution de la requête");
      try (ResultSet resultSet = preparedStatement.executeQuery()) {

        logger.debug("Allocataire mapping");
        while (resultSet.next()) {
          logger.debug("ResultSet#next");
          allocataires
              .add(new Allocataire(new NoAVS(resultSet.getString(3)), resultSet.getString(2),
                  resultSet.getString(1)));
        }
      }
      logger.debug("Allocataires trouvés " + allocataires.size());
      return allocataires;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public Allocataire findById(long id) {
    logger.debug("findById() " + id);
    Connection connection = activeJDBCConnection();
    try {
      logger.debug("SQL:" + QUERY_FIND_WHERE_NUMERO);
      PreparedStatement preparedStatement = connection.prepareStatement(QUERY_FIND_WHERE_NUMERO);
      preparedStatement.setLong(1, id);
      ResultSet resultSet = preparedStatement.executeQuery();
      logger.debug("ResultSet#next");
      resultSet.next();
      logger.debug("Allocataire mapping");
      return new Allocataire(new NoAVS(resultSet.getString(1)),
          resultSet.getString(2), resultSet.getString(3));
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean hasVersements(long allocataireId) {
    logger.debug("hasVersements() {}", allocataireId);
    Connection connection = activeJDBCConnection();
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(QUERY_EXISTS_VERSEMENT_FOR_ALLOCATAIRE);
      preparedStatement.setLong(1, allocataireId);
      ResultSet resultSet = preparedStatement.executeQuery();
      if (!resultSet.next()) {
        return false;
      }
      return resultSet.getBoolean(1);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean deleteById(long id) {
    logger.debug("deleteById() {}", id);
    Connection connection = activeJDBCConnection();
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(QUERY_DELETE_BY_NUMERO);
      preparedStatement.setLong(1, id);
      return preparedStatement.executeUpdate() > 0;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean updateById(long id, String nom, String prenom) {
    logger.debug("updateById() {}", id);
    Connection connection = activeJDBCConnection();
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(QUERY_UPDATE_BY_NUMERO);
      preparedStatement.setString(1, nom);
      preparedStatement.setString(2, prenom);
      preparedStatement.setLong(3, id);
      return preparedStatement.executeUpdate() > 0;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
