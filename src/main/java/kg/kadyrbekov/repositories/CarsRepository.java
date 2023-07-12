package kg.kadyrbekov.repositories;

import kg.kadyrbekov.model.User;
import kg.kadyrbekov.model.entity.Cars;
import kg.kadyrbekov.model.enums.CarsStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarsRepository extends JpaRepository<Cars, Long> {


    List<Cars> findAllByCarsStatus(CarsStatus status);

    boolean existsByIdAndLikedUsers(Long carId, User user);


}
