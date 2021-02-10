package ru.muzonik.telegram.bot.bot.noncommand;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Пользовательские настройки
 */
@Getter
@EqualsAndHashCode
public class Settings {

    /**
     * Минимальное число, используемое в заданиях
     */
    private int min;

    /**
     * Максимальное число, используемое в заданиях
     */
    private int max;

    /**
     * Количество страниц выгружаемого файла
     */
    private int listCount;

    /**
     * Количество уникальных задач, которыне можно сформировать с использованием интервала чисел от min до max
     */
    @EqualsAndHashCode.Exclude
    private int uniqueTaskCount;

    public Settings(int min, int max, int listCount) {
        this.min = SettingsAssistant.calculateMin(min, max);
        this.max = SettingsAssistant.calculateMax(min, max);
        this.listCount = SettingsAssistant.calculateListCount(listCount);
        this.uniqueTaskCount = SettingsAssistant.calculateUniqueTaskCount(this.min, this.max);
    }
}
