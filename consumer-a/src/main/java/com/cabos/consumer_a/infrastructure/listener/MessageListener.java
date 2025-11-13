package com.cabos.consumer_a.infrastructure.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MessageListener {

    @KafkaListener(topics = "mensagens", groupId = "grupo-a")
    public void ouvirMensagem(String mensagem) {
        System.out.println("Consumer A recebeu: " + mensagem);
    }
}
