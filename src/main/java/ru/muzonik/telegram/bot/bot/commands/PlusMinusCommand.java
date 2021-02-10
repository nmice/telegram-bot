package ru.muzonik.telegram.bot.bot.commands;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.muzonik.telegram.bot.enums.OperationEnum;

/**
 * Команда получение файла с заданиями на сложение и вычитание
 */
public class PlusMinusCommand extends OperationCommand {

    public PlusMinusCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        //формируем имя пользователя - поскольку userName может быть не заполнено, для этого случая используем имя и фамилию пользователя
        String userName = (user.getUserName() != null) ? user.getUserName() :
                String.format("%s %s", user.getLastName(), user.getFirstName());
        //обращаемся к методу суперкласса для формирования файла на сложение и вычитание (за это отвечает метод getPlusMinus() перечисления OperationEnum) и отправки его пользователю
        sendAnswer(absSender, chat.getId(), OperationEnum.getPlusMinus(), this.getDescription(), this.getCommandIdentifier(), userName);
    }
}
