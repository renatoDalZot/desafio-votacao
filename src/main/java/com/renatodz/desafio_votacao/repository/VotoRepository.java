package com.renatodz.desafio_votacao.repository;

import com.renatodz.desafio_votacao.domain.model.Voto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VotoRepository extends MongoRepository<Voto, String> {
    List<Voto> findAllByPautaId(String pautaId);
    List<Voto> findByAssociadoCPF(String cpf);

    boolean existsByPautaIdAndAssociadoCPF(String idPauta, String cpf);
}
