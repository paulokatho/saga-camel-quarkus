package com.katho;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.camel.CamelContext;

@Path("compra-camel")
public class CompraCamelResource {

    @Inject
    CamelContext context;

    @Path("teste-saga")
    @Produces(MediaType.TEXT_PLAIN)
    @GET
    public Response saga() {
        Long id = 0L;

        comprar(++id, 20);
        comprar(++id, 30);
        comprar(++id, 30);
        comprar(++id, 25);

        return Response.ok().build();
    }

    //Chamando nossa Saga
    private void comprar(Long id, int valor) {
        context.createFluentProducerTemplate()
                .to("direct:saga")
                .withHeader("id", id)
                .withHeader("pedidoId", id)
                .withHeader("valor", valor)
                .request();

    }
}
