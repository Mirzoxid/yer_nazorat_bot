package uz.pr.yer_nazorat_bot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Contact;
import uz.pr.yer_nazorat_bot.enteties.TgUser;
import uz.pr.yer_nazorat_bot.enteties.UserState;
import uz.pr.yer_nazorat_bot.repositories.UserStateRepository;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserStateService {
    private final TgBotUserService tgBotUserService;
    private final UserStateRepository userStateRepository;

    public void save(String userId, String stateData){
        UserState userState = userStateRepository.findByUserId(userId).orElseGet(UserState::new);
        if (Objects.nonNull(userState.getId())){
            userState.setUpdatedDate(Instant.now());
        } else {
            userState.setCreatedDate(Instant.now());
        }
        userState.setUserId(userId);
        userState.setStateData(stateData);
        userStateRepository.save(userState);
    }

    public List<UserState> getAllStates() {
        return userStateRepository.findAll();
    }
}
