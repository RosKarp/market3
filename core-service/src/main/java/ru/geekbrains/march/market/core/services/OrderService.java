package ru.geekbrains.march.market.core.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.geekbrains.march.market.api.CartItemDto;
import ru.geekbrains.march.market.core.entities.Order;
import ru.geekbrains.march.market.core.entities.OrderItem;
import ru.geekbrains.march.market.core.entities.Product;
import ru.geekbrains.march.market.core.exceptions.ResourceNotFoundException;
import ru.geekbrains.march.market.core.integrations.CartServiceIntegration;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final CartServiceIntegration cartServiceIntegration;
    private final UserService userService;
    private final ProductService productService;

    public Order createOrder(Principal principal) {
        Order order = new Order();
        order.setUser(userService.findByUsername(principal.getName()).orElseThrow(() ->
                new ResourceNotFoundException("Нельзя создать заказ неавторизованному пользователю.")));
        order.setTotalPrice(cartServiceIntegration.getCurrentCart().getTotalPrice());
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItemDto c: cartServiceIntegration.getCurrentCart().getItems())
              {
                  Product p = productService.findById(c.getProductId()).orElseThrow(() -> new ResourceNotFoundException("Продукт не найден в базе."));
            orderItems.add(new OrderItem(order, p, c.getPricePerProduct(), c.getPrice(), c.getQuantity()));
        }
        order.setItems(orderItems);
        return order;
    }
}
