package com.renatodz.desafio_votacao.repository;


import com.renatodz.desafio_votacao.domain.model.Pauta;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PautaRepository extends MongoRepository<Pauta, String> {
}
