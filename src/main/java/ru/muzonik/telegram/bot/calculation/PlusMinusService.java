package ru.muzonik.telegram.bot.calculation;

import lombok.AllArgsConstructor;
import ru.muzonik.telegram.bot.enums.OperationEnum;
import ru.muzonik.telegram.bot.fileprocessor.WordFileProcessorImpl;
import ru.muzonik.telegram.bot.bot.noncommand.Settings;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Сервис генерации заданий на сложение и/или вычитание
 */
@AllArgsConstructor
public class PlusMinusService {
    private final WordFileProcessorImpl fileProcessor;
    private final Calculator calculator;

    /**
     * Формирование файла с заданиями на сложение и/или вычитание
     *
     * @param operations список типов операций (сложение и/или вычитание)
     * @param settings   настройки выгружаемого файла
     */
    public FileInputStream getPlusMinusFile(List<OperationEnum> operations, Settings settings)
            throws IOException {
        List<String> taskList = new ArrayList<>();
        //количество страниц итогового документа. Если будет потребность пользователей, можно перенести в параметры,
        // определяемые пользователем
        for (int i = 1; i <= settings.getListCount(); i++) {
            taskList.addAll(getTaskList(operations, settings));
        }
        if (taskList.isEmpty()) {
            throw new IllegalArgumentException(String.format("По непонятным причинам по заданным настройкам " +
                            "(min = %s, max = %s, listCount = %s, uniqueTaskCount =%s) не удалось создать ни одной строки " +
                            "с задачами", settings.getMin(), settings.getMax(), settings.getListCount(),
                    settings.getUniqueTaskCount()));
        }
        return fileProcessor.createWordFile(taskList);
    }

    /**
     * Формирование списка задач
     *
     * @param operations типы операций (например, сложение и вычитание)
     * @param settings   настройки выгружаемого файла
     */
    private List<String> getTaskList(List<OperationEnum> operations, Settings settings) {
        int taskCount = getOperationTaskCount(operations.size());
        List<String> taskList = new ArrayList<>();
        for (OperationEnum operation : operations) {
            fillTaskList(taskList, operation, taskCount, settings.getMin(), settings.getMax(),
                    settings.getUniqueTaskCount());
        }
        Collections.shuffle(taskList);
        return taskList;
    }

    /**
     * Наполнение списка задач. В случае, если уникальных задач хватает для наполнения листа, используются только
     * уникальные задачи. В противном случае задания повторяются
     *
     * @param taskList        список задач
     * @param operation       тип операции (сложение или вычитание)
     * @param taskCount       требуемое количество задач в списке
     * @param min             минимальное значение
     * @param max             максимальное значение
     * @param uniqueTaskCount число уникальных задач, которыне можно сформировать с использованием интервала чисел
     *                        от min до max
     */
    private void fillTaskList(List<String> taskList, OperationEnum operation, int taskCount, int min, int max,
                              int uniqueTaskCount) {
        if (taskCount <= uniqueTaskCount) {
            taskList.addAll(calculator.getTaskSet(operation, min, max, taskCount));
        } else {
            taskList.addAll(calculator.getTaskSet(operation, min, max, uniqueTaskCount));
            int remainingNumbers = taskCount - uniqueTaskCount;
            fillTaskList(taskList, operation, remainingNumbers, min, max, uniqueTaskCount);
        }
    }

    /**
     * Расчёт количества задач для каждой операции. Если запрашивается больше 2 операций - ошибка
     *
     * @param operationsCount количество операций
     */
    private int getOperationTaskCount(int operationsCount) {
        //количество строк, вмещающихся на одну страницу А4 в горизонтальной ориентации при выбранных размере и
        //типе шрифта (см. метод setRunParameters класса WordFileProcessorImpl)
        int linesCount = 52;
        if (operationsCount == 1) {
            return linesCount;
        } else if (operationsCount == 2) {
            return linesCount / 2;
        } else {
            throw new IllegalArgumentException("Количество операций для формирования файла с заданиями на " +
                    "сложение-вычитание больше 2");
        }
    }
}
