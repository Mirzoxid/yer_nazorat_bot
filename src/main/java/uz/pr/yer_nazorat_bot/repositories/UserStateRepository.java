package uz.pr.yer_nazorat_bot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.pr.yer_nazorat_bot.enteties.UserState;

import java.util.Optional;

@Repository
public interface UserStateRepository extends JpaRepository<UserState, Long> {
    Optional<UserState> findByUserId(String userId);
}
