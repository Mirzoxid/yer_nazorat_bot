package uz.pr.yer_nazorat_bot.events;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import uz.pr.yer_nazorat_bot.utils.EventCreater;

public class SendMessageEvent extends EventCreater<SendMessage> {

    public SendMessageEvent(SendMessage obj) {
        super(obj);
    }
}
