package dongguk.capstone.backend.Repository;

import dongguk.capstone.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> { // JpaRepository의 제너릭은 <Domain 타입, ID 타입> 이다.
    // save는 JpaRepository에 이미 존재한다(정확히는 부모 인터페이스에 존재)
    Optional<User> findByEmail(String email);
}