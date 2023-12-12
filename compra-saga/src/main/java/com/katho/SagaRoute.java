package com.katho;

import jakarta.inject.Inject;
import org.apache.camel.builder.RouteBuilder;

public class SagaRoute extends RouteBuilder {

    @Inject
    PedidoService pedidoService;

    @Inject
    CreditoService creditoService;

    @Override
    public void configure() throws Exception {

    }
}
