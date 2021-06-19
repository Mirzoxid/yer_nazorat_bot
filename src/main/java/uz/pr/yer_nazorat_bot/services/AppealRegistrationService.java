package uz.pr.yer_nazorat_bot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.pr.yer_nazorat_bot.components.UserStateMapComponent;
import uz.pr.yer_nazorat_bot.enteties.District;
import uz.pr.yer_nazorat_bot.enteties.NazoratMessage;
import uz.pr.yer_nazorat_bot.enteties.NazoratMessageFiles;
import uz.pr.yer_nazorat_bot.enteties.Region;
import uz.pr.yer_nazorat_bot.enums.NazoratMessageStatus;
import uz.pr.yer_nazorat_bot.enums.QonunBuzilishTuri;
import uz.pr.yer_nazorat_bot.enums.RegisterStateType;
import uz.pr.yer_nazorat_bot.enums.YerTuri;
import uz.pr.yer_nazorat_bot.events.DeleteMessageEvent;
import uz.pr.yer_nazorat_bot.events.SendMessageEvent;
import uz.pr.yer_nazorat_bot.repositories.DistrictRepository;
import uz.pr.yer_nazorat_bot.repositories.NazoratMessageFileRepository;
import uz.pr.yer_nazorat_bot.repositories.NazoratMessageRepository;
import uz.pr.yer_nazorat_bot.repositories.RegionRepository;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AppealRegistrationService {
    private final ApplicationEventPublisher publisher;
    private final TgBotUserService tgBotUserService;
    private final UserStateMapComponent userStateMap;
    private final RegionRepository regionRepository;
    private final DistrictRepository districtRepository;
    private final NazoratMessageRepository nazoratMessageRepository;
    private final NazoratMessageFileRepository nazoratMessageFileRepository;


    public void startRegistration(Message message, Map<String, Object> userState) {
        userStateMap.putUserStateMap(message.getChatId().toString(), "registerState", RegisterStateType.REGION.name());
        userStateMap.putUserStateMap(message.getChatId().toString(), "nazoratMessageId", createNazoratMessageAndGetId(message.getChatId()));
        publisher.publishEvent(new SendMessageEvent(regionSelectMessage(message)));
    }

    public void nextSteepMessage(Message message, Map<String, Object> userState) {
        if ("Якунлаш!".equals(message.getText())) {
            checkAndEndRegister(message, userState);
        } else {
            if (RegisterStateType.ADDRESS.name().equals(userState.get("registerState"))) {
                publisher.publishEvent(new DeleteMessageEvent(deleteMessageData(message.getFrom().getId().toString(), (Integer) userState.get("sendMessageId"))));
                userStateMap.putUserStateMap(message.getFrom().getId().toString(), "registerState", RegisterStateType.FINISH.name());
                publisher.publishEvent(new SendMessageEvent(sendMessageInformationRegisterData(message, userState)));
                publisher.publishEvent(new SendMessageEvent(messageAndFileInput(message, userState)));
            } else if (RegisterStateType.FINISH.name().equals(userState.get("registerState"))) {
//                publisher.publishEvent(new DeleteMessageEvent(deleteMessageData(message.getFrom().getId().toString(), (Integer) userState.get("sendMessageId"))));
                publisher.publishEvent(new SendMessageEvent(finishDataSave(message, userState)));
            }
        }
    }

    public void nextSteepCallback(CallbackQuery callbackQuery, Map<String, Object> userState) {
        if (RegisterStateType.REGION.name().equals(userState.get("registerState"))) {
            publisher.publishEvent(new DeleteMessageEvent(deleteMessageData(callbackQuery.getFrom().getId().toString(), (Integer) userState.get("sendMessageId"))));
            userStateMap.putUserStateMap(callbackQuery.getFrom().getId().toString(), "registerState", RegisterStateType.DISTRICT.name());
            publisher.publishEvent(new SendMessageEvent(districtSelectMessage(callbackQuery, userState)));
        } else if (RegisterStateType.DISTRICT.name().equals(userState.get("registerState"))) {
            publisher.publishEvent(new DeleteMessageEvent(deleteMessageData(callbackQuery.getFrom().getId().toString(), (Integer) userState.get("sendMessageId"))));
            userStateMap.putUserStateMap(callbackQuery.getFrom().getId().toString(), "registerState", RegisterStateType.YER_TYPE.name());
            publisher.publishEvent(new SendMessageEvent(yerTypeSelectMessage(callbackQuery, userState)));
        } else if (RegisterStateType.YER_TYPE.name().equals(userState.get("registerState"))) {
            publisher.publishEvent(new DeleteMessageEvent(deleteMessageData(callbackQuery.getFrom().getId().toString(), (Integer) userState.get("sendMessageId"))));
            userStateMap.putUserStateMap(callbackQuery.getFrom().getId().toString(), "registerState", RegisterStateType.QONUN_BUZILISH.name());
            publisher.publishEvent(new SendMessageEvent(qonunBuzilishTuriSelectData(callbackQuery, userState)));
        } else if (RegisterStateType.QONUN_BUZILISH.name().equals(userState.get("registerState"))) {
            publisher.publishEvent(new DeleteMessageEvent(deleteMessageData(callbackQuery.getFrom().getId().toString(), (Integer) userState.get("sendMessageId"))));
            userStateMap.putUserStateMap(callbackQuery.getFrom().getId().toString(), "registerState", RegisterStateType.ADDRESS.name());
            publisher.publishEvent(new SendMessageEvent(addressInputData(callbackQuery, userState)));
        }
    }

    public void nextSteepPhotoAndDocument(Message message) {
        if (message.hasDocument()) {
            Document document = message.getDocument();
            Map<String, Object> userState = userStateMap.getUserStateMap(message.getFrom().getId().toString());
            if (userState.containsKey("nazoratMessageId") && Objects.nonNull(userState.get("nazoratMessageId"))) {
                Optional<NazoratMessage> nazoratMessage = nazoratMessageRepository.findById((Long) userState.get("nazoratMessageId"));
                NazoratMessageFiles nazoratMessageFiles = new NazoratMessageFiles();
                nazoratMessageFiles.setNazoratMessage(nazoratMessage.get());
                nazoratMessageFiles.setTgFileId(document.getFileId());
                nazoratMessageFiles.setIsDeleted(0);
                nazoratMessageFiles.setDownloadActionCnt(0L);
                nazoratMessageFileRepository.save(nazoratMessageFiles);
            }
        } else {
            List<PhotoSize> photoSizes = message.getPhoto();
            Map<String, Object> userState = userStateMap.getUserStateMap(message.getFrom().getId().toString());
            if (userState.containsKey("nazoratMessageId") && Objects.nonNull(userState.get("nazoratMessageId"))) {
                NazoratMessageFiles nazoratMessageFiles = new NazoratMessageFiles();
                Optional<NazoratMessage> nazoratMessage = nazoratMessageRepository.findById((Long) userState.get("nazoratMessageId"));
                nazoratMessageFiles.setNazoratMessage(nazoratMessage.get());
                nazoratMessageFiles.setTgFileId(photoSizes.get(photoSizes.size() - 1).getFileId());
                nazoratMessageFiles.setIsDeleted(0);
                nazoratMessageFiles.setDownloadActionCnt(0L);
                nazoratMessageFileRepository.save(nazoratMessageFiles);
            }
        }
    }

    private DeleteMessage deleteMessageData(String userId, Integer sendMessageId) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setMessageId(sendMessageId);
        deleteMessage.setChatId(userId);
        return deleteMessage;
    }

    public void sendedMessage(Message message) {
        Map<String, Object> userState = userStateMap.getUserStateMap(message.getChatId().toString());
        if (RegisterStateType.hasContains(userState.getOrDefault("registerState", "").toString())) {
            userStateMap.putUserStateMap(message.getChatId().toString(), "sendMessageId", message.getMessageId());
        }
    }

    private SendMessage regionSelectMessage(Message message) {
        SendMessage msg = new SendMessage();
        msg.setText("Вилоятни танланг!");
        msg.setChatId(message.getChatId().toString());
        msg.setReplyMarkup(new InlineKeyboardMarkup() {{
            List<List<InlineKeyboardButton>> keyboardsList = new ArrayList<>();
            List<InlineKeyboardButton> keyboards = new ArrayList<>();
            List<Region> regions = regionRepository.findAllByOrderByName();
            for (int i = 0; i < regions.size(); i++) {
                Region region = regions.get(i);
                keyboards.add(new InlineKeyboardButton() {{
                    setText(region.getName());
                    setCallbackData(region.getId().toString());
                }});
                if (i % 2 == 1) {
                    keyboardsList.add(keyboards);
                    keyboards = new ArrayList<>();
                }
            }
            keyboardsList.add(keyboards);
            setKeyboard(keyboardsList);
        }});
        return msg;
    }

    private SendMessage districtSelectMessage(CallbackQuery callbackQuery, Map<String, Object> userState) {
        Long id = Long.parseLong(callbackQuery.getData());

        //Nazorat message save
        Optional<NazoratMessage> nazoratMessageO = nazoratMessageRepository.findById((Long) userState.get("nazoratMessageId"));
        NazoratMessage nazoratMessage = nazoratMessageO.get();
        nazoratMessage.setRegion(regionRepository.getById(id));
        nazoratMessageRepository.save(nazoratMessage);

        SendMessage msg = new SendMessage();
        msg.setText("Туман (шаҳар)ни танланг!");
        msg.setChatId(callbackQuery.getFrom().getId().toString());
        msg.setReplyMarkup(new InlineKeyboardMarkup() {{
            List<List<InlineKeyboardButton>> keyboardsList = new ArrayList<>();
            List<InlineKeyboardButton> keyboards = new ArrayList<>();
            List<District> districts = districtRepository.findAllByRegionIdOrderByName(id);
            for (int i = 0; i < districts.size(); i++) {
                District district = districts.get(i);
                keyboards.add(new InlineKeyboardButton() {{
                    setText(district.getName());
                    setCallbackData(district.getId().toString());
                }});
                if (i % 2 == 1) {
                    keyboardsList.add(keyboards);
                    keyboards = new ArrayList<>();
                }
            }
            keyboardsList.add(keyboards);
            setKeyboard(keyboardsList);
        }});
        return msg;
    }

    private SendMessage yerTypeSelectMessage(CallbackQuery callbackQuery, Map<String, Object> userState) {
        Long id = Long.parseLong(callbackQuery.getData());

        //Nazorat message save
        Optional<NazoratMessage> nazoratMessageO = nazoratMessageRepository.findById((Long) userState.get("nazoratMessageId"));
        NazoratMessage nazoratMessage = nazoratMessageO.get();
        nazoratMessage.setDistrict(districtRepository.getById(id));
        nazoratMessageRepository.save(nazoratMessage);

        SendMessage msg = new SendMessage();
        msg.setText("Қонун бузилиши аниқланган ер турини танланг!");
        msg.setChatId(callbackQuery.getFrom().getId().toString());
        msg.setReplyMarkup(new InlineKeyboardMarkup() {{
            List<List<InlineKeyboardButton>> keyboardsList = new ArrayList<>();
            List<InlineKeyboardButton> keyboards = new ArrayList<>();
            keyboards.add(new InlineKeyboardButton() {{
                setText("Қишлоқ хўжалиги ерлари");
                setCallbackData(YerTuri.QISHLOQ_XUJALIGI_YERLARI.name());
            }});
            keyboardsList.add(keyboards);
            keyboards = new ArrayList<>();
            keyboards.add(new InlineKeyboardButton() {{
                setText("Ноқишлоқ хўжалиги ерлари");
                setCallbackData(YerTuri.NOQISHLOQ_XUJALIGI_YERLARI.name());
            }});
            keyboardsList.add(keyboards);
            setKeyboard(keyboardsList);
        }});
        return msg;
    }

    private SendMessage addressInputData(CallbackQuery callbackQuery, Map<String, Object> userState) {

        //Nazorat message save
        Optional<NazoratMessage> nazoratMessageO1 = nazoratMessageRepository.findById((Long) userState.get("nazoratMessageId"));
        NazoratMessage nazoratMessage1 = nazoratMessageO1.get();
        nazoratMessage1.setQonunBuzilishTuri(QonunBuzilishTuri.valueOf(callbackQuery.getData()));
        nazoratMessageRepository.save(nazoratMessage1);

        SendMessage msg = new SendMessage();
        msg.setText("Қонун бузилиш аниқланган ҳудуд номи, юридик ёки жисмоний шахс маълумотлари \n" +
                "(ер контури, қишлоқ хўжалиги корхонаси номи, раҳбари Ф.И.Ш ва ҳ.к.)\n\n" +
                "Ёзувли хабар сифатида қолдиринг!");
        msg.setChatId(callbackQuery.getFrom().getId().toString());
        return msg;
    }

    private SendMessage qonunBuzilishTuriSelectData(CallbackQuery callbackQuery, Map<String, Object> userState) {
        //Nazorat message save
        Optional<NazoratMessage> nazoratMessageO = nazoratMessageRepository.findById((Long) userState.get("nazoratMessageId"));
        NazoratMessage nazoratMessage = nazoratMessageO.get();
        nazoratMessage.setYerTuri(YerTuri.valueOf(callbackQuery.getData()));
        nazoratMessageRepository.save(nazoratMessage);

        SendMessage msg = new SendMessage();
        msg.setText("Қонун бузилиши турини танланг!");
        msg.setChatId(callbackQuery.getFrom().getId().toString());
        msg.setReplyMarkup(new InlineKeyboardMarkup() {{
            List<List<InlineKeyboardButton>> keyboardsList = new ArrayList<>();
            List<InlineKeyboardButton> keyboards = new ArrayList<>();
            keyboards.add(new InlineKeyboardButton() {{
                setText("Ўзбошимчалик билан эгаллаш");
                setCallbackData(QonunBuzilishTuri.UZBOSHIMCHALIK_BILAN_EGALLASH.name());
            }});
            keyboardsList.add(keyboards);
            keyboards = new ArrayList<>();
            keyboards.add(new InlineKeyboardButton() {{
                setText("Ноқонуний қурилиш");
                setCallbackData(QonunBuzilishTuri.NOQONUNIY_QURILISH.name());
            }});
            keyboardsList.add(keyboards);
            keyboards = new ArrayList<>();
            keyboards.add(new InlineKeyboardButton() {{
                setText("Мақсадсиз ва самарасиз фойдаланиш");
                setCallbackData(QonunBuzilishTuri.MAQSADSIZ_VA_SAMARASIZ_QURILISH.name());
            }});
            keyboardsList.add(keyboards);
            setKeyboard(keyboardsList);
        }});
        return msg;
    }

    private SendMessage messageAndFileInput(Message message, Map<String, Object> userState) {
        SendMessage msg = new SendMessage();
        msg.setText("Қонун бузилиш холатини ёзувли хабар сифатида қолдиринг ва тўпланган ҳужжатларни (камида 2 та расм) жунатинг!");
        msg.setReplyMarkup(new ReplyKeyboardMarkup() {{
            setKeyboard(new ArrayList<KeyboardRow>() {{
                add(new KeyboardRow() {{
                    add(new KeyboardButton() {{
                        setText("Якунлаш!");
                    }});
                }});
            }});
            setResizeKeyboard(true);
            setOneTimeKeyboard(true);
        }});
        msg.setChatId(message.getFrom().getId().toString());
        return msg;
    }

    private SendMessage sendMessageInformationRegisterData(Message message, Map<String, Object> userState) {

        String address = message.getText();

        //Nazorat message save
        Optional<NazoratMessage> nazoratMessageO = nazoratMessageRepository.findById((Long) userState.get("nazoratMessageId"));
        NazoratMessage nazoratMessage = nazoratMessageO.get();
        nazoratMessage.setAddress(address);
        nazoratMessageRepository.save(nazoratMessage);

        SendMessage msg = new SendMessage();
        Optional<NazoratMessage> nazoratMessageO1 = nazoratMessageRepository.findById((Long) userState.get("nazoratMessageId"));
        NazoratMessage nazoratMessage1 = nazoratMessageO1.get();
        String msgText = "- " + nazoratMessage1.getRegion().getName() + "\n";
        msgText += "- " + nazoratMessage1.getDistrict().getName() + "\n";
        msgText += "- " + nazoratMessage1.getYerTuri().toString() + "\n";
        msgText += "- " + nazoratMessage1.getQonunBuzilishTuri().toString() + "\n";
        msgText += "- " + nazoratMessage1.getAddress() + "\n";
        msg.setText(msgText);
        msg.setChatId(message.getFrom().getId().toString());
        return msg;
    }

    private SendMessage finishDataSave(Message message, Map<String, Object> userState) {
        //Nazorat message save
        Optional<NazoratMessage> nazoratMessageO = nazoratMessageRepository.findById((Long) userState.get("nazoratMessageId"));
        NazoratMessage nazoratMessage = nazoratMessageO.get();
        if (Objects.nonNull(nazoratMessage.getXabarMazmuni())){
            nazoratMessage.setXabarMazmuni(message.getText());
        } else {
            nazoratMessage.setXabarMazmuni(nazoratMessage.getXabarMazmuni() + "\n" + message.getText());
        }
        nazoratMessageRepository.save(nazoratMessage);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Қонун бузилишининг матнли мазмуни қабул қилинди");
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyMarkup(new ReplyKeyboardMarkup() {{
            setKeyboard(new ArrayList<KeyboardRow>() {{
                add(new KeyboardRow() {{
                    add(new KeyboardButton() {{
                        setText("Якунлаш!");
                    }});
                }});
            }});
            setResizeKeyboard(true);
            setOneTimeKeyboard(true);
        }});
        return sendMessage;
    }

    private Long createNazoratMessageAndGetId(Long chatId) {
        NazoratMessage nazoratMessage = new NazoratMessage();
        nazoratMessage.setMessageStatus(NazoratMessageStatus.CREATED);
        nazoratMessage.setCreatedDate(Instant.now());
        nazoratMessage.setIsView((byte) 0);
        nazoratMessage.setTgUser(tgBotUserService.getUser(chatId.toString()));
        nazoratMessage = nazoratMessageRepository.save(nazoratMessage);
        return nazoratMessage.getId();
    }

    private void checkAndEndRegister(Message message, Map<String, Object> userState) {
        Optional<NazoratMessage> nazoratMessageO = nazoratMessageRepository.findById((Long) userState.get("nazoratMessageId"));
        NazoratMessage nazoratMessage = nazoratMessageO.get();
        List<NazoratMessageFiles> nazoratMessageFiles = nazoratMessageFileRepository.findAllByNazoratMessageId(nazoratMessage.getId());
        if (nazoratMessageFiles.size() >= 2 && !nazoratMessage.getXabarMazmuni().isEmpty()){
            nazoratMessage.setMessageStatus(NazoratMessageStatus.NEW);
            nazoratMessageRepository.save(nazoratMessage);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(message.getChatId().toString());
            sendMessage.setText("Хабарингиз қабул қилинди!\n" +
                    "Хабарингиз кўриб чиқилиб, таъсирчан чоралар қўлланилади!");
            userStateMap.endRegisterState(message.getChatId().toString());
            publisher.publishEvent(new SendMessageEvent(sendMessage));
        } else {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(message.getChatId().toString());
            sendMessage.setReplyMarkup(new ReplyKeyboardMarkup() {{
                setKeyboard(new ArrayList<KeyboardRow>() {{
                    add(new KeyboardRow() {{
                        add(new KeyboardButton() {{
                            setText("Якунлаш!");
                        }});
                    }});
                }});
                setResizeKeyboard(true);
                setOneTimeKeyboard(true);
            }});
            String messageText = "";
            if(Objects.isNull(nazoratMessage.getXabarMazmuni()) || nazoratMessage.getXabarMazmuni().isEmpty()) {
                messageText += "* Хабарни якунлаш учун қонун бузилиш холатини ёзувли хабар сифатида қолдиринг!\n";
            }
            if (nazoratMessageFiles.size() < 2){
                messageText += "* Хабарни якунлаш тўпланган ҳужжатларни (камида 2 та расм) жунатинг!'\n";
            }
            sendMessage.setText(messageText);
            publisher.publishEvent(new SendMessageEvent(sendMessage));
        }
    }

}
