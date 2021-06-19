package uz.pr.yer_nazorat_bot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import uz.pr.yer_nazorat_bot.components.UserStateMapComponent;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UpdateCallbackService {

    private final AppealRegistrationService appealRegistrationService;
    private final UserStateMapComponent userStateMap;

    public void callbackUpdate(CallbackQuery callbackQuery) {
        Map<String, Object> userState = userStateMap.getUserStateMap(callbackQuery.getFrom().getId().toString());
        appealRegistrationService.nextSteepCallback(callbackQuery, userState);
    }
}
