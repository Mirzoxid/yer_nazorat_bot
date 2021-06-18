package uz.pr.yer_nazorat_bot.events;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import uz.pr.yer_nazorat_bot.utils.EventCreater;

public class DeleteMessageEvent extends EventCreater<DeleteMessage> {
    public DeleteMessageEvent(DeleteMessage obj) {
        super(obj);
    }
}
