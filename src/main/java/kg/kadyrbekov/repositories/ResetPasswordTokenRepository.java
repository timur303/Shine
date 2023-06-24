package kg.kadyrbekov.repositories;


import kg.kadyrbekov.model.entity.ResetPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResetPasswordTokenRepository extends JpaRepository<ResetPasswordToken,Long> {

    ResetPasswordToken findByToken(String token);
}
