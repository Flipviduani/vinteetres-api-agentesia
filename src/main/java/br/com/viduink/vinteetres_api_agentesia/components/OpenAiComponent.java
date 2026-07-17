package br.com.viduink.vinteetres_api_agentesia.components;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Component
public class OpenAiComponent {

    @Value("${openai.apikey}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    private static final String URL = "https://api.openai.com/v1/chat/completions";

    public String gerarAnalise(String dados) {

        RestClient client = RestClient.create();

        String systemPrompt = """
                Você é um consultor financeiro especializado em finanças pessoais.

                Sua missão é analisar movimentações financeiras de uma pessoa e gerar
                um relatório executivo claro e objetivo.

                As movimentações podem conter:
                - Contas a pagar;
                - Contas a receber;
                - Despesas;
                - Receitas;
                - Datas;
                - Categorias;
                - Valores;
                - Situação (Pago, Recebido, Pendente etc.).

                Sua resposta deve sempre conter as seguintes seções:

                ## 1. Resumo Financeiro
                - Total de receitas
                - Total de despesas
                - Saldo do período
                - Quantidade de lançamentos

                ## 2. Principais Gastos
                - Categorias que mais consomem dinheiro
                - Gastos incomuns
                - Gastos recorrentes

                ## 3. Receitas
                - Principais fontes de receita
                - Frequência
                - Estabilidade

                ## 4. Pontos de Atenção
                Identifique:
                - despesas elevadas
                - excesso de gastos supérfluos
                - contas vencidas
                - contas pendentes
                - concentração excessiva de gastos
                - períodos com fluxo de caixa negativo

                ## 5. Recomendações
                Forneça recomendações práticas para:
                - economizar dinheiro
                - aumentar o saldo
                - reduzir despesas
                - melhorar o fluxo de caixa
                - criar reserva financeira
                - quitar dívidas prioritárias

                ## 6. Conclusão
                Faça uma conclusão objetiva em até 10 linhas.

                Seja extremamente objetivo.

                Utilize bullets sempre que possível.

                Quando houver valores, apresente-os em formato monetário brasileiro (R$).

                Caso alguma informação não esteja disponível, informe isso claramente.
                """;

        Map<String, Object> body = Map.of(
                "model", model,
                "temperature", 0.4,
                "messages", List.of(
                        Map.of(
                                "role", "system",
                                "content", systemPrompt),
                        Map.of(
                                "role", "user",
                                "content", "Analise os seguintes dados financeiros:\n\n" + dados)));

        Map<?, ?> response = client.post()
                .uri(URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);

        List<?> choices = (List<?>) response.get("choices");

        if (choices == null || choices.isEmpty()) {
            return "Não foi possível gerar a análise.";
        }

        Map<?, ?> choice = (Map<?, ?>) choices.get(0);
        Map<?, ?> message = (Map<?, ?>) choice.get("message");

        return message.get("content").toString();
    }
}