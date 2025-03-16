package unioeste.geral.receitamedica.service;

import org.json.JSONArray;
import org.json.JSONObject;
import unioeste.geral.receitamedica.utils.RespostaInteligenciaArtificial;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UCInteligenciaArtificial {

    private String geminiApiKey;

    public UCInteligenciaArtificial() {
        this.geminiApiKey = "AIzaSyBn4PQZtfo_xQXWncegjCk2HSl0tVKH0l0";
    }

    public static String buildJsonBody(String userMessage) {
        return """
                {
                  "contents": [
                    {
                      "role": "user",
                      "parts": [
                        {
                          "text": "Você é o assistente de um consultório clínico.\\n\
                                    Sua função é identificar a intenção do usuário a partir da mensagem dele, as possíveis intenções são:\\n\\n\
                                    
                                    1. CONSULTAR_PACIENTE_POR_ID: deve retornar a intenção e o id do paciente contido na mensagem. Caso o id não seja informado, então a intenção deve ser ERRO.\\n\\n\
                                    Exemplos de mensagens de usuário para a intenção 1:\\n\
                                    \\"Quero saber informações sobre o paciente com id 1\\".\\n\
                                    \\"Mostre-me informações do paciente com id 59\\".\\n\\n\
                                   
                                    2. CONSULTAR_RECEITA_MEDICA_POR_NUMERO: deve retornar a intenção e o número da receita contido na mensagem. Caso o número da receita não seja informado, então a intenção deve ser ERRO.\\n\\n\
                                    Exemplos de mensagens de usuário para a intenção 2:\\n\
                                    \\"Quero saber os detalhes da receita médica número 200\\".\\n\
                                    \\"Mostre-me os medicamentos da receita número 50\\".\\n\\n\
                                    
                                    3. ERRO\\n\
                                    A intenção ERRO deve ser utilizada caso a intenção identificada não seja nem CONSULTAR_PACIENTE_POR_ID e nem CONSULTAR_RECEITA_MEDICA_POR_NUMERO.\\n\\n\
                                    A mensagem do usuário é a seguinte: \\"%s\\""
                        }
                      ]
                    }
                  ],
                  "generationConfig": {
                    "temperature": 1,
                    "topK": 40,
                    "topP": 0.95,
                    "maxOutputTokens": 8192,
                    "responseMimeType": "application/json",
                    "responseSchema": {
                      "type": "object",
                      "properties": {
                        "intencao": {
                          "type": "string"
                        },
                        "id": {
                          "type": "integer"
                        }
                      },
                      "required": [
                        "intencao"
                      ]
                    }
                  }
                }
                """.formatted(userMessage);
    }


    private JSONObject extrairJsonAninhado(String geminiResponse) {
        JSONObject root = new JSONObject(geminiResponse);

        JSONArray candidates = root.optJSONArray("candidates");
        if (candidates == null) {
            return null;
        }

        JSONObject candidate0 = candidates.optJSONObject(0);
        if (candidate0 == null) {
            return null;
        }

        JSONObject content = candidate0.optJSONObject("content");
        if (content == null) {
            return null;
        }

        JSONArray parts = content.optJSONArray("parts");
        if (parts == null) {
            return null;
        }

        JSONObject part0 = parts.optJSONObject(0);
        if (part0 == null) {
            return null;
        }

        String nestedJsonString = part0.optString("text", null);
        if (nestedJsonString == null) {
            return null;
        }

        return new JSONObject(nestedJsonString);
    }

    public RespostaInteligenciaArtificial generateQuestionsForQuestionnaire(String prompt)
            throws IOException, InterruptedException {

        String requestBody = buildJsonBody(prompt);

        String geminiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key="
                + geminiApiKey;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(geminiUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Resposta da API Gemini: " + response.body());

        JSONObject nestedJson = extrairJsonAninhado(response.body());

        if (nestedJson == null) {
            RespostaInteligenciaArtificial respostaErro = new RespostaInteligenciaArtificial();
            respostaErro.setIntencao("ERRO");
            respostaErro.setId(null);
            return respostaErro;
        }

        String intencao = nestedJson.optString("intencao", "ERRO");
        Integer id = nestedJson.has("id") ? nestedJson.getInt("id") : null;

        return new RespostaInteligenciaArtificial(intencao, id);
    }

    public static void main(String[] args) {
        UCInteligenciaArtificial ucIA = new UCInteligenciaArtificial();
        try {
            RespostaInteligenciaArtificial resultado = ucIA.generateQuestionsForQuestionnaire("mostreme os dados do paciente com o id 4 eu acho?");

            System.out.println("Intenção retornada: " + resultado.getIntencao());
            System.out.println("ID retornado: " + resultado.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
