package com.cabos.consumer_b.infrastructure.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MessageListener {

    @KafkaListener(topics = "mensagens", groupId = "grupo-b")
    public void ouvirMensagem(String mensagem) {
        System.out.println("Consumer B recebeu: " + mensagem);
    }
}
