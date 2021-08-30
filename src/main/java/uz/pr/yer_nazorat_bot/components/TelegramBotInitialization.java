package uz.pr.yer_nazorat_bot.components;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.pr.yer_nazorat_bot.enteties.NazoratMessageFiles;
import uz.pr.yer_nazorat_bot.events.DeleteMessageEvent;
import uz.pr.yer_nazorat_bot.events.MessageSendEvent;
import uz.pr.yer_nazorat_bot.events.SendMessageEvent;
import uz.pr.yer_nazorat_bot.events.UpdateInitializeEvent;
import uz.pr.yer_nazorat_bot.services.AppealRegistrationService;
import uz.pr.yer_nazorat_bot.utils.EventCreater;
import uz.pr.yer_nazorat_bot.utils.FileResourceUtil;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class TelegramBotInitialization extends TelegramLongPollingBot {
    private final AppealRegistrationService appealRegistrationService;
    @Value("${telegram-bot.username}")
    private String botName;
    @Value("${telegram-bot.token}")
    private String botToken;
    @Value("${storage.upload-url}")
    private String uploadUrl;
    private final Logger logger = LogManager.getLogger(TelegramBotInitialization.class);

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

    public File downloadPhoto(final String filePath) {
        try {
            return downloadFile(filePath);
        } catch (final TelegramApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getFilePathByFileId(String fileId) throws TelegramApiException {
        GetFile getFile = new GetFile();
        getFile.setFileId(fileId);
        return execute(getFile).getFilePath();
    }

    @Scheduled(fixedDelayString = "${fixedDelay.in.milliseconds}")
    private void loadFiles() {
        List<NazoratMessageFiles> nazoratMessageFiles = appealRegistrationService.findAllByFileUrlIsNull();
        for (NazoratMessageFiles nazoratMessageFile : nazoratMessageFiles) {
            try {
                if (nazoratMessageFile.getDownloadActionCnt() < 4 ){
                    String filePath = getFilePathByFileId(nazoratMessageFile.getTgFileId());
                    FileResourceUtil.Result result = FileResourceUtil.uploadResource(uploadUrl,
                            "obod-mahalla-bot",
                            FileResourceUtil.ResourceType.TG_FILES,
                            filePath,
                            Files.readAllBytes(downloadFile(filePath).toPath()));
                    if (result != null && result.getFileResourceUrl() != null) {
                        nazoratMessageFile.setFileUrl(result.getFileResourceUrl());
                        appealRegistrationService.saveMessageFile(nazoratMessageFile);
                    }
                }
            } catch (Exception e) {
                nazoratMessageFile.setDownloadActionCnt(nazoratMessageFile.getDownloadActionCnt() + 1);
                appealRegistrationService.saveMessageFile(nazoratMessageFile);
                logger.error("messageId: " + nazoratMessageFile.getNazoratMessage().getId());
                logger.error("Get telegram file error: ", e);
            }
        }
    }
}
