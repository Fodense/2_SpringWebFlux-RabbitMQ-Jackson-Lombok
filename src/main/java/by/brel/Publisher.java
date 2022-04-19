package by.brel;

import by.brel.config.AppConfig;
import by.brel.dto.Balance;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

public class Publisher {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        Communication communication = context.getBean("communication", Communication.class);
        Channel channel = context.getBean("channel", Channel.class);
        Queue queue = context.getBean("queue", Queue.class);

        try {
            do {
                Thread.sleep(5000);

                List<Balance> balances = communication.getBalancesMoreNumber();

                channel.queueDeclare(
                        queue.getActualName(),
                        true,
                        false,
                        false,
                        null
                );

                channel.basicPublish(
                        "",
                        queue.getActualName(),
                        null,
                        toBalanceByte(balances)
                );

                System.out.print("Ids sent balances -> ");
                balances.forEach(
                        balance -> System.out.print(balance.getIdBalance() + " ")
                );
                System.out.println();

            } while (true);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static byte[] toBalanceByte(List<Balance> balances) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(outputStream);
        out.writeObject(balances);

        return outputStream.toByteArray();
    }
}
