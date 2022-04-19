package by.brel;

import by.brel.config.AppConfig;
import by.brel.dto.Balance;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

public class Consumer {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        Channel channel = context.getBean("channel", Channel.class);
        Queue queue = context.getBean("queue", Queue.class);

        try {
            do {
                Thread.sleep(5000);

                channel.queueDeclare(
                        queue.getActualName(),
                        true,
                        false,
                        false,
                        null
                );

                DeliverCallback deliverCallback = (s, delivery) -> {
                    try {
                        List<Balance> balanceList = readByteArray(delivery.getBody());

                        System.out.print("Received balances IDs -> ");
                        balanceList.forEach(
                                balance -> System.out.print(balance.getIdBalance() + " ")
                        );
                        System.out.println();

                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                };

                channel.basicConsume(
                        queue.getActualName(),
                        true,
                        deliverCallback,
                        consumerTag -> {}
                );

            } while (true);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static List readByteArray(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream in = new ObjectInputStream(inputStream);
        List balances = (List) in.readObject();

        return balances;
    }
}
