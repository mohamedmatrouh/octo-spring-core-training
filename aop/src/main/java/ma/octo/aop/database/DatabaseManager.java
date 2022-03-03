package ma.octo.aop.database;

import ma.octo.aop.entity.Language;
import ma.octo.aop.util.Logger;
import ma.octo.aop.util.impl.LoggerImpl;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.h2.jdbcx.JdbcDataSource;

import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class DatabaseManager implements InitializingBean, DisposableBean {

    private final String jdbcURL;
    private final String jdbcUsername;
    private final String jdbcPassword;
    private Connection connection;
    private final Logger logger;

    @Value("classpath:data.sql")
    private Resource sqlResource;

    public DatabaseManager(Environment environment) {
        jdbcURL = environment.getProperty("app.db.url");
        jdbcUsername = environment.getProperty("app.db.username");
        jdbcPassword = environment.getProperty("app.db.password");
        this.logger = new LoggerImpl(DatabaseManager.class, environment);
    }

    @Override
    public void afterPropertiesSet() {

        JdbcDataSource ds = createH2Datasource();

        try {
            connection = ds.getConnection();
            logger.info(String.format("Connected to database {%s} successfully", jdbcURL));

            initDatabase();
            logger.info(String.format("database {%s} has been filled successfully", jdbcURL));

        } catch (SQLException | IOException e) {
            logger.error(String.format("can't connect to database {%s} because: %s", jdbcURL, e.getMessage()));
            e.printStackTrace();
        }

    }

    private JdbcDataSource createH2Datasource(){
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL(jdbcURL);
        ds.setUser(jdbcUsername);
        ds.setPassword(jdbcPassword);
        return ds;
    }

    private void initDatabase() throws SQLException, IOException {
        var sqlFile = sqlResource.getFile();
        var sqlQuery = Files.readString(sqlFile.toPath());

        var stm = connection.createStatement();
        stm.execute(sqlQuery);
    }


    public Optional<Language> getLanguageById(String id) {

        PreparedStatement preStm = null;
        Language language = null;

        try {
            preStm = connection.prepareStatement("select * from language where id = ?");
            preStm.setString(1, id);
            var resultSet = preStm.executeQuery();
            if (resultSet.next()){
                 language = new Language(
                        resultSet.getString("id"),
                        resultSet.getString("name"),
                        resultSet.getString("author"),
                        resultSet.getString("fileExtension")
                        );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (preStm != null){
                try {
                    preStm.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return language != null ? Optional.of(language) : Optional.empty();
    }

    public Optional<Language> getLanguageByExtension(String extension) {

        PreparedStatement preStm = null;
        Language language = null;

        try {
            preStm = connection.prepareStatement("select * from language where fileExtension = ?");
            preStm.setString(1, extension);
            var resultSet = preStm.executeQuery();
            if (resultSet.next()){
                language = new Language(
                        resultSet.getString("id"),
                        resultSet.getString("name"),
                        resultSet.getString("author"),
                        resultSet.getString("fileExtension")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (preStm != null){
                try {
                    preStm.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return language != null ? Optional.of(language) : Optional.empty();
    }

    public List<Language> findAllLanguages() {
        List<Language> languages = new ArrayList<>();

        PreparedStatement preStm = null;

        try {
            preStm = connection.prepareStatement("select * from language");
            var resultSet = preStm.executeQuery();
            while (resultSet.next()){
                languages.add( new Language(
                        resultSet.getString("id"),
                        resultSet.getString("name"),
                        resultSet.getString("author"),
                        resultSet.getString("fileExtension")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (preStm != null){
                try {
                    preStm.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return languages;
    }

    @Override
    public void destroy() throws Exception {
        if (connection != null){
            connection.close();
        }
    }
}
