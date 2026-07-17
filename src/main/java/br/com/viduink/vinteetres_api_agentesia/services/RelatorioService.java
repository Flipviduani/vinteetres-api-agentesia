package br.com.viduink.vinteetres_api_agentesia.services;

import br.com.viduink.vinteetres_api_agentesia.components.OpenAiComponent;
import br.com.viduink.vinteetres_api_agentesia.dtos.RelatorioRequest;
import br.com.viduink.vinteetres_api_agentesia.dtos.RelatorioResponse;
import br.com.viduink.vinteetres_api_agentesia.entities.HistoricoRelatorio;
import br.com.viduink.vinteetres_api_agentesia.repositories.HistoricoRelatorioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RelatorioService {

    @Autowired
    private HistoricoRelatorioRepository historicoRelatorioRepository;

    @Autowired
    private OpenAiComponent  openAiComponent;


    /* Metodo para implementar o fluxo de:
    1) Enviar os dados para a IA gerar a análise financeira
    2) Salvar a análise gerada no banco de dados do MongoDB
    3) Enviar a análise para o e-mail do usuário
     */

    public RelatorioResponse gerarRelatorio(RelatorioRequest request) {
        //Gerar análise financeira com a OpenAI
        var analise = openAiComponent.gerarAnalise(request.dadosAnalise());

        //Criando um objeto de entidade para salvar o histórico no banco de dados
        var historico = new HistoricoRelatorio();
        historico.setUsuario(request.usuario());
        historico.setDadosAnalise(request.dadosAnalise());
        historico.setResultadoAnalise(analise);
        historico.setDataHora(LocalDateTime.now());

        //Salvar no banco de dados:
        historicoRelatorioRepository.save(historico);

        //Retornando o resultado
        return new RelatorioResponse(analise);
    }
}
