package com.rodrigo.springajax.repository;

import com.rodrigo.springajax.domain.Promocao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromocaoRepository extends JpaRepository<Promocao, Long> {
}
