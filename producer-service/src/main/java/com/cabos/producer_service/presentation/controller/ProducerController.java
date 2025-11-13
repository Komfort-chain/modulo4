package com.cabos.producer_service.presentation.controller;

import com.cabos.producer_service.application.service.KafkaProducerService;
import com.cabos.producer_service.domain.MessagePayload;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/producer")
public class ProducerController {

    private final KafkaProducerService service;

    public ProducerController(KafkaProducerService service) {
        this.service = service;
    }

    @PostMapping("/enviar")
    public String enviarMensagem(@RequestBody MessagePayload payload) {
        service.enviarMensagem("mensagens", payload.getMensagem());
        return "Mensagem enviada!";
    }
}
