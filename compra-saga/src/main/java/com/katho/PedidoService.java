package com.katho;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Header;

import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class PedidoService {

    private Set<Long> pedidos = new HashSet<>();

    public void newPedido(@Header("id") Long id) {
        pedidos.add(id);
    }

    public void cancelPedido(Long id) {
        pedidos.remove(id);
    }
}
