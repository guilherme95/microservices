package br.com.guilherme.repository;

import br.com.guilherme.model.Cambio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CambioRepository extends JpaRepository<Cambio, Long> {
    Cambio findByFromAndTo(String from, String to);
}
