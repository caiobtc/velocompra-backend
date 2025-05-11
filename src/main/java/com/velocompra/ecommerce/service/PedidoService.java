package com.velocompra.ecommerce.service;

import com.velocompra.ecommerce.dto.*;
import com.velocompra.ecommerce.model.*;
import com.velocompra.ecommerce.repository.EnderecoEntregaRepository;
import com.velocompra.ecommerce.repository.PedidoRepository;
import com.velocompra.ecommerce.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private EnderecoEntregaRepository enderecoEntregaRepository;

    public Pedido criarPedido(PedidoDTO pedidoDTO, String emailCliente) {
        Cliente cliente = clienteService.getClienteByEmail(emailCliente);
        EnderecoEntrega enderecoEntrega = enderecoEntregaRepository.findById(pedidoDTO.getEnderecoEntregaId())
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado"));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setEnderecoEntrega(enderecoEntrega);
        pedido.setFormaPagamento(pedidoDTO.getFormaPagamento());
        pedido.setFrete(pedidoDTO.getFrete());
        pedido.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);

        BigDecimal valorTotal = BigDecimal.ZERO;

        for (ItemPedidoDTO itemPedidoDTO : pedidoDTO.getProdutos()) {
            Produto produto = produtoRepository.findById(itemPedidoDTO.getProdutoId())
                    .orElseThrow(()-> new RuntimeException("Produto não encontrado"));

            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setPedido(pedido);
            itemPedido.setProduto(produto);
            itemPedido.setQuantidade(itemPedidoDTO.getQuantidade());
            itemPedido.setPrecoUnitario(itemPedidoDTO.getPrecoUnitario());

            pedido.getItens().add(itemPedido);

            valorTotal = valorTotal.add(itemPedidoDTO
                    .getPrecoUnitario()
                    .multiply(BigDecimal.valueOf(itemPedidoDTO.getQuantidade())));

        }

        valorTotal = valorTotal.add(pedidoDTO.getFrete());
        pedido.setValorTotal(valorTotal);

        pedido.setNumeroPedido(gerarNumeroPedido());

        return pedidoRepository.save(pedido);
    }

    private String gerarNumeroPedido() {
        long count = pedidoRepository.count() + 1;
        return String.format("PED%05d", count);
    }

    public List<PedidoResumoDTO> listarPedidosDoCliente(Long clienteId) {
        List<Pedido> pedidos = pedidoRepository.findByClienteId(clienteId);
        return pedidos.stream().map(pedido -> {
            PedidoResumoDTO dto = new PedidoResumoDTO();
            dto.setNumeroPedido(pedido.getNumeroPedido());
            dto.setDataCriacao(pedido.getDataCriacao());
            dto.setValorTotal(pedido.getValorTotal());
            dto.setStatus(pedido.getStatus().name());
            return dto;
        }).collect(Collectors.toList());
    }

    public PedidoDetalhadoDTO buscarDetalhesPedido(String numeroPedido, String emailCliente) {
        Pedido pedido = pedidoRepository.findByNumeroPedido(numeroPedido)
                .orElseThrow(()-> new RuntimeException("Pedido nao encontrado"));
        if (!pedido.getCliente().getEmail().equals(emailCliente)) {
            throw new RuntimeException("Acesso negado ao pedido");
        }

        PedidoDetalhadoDTO pedidoDetalhadoDTO = new PedidoDetalhadoDTO();
        pedidoDetalhadoDTO.setNumeroPedido(pedido.getNumeroPedido());
        pedidoDetalhadoDTO.setDataCriacao(pedido.getDataCriacao());
        pedidoDetalhadoDTO.setValorFrete(pedido.getFrete());
        pedidoDetalhadoDTO.setValorTotal(pedido.getValorTotal());
        pedidoDetalhadoDTO.setFormaPagamento(pedido.getFormaPagamento());
        pedidoDetalhadoDTO.setStatus(pedido.getStatus());

        EnderecoEntrega enderecoEntrega = pedido.getEnderecoEntrega();
        EnderecoDTO enderecoDTO = new EnderecoDTO();
        enderecoDTO.setCep(enderecoEntrega.getCep());
        enderecoDTO.setLogradouro(enderecoEntrega.getLogradouro());
        enderecoDTO.setNumero(enderecoEntrega.getNumero());
        enderecoDTO.setComplemento(enderecoEntrega.getComplemento());
        enderecoDTO.setBairro(enderecoEntrega.getBairro());
        enderecoDTO.setCidade(enderecoEntrega.getCidade());
        enderecoDTO.setUf(enderecoEntrega.getUf());
        pedidoDetalhadoDTO.setEnderecoEntrega(enderecoDTO);

        List<PedidoDetalhadoDTO.ItemPedidoDTO> itens = pedido.getItens().stream().map(item -> {
            PedidoDetalhadoDTO.ItemPedidoDTO itemDTO = new PedidoDetalhadoDTO.ItemPedidoDTO();
            itemDTO.setNomeProduto(item.getProduto().getNome());
            itemDTO.setQuantidade(item.getQuantidade());
            itemDTO.setPrecoUnitario(item.getPrecoUnitario());
            itemDTO.setPrecoTotal(item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())));
            itemDTO.setImagemProduto(item.getProduto().getImagemPadrao());
            return itemDTO;
        }).toList();

        pedidoDetalhadoDTO.setItens(itens);
        return pedidoDetalhadoDTO;
    }
}
