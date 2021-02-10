package ru.muzonik.telegram.bot.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Операции, использующиеся в заданиях
 */
public enum OperationEnum {
    ADDITION,//сложение
    SUBTRACTION;//вычитание

    public static List<OperationEnum> getPlusMinus() {
        return Arrays.asList(values());
    }
}
