package jpabook.helloshop.service;

import jpabook.helloshop.domain.Address;
import jpabook.helloshop.domain.Member;
import jpabook.helloshop.domain.Order;
import jpabook.helloshop.domain.OrderStatus;
import jpabook.helloshop.domain.item.Book;
import jpabook.helloshop.domain.item.Item;
import jpabook.helloshop.exception.NotEnoughStockException;
import jpabook.helloshop.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;


import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
class OrderServiceTest {

    @Autowired EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception {
        //given
        Member member = createMember("회원1");

        Book book = createBook("시골 JPA", 10000, 10);

        int orderCount = 3;

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        Assertions.assertEquals(OrderStatus.ORDER, getOrder.getStatus(), "상품 주문시 상태는 ORDER");
        Assertions.assertEquals(1, getOrder.getOrderItems().size(), "주문한 상품 종류 수가 정확해야 한다.");
        Assertions.assertEquals(10000*orderCount, getOrder.getTotalPrice(), "주문 가격은 가격*수량 이다.");
        Assertions.assertEquals(7, book.getStockQuantity(), "주문 수량만큼 재고가 줄어야 한다.");
    }


    @Test
    public void 주문취소() throws Exception {
        //given
        Member member = createMember("회원1");
        Book item = createBook("JPA", 10000, 10);

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);


        // System.out.println("=================== 주문취소 전 재고 : " + item.getStockQuantity());
        //when
        orderService.cancelOrder(orderId);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        Assertions.assertEquals(OrderStatus.CANCEL, getOrder.getStatus(),"주문 취소시 상태는 CALCEL 이다.");
        Assertions.assertEquals(10, item.getStockQuantity(),"주문이 취소된 상품은 재고가 다시 증가해야 한다.");

    }

    @Test
    public void 상품주문_재고수량초과() throws Exception {
        //given
        Member member = createMember("회원1");
        Item item = createBook("JPA", 20000, 10);

        int orderCount = 12;
        //when

        //then
        assertThrows("재고 수량 부족 예외가 발생해야 한다.",
                NotEnoughStockException.class,
                () -> orderService.order(member.getId(), item.getId(), orderCount));

    }



    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember(String name) {
        Member member = new Member();
        member.setName(name);
        member.setAddress(new Address("서울", "강가", "123-123"));
        em.persist(member);
        return member;
    }

}
