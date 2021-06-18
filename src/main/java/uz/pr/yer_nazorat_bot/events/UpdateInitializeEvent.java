package uz.pr.yer_nazorat_bot.events;

import org.telegram.telegrambots.meta.api.objects.Update;
import uz.pr.yer_nazorat_bot.utils.EventCreater;

public class UpdateInitializeEvent extends EventCreater<Update> {

    public UpdateInitializeEvent(Update update) {
        super(update);
    }
}
