package by.brel;

import by.brel.dto.Balance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class Communication {
    @Autowired
    private WebClient webClient;

    @Value("${app.path}")
    private String URL;

    @Value("${balance.min}")
    private int minBalance;

    public List<Balance> getAllBalances() {
        return webClient
                .get()
                .uri(URL).accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Balance.class)
                .collectList()
                .block();
    }

    public List<Balance> getBalancesMoreNumber() {
        return getAllBalances().stream()
                .filter(balance -> balance.getBalance() > minBalance)
                .collect(Collectors.toList());
    }
}
