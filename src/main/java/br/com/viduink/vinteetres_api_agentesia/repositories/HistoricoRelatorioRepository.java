package br.com.viduink.vinteetres_api_agentesia.repositories;

import br.com.viduink.vinteetres_api_agentesia.entities.HistoricoRelatorio;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoricoRelatorioRepository extends MongoRepository <HistoricoRelatorio, String> {

}
