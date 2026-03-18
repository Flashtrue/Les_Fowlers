package ch.hearc.cafheg.infrastructure.persistence;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

public class Database {
  /** Pool de connections JDBC */
  private static DataSource dataSource;

  /** Connection JDBC active par utilisateur/thread (ThreadLocal) */
  private static final ThreadLocal<Connection> connection = new ThreadLocal<>();

  private static final Logger logger = LoggerFactory.getLogger(Database.class);

  /**
   * Retourne la transaction active ou throw une Exception si pas de transaction
   * active.
   * @return Connection JDBC active
   */
  static Connection activeJDBCConnection() {
    if (connection.get() == null) {
      throw new RuntimeException("Pas de connection JDBC active");
    }
    return connection.get();
  }

  /**
   * Exécution d'une fonction dans une transaction.
   * @param inTransaction La fonction a exécuter au travers d'une transaction
   * @param <T> Le type du retour de la fonction
   * @return Le résultat de l'exécution de la fonction
   */
  public static <T> T inTransaction(Supplier<T> inTransaction) {
    logger.debug("inTransaction#start");
    try {
      logger.debug("inTransaction#getConnection");
      connection.set(dataSource.getConnection());
      return inTransaction.get();
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      try {
        logger.debug("inTransaction#closeConnection");
        connection.get().close();
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
      logger.debug("inTransaction#end");
      connection.remove();
    }
  }

  DataSource dataSource() {
    return dataSource;
  }

  /**
   * Initialisation du pool de connections.
   */
  public void start(String jdbcUrl, String username, String password) {
    logger.debug("Initializing datasource");
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(jdbcUrl);
    config.setUsername(username);
    config.setPassword(password);
    config.setMaximumPoolSize(20);
    config.setDriverClassName("org.postgresql.Driver");
    dataSource = new HikariDataSource(config);
    logger.debug("Datasource initialized");
  }
}
