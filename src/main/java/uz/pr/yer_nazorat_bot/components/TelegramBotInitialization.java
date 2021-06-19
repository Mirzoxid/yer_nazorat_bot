package uz.pr.yer_nazorat_bot.components;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.pr.yer_nazorat_bot.events.DeleteMessageEvent;
import uz.pr.yer_nazorat_bot.events.MessageSendEvent;
import uz.pr.yer_nazorat_bot.events.SendMessageEvent;
import uz.pr.yer_nazorat_bot.events.UpdateInitializeEvent;
import uz.pr.yer_nazorat_bot.utils.EventCreater;

@Component
@Slf4j
@RequiredArgsConstructor
public class TelegramBotInitialization extends TelegramLongPollingBot {
    @Value("${telegram-bot.username}")
    private String botName;
    @Value("${telegram-bot.token}")
    private String botToken;

    private final ApplicationEventPublisher publisher;

    @Override
    public String getBotUsername() {
        return this.botName;
    }

    @Override
    public String getBotToken() {
        return this.botToken;
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        publisher.publishEvent(new UpdateInitializeEvent(update));
    }

    @EventListener(classes = {SendMessageEvent.class})
    public void sendMessageExecute(EventCreater<SendMessage> messageEvent) {
        SendMessage sendMessage = messageEvent.get();
        try {
            publisher.publishEvent(new MessageSendEvent(execute(sendMessage)));
            log.info("Message is sended {}", sendMessage);
        } catch (Exception e) {
            log.error("Exception while send message {} to user: {}", sendMessage, e.getMessage());
        }
    }

    @EventListener(classes = {DeleteMessageEvent.class})
    public void deleteMessage(EventCreater<DeleteMessage> messageEvent) throws TelegramApiException {
        DeleteMessage deleteMessage = messageEvent.get();
        execute(deleteMessage);
    }
}
