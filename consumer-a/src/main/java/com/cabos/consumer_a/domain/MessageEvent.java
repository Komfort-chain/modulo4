package com.cabos.consumer_a.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageEvent {
    private String mensagem;
}
