package com.ouc.aamanagement.utils;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateUtils {

    private static final DateTimeFormatter BIRTH_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM. dd yyyy", Locale.ENGLISH);

    private static final DateTimeFormatter ADMISSION_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);

    public static String formatBirthDate(String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        return date.format(BIRTH_DATE_FORMAT);
    }

    public static String formatAdmissionDate(String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        return date.format(ADMISSION_DATE_FORMAT);
    }

    public static String getCurrentDate() {
        return LocalDate.now().format(BIRTH_DATE_FORMAT);
    }
}