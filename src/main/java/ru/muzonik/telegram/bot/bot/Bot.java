package ru.muzonik.telegram.bot.bot;

import lombok.Getter;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.muzonik.telegram.bot.bot.commands.MinusCommand;
import ru.muzonik.telegram.bot.bot.commands.PlusCommand;
import ru.muzonik.telegram.bot.bot.commands.PlusMinusCommand;
import ru.muzonik.telegram.bot.bot.commands.HelpCommand;
import ru.muzonik.telegram.bot.bot.commands.SettingsCommand;
import ru.muzonik.telegram.bot.bot.commands.StartCommand;
import ru.muzonik.telegram.bot.bot.noncommand.NonCommand;
import ru.muzonik.telegram.bot.bot.noncommand.Settings;

import java.util.HashMap;
import java.util.Map;

public final class Bot extends TelegramLongPollingCommandBot {
    private final String BOT_NAME;
    private final String BOT_TOKEN;

    //Настройки по умолчанию
    @Getter
    private static final Settings defaultSettings = new Settings(1, 15, 1);

    //Класс для обработки сообщений, не являющихся командой
    private final NonCommand nonCommand;

    /**
     * Настройки файла для разных пользователей. Ключ - уникальный id чата
     */
    @Getter
    private static Map<Long, Settings> userSettings;

    public Bot(String botName, String botToken) {
        super();
        this.BOT_NAME = botName;
        this.BOT_TOKEN = botToken;
        //создаём вспомогательный класс для работы с сообщениями, не являющимися командами
        this.nonCommand = new NonCommand();
        //регистрируем команды
        register(new StartCommand("start", "Старт"));
        register(new PlusCommand("plus", "Сложение"));
        register(new MinusCommand("minus", "Вычитание"));
        register(new PlusMinusCommand("plusminus", "Сложение и вычитание"));
        register(new HelpCommand("help", "Помощь"));
        register(new SettingsCommand("settings", "Мои настройки"));
        userSettings = new HashMap<>();
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    /**
     * Ответ на запрос, не являющийся командой
     */
    @Override
    public void processNonCommandUpdate(Update update) {
        Message msg = update.getMessage();
        Long chatId = msg.getChatId();
        String userName = getUserName(msg);

        String answer = nonCommand.nonCommandExecute(chatId, userName, msg.getText());
        setAnswer(chatId, userName, answer);
    }

    /**
     * Получение настроек по id чата. Если ранее для этого чата в ходе сеанса работы бота настройки не были установлены, используются настройки по умолчанию
     */
    public static Settings getUserSettings(Long chatId) {
        Map<Long, Settings> userSettings = Bot.getUserSettings();
        Settings settings = userSettings.get(chatId);
        if (settings == null) {
            return defaultSettings;
        }
        return settings;
    }

    /**
     * Формирование имени пользователя
     *
     * @param msg сообщение
     */
    private String getUserName(Message msg) {
        User user = msg.getFrom();
        String userName = user.getUserName();
        return (userName != null) ? userName : String.format("%s %s", user.getLastName(), user.getFirstName());
    }

    /**
     * Отправка ответа
     *
     * @param chatId   id чата
     * @param userName имя пользователя
     * @param text     текст ответа
     */
    private void setAnswer(Long chatId, String userName, String text) {
        SendMessage answer = new SendMessage();
        answer.setText(text);
        answer.setChatId(chatId.toString());
        try {
            execute(answer);
        } catch (TelegramApiException e) {
            //логируем сбой Telegram Bot API, используя userName
        }
    }
}
