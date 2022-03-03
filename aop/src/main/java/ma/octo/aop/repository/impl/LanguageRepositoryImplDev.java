package ma.octo.aop.repository.impl;

import ma.octo.aop.database.DatabaseManager;
import ma.octo.aop.entity.Language;
import ma.octo.aop.repository.LanguageRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Profile("dev")
public class LanguageRepositoryImplDev implements LanguageRepository {

  private final DatabaseManager databaseManager;

  public LanguageRepositoryImplDev(DatabaseManager databaseManager) {
    this.databaseManager = databaseManager;
  }

  @Override
  public Optional<Language> findByExtension(final String extension) {
      return databaseManager.getLanguageByExtension(extension);
  }

  @Override
  public Optional<Language> findById(final String id) {
    return databaseManager.getLanguageById(id);
  }

  @Override
  public List<Language> findAll() {
    return databaseManager.findAllLanguages();
  }


}
