package by.brel.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Configuration
@ComponentScan("by.brel")
@PropertySource("classpath:/config.properties")
public class AppConfig {

    @Value("${app.url}")
    private String URL;

    @Bean
    public WebClient webClientWithTimeout() {
        return WebClient.builder()
                .baseUrl(URL)
                .build();
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        return factory;
    }

    @Bean
    public Connection connection() throws IOException, TimeoutException {
        return connectionFactory().newConnection();
    }

    @Bean
    public Channel channel() throws IOException, TimeoutException {
        return connection().createChannel();
    }

    @Bean
    public Queue queue() {
        return new Queue("queue1");
    }
}
