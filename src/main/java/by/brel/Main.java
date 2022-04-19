package by.brel;

import by.brel.config.AppConfig;
import by.brel.dto.Balance;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.*;
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
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

//Publish
                channel.basicPublish(
                        "",
                        queue.getActualName(),
                        null,
                        toBalanceByte(balances)
                );

                System.out.print("Sent balance IDs -> ");
                balances.forEach(
                        balance -> System.out.print(balance.getIdBalance() + " ")
                );
                System.out.println();

//Consume
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] toBalanceByte(List<Balance> balances) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(outputStream);
        out.writeObject(balances);

        return outputStream.toByteArray();
    }

    public static List readByteArray(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream in = new ObjectInputStream(inputStream);
        List balances = (List) in.readObject();

        return balances;
    }
}
