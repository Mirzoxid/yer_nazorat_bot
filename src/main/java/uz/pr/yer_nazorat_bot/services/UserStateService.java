package uz.pr.yer_nazorat_bot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Contact;
import uz.pr.yer_nazorat_bot.enteties.TgUser;
import uz.pr.yer_nazorat_bot.repositories.UserStateRepository;

import java.time.Instant;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserStateService {
    private final TgBotUserService tgBotUserService;

}
