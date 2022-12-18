package com.webclient.demo;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.HashMap;

@RestController
public class BasicController {
    WebClient webClient = WebClient.builder()
            .baseUrl("https://countriesnow.space/api/v0.1/countries/population/cities")
            .exchangeStrategies(
                    ExchangeStrategies.builder().codecs(codecs->codecs.defaultCodecs().maxInMemorySize(10000*1024)).build()
            )
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();
    private ClientResponse validateResponse(ClientResponse clientResponse){
        if (clientResponse.statusCode().equals(HttpStatus.OK)){
            return clientResponse;
        } else if (clientResponse.statusCode().is4xxClientError()){
            System.out.println("Client Error");
            return null;
        } else {
            System.out.println("Fatal Error");
            return null;
        }
    }
    @GetMapping(value = "/mono")
    public Mono<Object> MonoRoute(){
        return webClient.get().uri("/").exchangeToMono(clientResponse -> {
            return validateResponse(clientResponse).bodyToMono(Object.class);
        });
    }

    @GetMapping(value = "/flux")
    public Flux<Object> FluxRoute(){
        HashMap<String, String> someBodyValues = new HashMap<>();
        someBodyValues.put("a", "1");
        someBodyValues.put("b", "2");
        return webClient.post().bodyValue(someBodyValues).exchangeToFlux(clientResponse -> {
            return validateResponse(clientResponse).bodyToFlux(Object.class);
        });
    }
}
