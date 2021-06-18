package uz.pr.yer_nazorat_bot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.pr.yer_nazorat_bot.enteties.TgUser;

import java.util.Optional;

@Repository
public interface TgBotUserRepository extends JpaRepository<TgUser, Long> {
    Optional<TgUser> findByTgChatId(String tgChatId);
}
