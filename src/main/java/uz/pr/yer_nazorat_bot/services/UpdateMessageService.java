package uz.pr.yer_nazorat_bot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.pr.yer_nazorat_bot.components.UserStateMapComponent;
import uz.pr.yer_nazorat_bot.enums.UserStateType;
import uz.pr.yer_nazorat_bot.events.SendMessageEvent;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UpdateMessageService {
    private final ApplicationEventPublisher publisher;
    private final UserStateMapComponent userStateMap;
    private final TgBotUserService tgBotUserService;

    public void messageUpdate(Message message) {
        if (message.isCommand()) {
            loadCommand(message);
        } else if (message.hasContact()) {
            tgBotUserService.createBotUser(message.getFrom().getUserName(), message.getContact());
            publisher.publishEvent(new SendMessageEvent(generateStartMessage(message)));
            userStateMap.putUserStateMap(message.getChatId().toString(), "stateType", UserStateType.WAIT);
        } else {
            userStateMap.putUserStateMap(message.getChatId().toString(), "stateType", UserStateType.REGISTER_APPIAL);
        }
    }

    private void loadCommand(Message message) {
        Map<String, Object> userState = userStateMap.getUserStateMap(message.getChatId().toString());
        if (Objects.equals(message.getText(), "/start")) {
            if (Objects.equals(userState.getOrDefault("stateType", UserStateType.VISITED.name()), UserStateType.VISITED.name())
                    && !tgBotUserService.hasRegistered(message.getChatId().toString())) {
                publisher.publishEvent(new SendMessageEvent(generateRegisterPhoneMessage(message)));
                userStateMap.putUserStateMap(message.getChatId().toString(), "stateType", UserStateType.VISITED);
            } else {
                publisher.publishEvent(new SendMessageEvent(generateStartMessage(message)));
                userStateMap.putUserStateMap(message.getChatId().toString(), "stateType", UserStateType.WAIT);
            }
        } else {

        }
    }

    private SendMessage generateRegisterPhoneMessage(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(new ReplyKeyboardMarkup() {{
            setKeyboard(new ArrayList<KeyboardRow>() {{
                add(new KeyboardRow() {{
                    add(new KeyboardButton() {{
                        setText("Рақамни жунатиш");
                        setRequestContact(true);
                    }});
                }});
            }});
            setResizeKeyboard(true);
            setOneTimeKeyboard(true);
        }});
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.enableMarkdown(true);
        String stringBuilder = "“YER NAZORATI” бот!\n" +
                "Ботдан фойдаланиш учун аввал телефон рақамингизни рўйҳатдан ўтказишингиз керак!\n" +
                "Шахсингиз сир сақланиши кафолатланади.";
        sendMessage.setText(stringBuilder);
        return sendMessage;
    }

    private SendMessage generateStartMessage(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.enableMarkdown(true);
        String stringBuilder = "Aссалому алайкум, “YER NAZORATI” дастурига хуш келибсиз!\n" +
                "Ушбу бот орқали Сиз ўз яшаш ҳудудингиздаги қишлоқ ва ноқишлоқ хўжалиги ер майдонларидан фойдаланиш жараёнида йўл қўйилаётган қонун бузилишлар ва бошқа ҳолатлар ҳақида Бош прокуратурага хабар беришингиз мумкин. \n" +
                "Маълумот тақдим қилган фуқароларнинг шахси сир сақланиши кафолатланади. \n" +
                "Ҳар бир хабар бўйича таъсирчан чоралар қўлланилади.";
        sendMessage.setText(stringBuilder);
        sendMessage.setReplyMarkup(new ReplyKeyboardMarkup() {{
            setKeyboard(new ArrayList<KeyboardRow>() {{
                add(new KeyboardRow() {{
                    add(new KeyboardButton() {{
                        setText("Хабар қолдириш");
                    }});
                }});
            }});
            setResizeKeyboard(true);
            setOneTimeKeyboard(true);
        }});
        return sendMessage;
    }
}
