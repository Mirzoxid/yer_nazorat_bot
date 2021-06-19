package uz.pr.yer_nazorat_bot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Contact;
import uz.pr.yer_nazorat_bot.enteties.TgUser;
import uz.pr.yer_nazorat_bot.repositories.TgBotUserRepository;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TgBotUserService {
    private final TgBotUserRepository tgBotUserRepository;

    public void createBotUser(String userName, Contact contact) {
        Optional<TgUser> optionalTgUser = tgBotUserRepository.findByTgChatId(contact.getUserId().toString());
        if (optionalTgUser.isPresent()){
            return;
        }
        TgUser tgUser = new TgUser();
        if (Objects.nonNull(userName)){
            tgUser.setUserName(userName);
        }
        if (Objects.nonNull(contact.getFirstName())){
            tgUser.setFirstName(contact.getFirstName());
        }
        if (Objects.nonNull(contact.getLastName())){
            tgUser.setLastName(contact.getLastName());
        }
        if (Objects.nonNull(contact.getPhoneNumber())){
            tgUser.setPhoneNumber(contact.getPhoneNumber());
        }
        tgUser.setTgChatId(contact.getUserId().toString());
        tgUser.setCreatedDate(Instant.now());
        tgBotUserRepository.save(tgUser);
    }

    public boolean hasRegistered(String chatId) {
        return tgBotUserRepository.findByTgChatId(chatId).isPresent();
    }

    public TgUser getUser(String chatId){
        return tgBotUserRepository.findByTgChatId(chatId).orElse(null);
    }
}
