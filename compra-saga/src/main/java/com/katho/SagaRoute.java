package com.katho;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.SagaPropagation;
import org.apache.camel.saga.CamelSagaService;
import org.apache.camel.saga.InMemorySagaService;

@ApplicationScoped
public class SagaRoute extends RouteBuilder {

    @Inject
    PedidoService pedidoService;
    @Inject
    CreditoService creditoService;

    @Override
    public void configure() throws Exception {

        CamelSagaService sagaService = new InMemorySagaService();
        getContext().addService(sagaService);

        //Saga
        from("direct:saga").saga().propagation(SagaPropagation.REQUIRES_NEW).log("Iniciando Saga da Transação")
                .to("direct:newPedido").log("Pedido ${header.id} criado. Saga ${body}.")
                .to("direct:newPedidoValor").log("Credito do pedido ${header.id} no valor de BRL ${header.id} reservado para a saga ${body}")
                .to("direct:finaliza").log("Saga realizada com sucesso!");

        //Pedido Service
        from("direct:newPedido").saga().propagation(SagaPropagation.MANDATORY)
                .compensation("direct:cancelPedido")
                .transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
                .bean(pedidoService, "newPedido").log("Criando novo pedido com id ${header.id}");
        from("direct:cancelPedido")
                .transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
                .bean(pedidoService,"cancelPedido").log("Pedido ${body} compensado");

        //Credito Service
        from("direct:newPedidoValor").saga().propagation(SagaPropagation.MANDATORY)
                .compensation("direct:cancelPedidoValor")
                .transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
                .bean(creditoService,"newPedidoValor").log("Reservando o Crédito");
        from("direct:cancelPedidoValor")
                .transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
                .bean(creditoService,"cancelPedidoValor").log("Credito compensado para a Saga ${body}");

        //Finaliza
        from("direct:finaliza").saga().propagation(SagaPropagation.MANDATORY)
                .choice()
                .end();
    }
}
