package uz.pr.yer_nazorat_bot.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.pr.yer_nazorat_bot.events.UpdateInitializeEvent;
import uz.pr.yer_nazorat_bot.utils.EventCreater;

@Service
@Slf4j
@RequiredArgsConstructor
public class TgUpdateProcessor {

    private final UpdateMessageService updateMessageService;

    @EventListener(classes = {UpdateInitializeEvent.class})
    public void handleUpdate(EventCreater<Update> eventUpdate){
        Update update = eventUpdate.get();
        if (update.hasMessage()){
            updateMessageService.messageUpdate(update.getMessage());
        }
    }

}
