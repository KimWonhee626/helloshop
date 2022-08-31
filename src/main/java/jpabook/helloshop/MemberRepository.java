package jpabook.helloshop;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;

    // 멤버 저장
    public long save(Member member){
        em.persist(member);
        return member.getId();
    }

    // 멤버 조회
    public Member find(Long id){
        return em.find(Member.class, id);
    }

}
