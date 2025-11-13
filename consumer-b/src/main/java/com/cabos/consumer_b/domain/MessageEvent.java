package com.cabos.consumer_b.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageEvent {
    private String mensagem;
}
