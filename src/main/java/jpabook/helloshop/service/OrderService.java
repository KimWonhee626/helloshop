package jpabook.helloshop.service;

import jpabook.helloshop.domain.Delivery;
import jpabook.helloshop.domain.Member;
import jpabook.helloshop.domain.Order;
import jpabook.helloshop.domain.OrderItem;
import jpabook.helloshop.domain.item.Item;
import jpabook.helloshop.repository.ItemRepository;
import jpabook.helloshop.repository.MemberRepository;
import jpabook.helloshop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    //==주문==//
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {

        // 엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // 배송정보 설정
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        // 주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        // 주문 저장
        orderRepository.save(order);

        return order.getId();
    }

    //==주문 취소==//
    @Transactional
    public void cancelOrder(Long orderId){

        // 주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);

        // 주문 취소
        order.cancel();
    }

//    //==주문 검색==//
//    public List<Order> searchOrder(OrderSearch orderSearch){
//        return orderRepository.findAll(orderSearch);
//    }

}
