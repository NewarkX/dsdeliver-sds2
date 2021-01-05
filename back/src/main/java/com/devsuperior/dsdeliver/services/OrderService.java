package com.devsuperior.dsdeliver.services;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dsdeliver.dto.OrderDTO;
import com.devsuperior.dsdeliver.dto.ProductDTO;
import com.devsuperior.dsdeliver.entities.Order;
import com.devsuperior.dsdeliver.entities.OrderStatus;
import com.devsuperior.dsdeliver.entities.Product;
import com.devsuperior.dsdeliver.repositories.OrderRepository;
import com.devsuperior.dsdeliver.repositories.ProductRepository;


@Service
public class OrderService {
	
	@Autowired
	private OrderRepository or;
	
	@Autowired
	private ProductRepository pr;
	
	@Transactional(readOnly = true)
	public List<OrderDTO> findAll(){
		List<Order> list = or.findOrdersWithProducts();
		return list.stream().map(x -> new OrderDTO(x)).collect(Collectors.toList());
	}
	
	@Transactional
	public OrderDTO insert(OrderDTO dto){ /* Associacao de produtos com pedidos */
		Order order = new Order(null,dto.getAddress(),dto.getLatitude(),dto.getLongitude(),Instant.now(),OrderStatus.PENDING);
		for (ProductDTO p : dto.getProducts()) {
			Product product = pr.getOne(p.getId());
			order.getProducts().add(product);
		}
		order = or.save(order);
		return new OrderDTO(order);
	}
	
	@Transactional
	public OrderDTO setDelivered(Long id){
		Order order = or.getOne(id);
		order.setStatus(OrderStatus.DELIVERED);
		order = or.save(order);
		return new OrderDTO(order);
	}
	
}	
