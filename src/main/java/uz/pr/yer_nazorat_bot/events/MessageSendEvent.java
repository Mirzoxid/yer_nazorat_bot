package uz.pr.yer_nazorat_bot.events;

import org.telegram.telegrambots.meta.api.objects.Message;
import uz.pr.yer_nazorat_bot.utils.EventCreater;

public class MessageSendEvent extends EventCreater<Message> {
    public MessageSendEvent(Message message) {
        super(message);
    }
}
