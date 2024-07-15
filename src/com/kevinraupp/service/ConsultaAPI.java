package com.kevinraupp.service;

import com.kevinraupp.model.ConversorMonetario;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ConsultaAPI{
    public ConversorMonetario consultaMoeda(String moedaInicial, String moedaFinal) {
        String key = "abfa0694537466a6cda8da43";
        String url = "https://v6.exchangerate-api.com/v6/" + key + "/pair/" + moedaInicial + "/" + moedaFinal;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

        try{
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200){
                throw new RuntimeException("Falha na requisição para a API com o código de status: " +response.statusCode());
            }

            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();

            String baseCode = jsonObject.get("base_code").getAsString();
            String targetCode = jsonObject.get("target_code").getAsString();
            double conversionRate = jsonObject.get("conversion_rate").getAsDouble();

            return new ConversorMonetario(baseCode, targetCode, String.valueOf(conversionRate));
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException("Error - A moeda informada não existe, verifique! ", e);
        } catch (JsonSyntaxException e) {
            throw new RuntimeException("Erro ao analisar a resposta da API. Verifique o formato do JSON! ", e);
        }
    }
}