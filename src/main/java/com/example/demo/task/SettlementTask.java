package com.example.demo.task;

import com.example.demo.model.Merchant;
import com.example.demo.model.Order;
import com.example.demo.model.OrderStatus;
import com.example.demo.model.Product;
import com.example.demo.repository.MerchantRepository;
import com.example.demo.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SettlementTask {
    @Autowired
    private OrderRepository orderRepository;

    @Scheduled(cron = "0 0 0 * * ?") // 每天午夜执行
    public void dailySettlement() {
        // 获取昨天所有完成的订单
        Instant startOfYesterday = LocalDate.now().minusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        Instant startOfToday = LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        List<Order> completedOrders = orderRepository.findByStatusAndCreatedAtBetween(
                OrderStatus.COMPLETED,
                Timestamp.from(startOfYesterday),
                Timestamp.from(startOfToday)
        );

        // 收集商家信息
        Map<Long, Merchant> merchantById = completedOrders.stream()
                .map(Order::getProduct)
                .map(Product::getMerchant)
                .collect(Collectors.toMap(
                        Merchant::getId,
                        merchant -> merchant,
                        (oldMerchant, newMerchant) -> newMerchant
                ));

        // 按商家分组计算销售额
        Map<Long, BigDecimal> merchantSales = completedOrders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getProduct().getMerchant().getId(),
                        Collectors.reducing(BigDecimal.ZERO, Order::getTotalAmount, BigDecimal::add)
                ));

        for (Entry<Long, BigDecimal> entry : merchantSales.entrySet()) {
            Long merchantId = entry.getKey();
            BigDecimal totalAmount = entry.getValue();

            Merchant merchant = merchantById.get(merchantId);
            if (merchant == null) {
                log.error("Merchant for {} doesn't exist", merchantId);
                continue;
            }
            BigDecimal balanceOfPreviousDay = merchant.getBalanceOfPreviousDay();
            if (balanceOfPreviousDay == null) {
                balanceOfPreviousDay = BigDecimal.ZERO;
            }
            if (balanceOfPreviousDay.add(totalAmount).equals(merchant.getBalance())) {
                log.warn("The sales amount of merchant {} doesn't match with the balance, balance of previous day: " +
                                "{}, total sales amount: {}, current balance: {}",
                        merchantId, balanceOfPreviousDay, totalAmount, merchant.getBalance());
            }
        }
    }
}
