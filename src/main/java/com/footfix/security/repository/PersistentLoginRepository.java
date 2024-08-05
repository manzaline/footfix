package com.footfix.security.repository;

import com.footfix.domain.PersistentLoginToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PersistentLoginRepository extends JpaRepository<PersistentLoginToken, String> {

  Optional<PersistentLoginToken> findBySeries(final String series);

  List<PersistentLoginToken> findByUsername(final String username);

}
