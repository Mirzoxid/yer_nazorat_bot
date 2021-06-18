package uz.pr.yer_nazorat_bot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.pr.yer_nazorat_bot.enteties.UserState;

@Repository
public interface UserStateRepository extends JpaRepository<UserState, Long> {
}
