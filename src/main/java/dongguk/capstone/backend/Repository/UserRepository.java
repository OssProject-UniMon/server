package dongguk.capstone.backend.Repository;

import dongguk.capstone.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Long> { // JpaRepository의 제너릭은 <Domain 타입, ID 타입> 이다.
    // save는 JpaRepository에 존재한다(정확히는 부모 인터페이스에 존재)
    boolean existsByEmail(String email);

    List<User> findByEmail(String email);
}


//package dongguk.capstone.backend.Repository;
//
//import dongguk.capstone.backend.DTO.SignupDTO;
//import dongguk.capstone.backend.domain.User;
//import jakarta.persistence.EntityManager;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public class UserRepository{
    // 스프링 데이터 JPA를 사용하려면 클래스 형태가 아니라 인터페이스 형태로 사용해야 된다

    // JPA - ORM은 일반적으로 단순한 CRUD(Create, Read, Update, Delete) 작업에 유용
    // JPA - 그러나 복잡한 비즈니스 로직이나 고성능을 요구하는 쿼리 등의 경우에는 직접 SQL을 작성하는 것이 ORM보다 더 효율적
//
//
//    // 이거 스프링 데이터 JPA로 바꾸자
//
//    private final EntityManager em; // JPA는 EntityManager가 무조건 필요!
//
//    public UserRepository(EntityManager em){
//        this.em = em;
//    }
//    public void save(User user) {
//        em.persist(user);
//    }
//
//
//}
