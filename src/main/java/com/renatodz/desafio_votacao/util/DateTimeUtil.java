package com.renatodz.desafio_votacao.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateTimeUtil {

    // Converte LocalDateTime para UTC
    public static LocalDateTime toUTC(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
    }

    // Converte LocalDateTime de UTC para o fuso horário local
    public static LocalDateTime fromUTC(LocalDateTime utcDateTime, ZoneId zoneId) {
        return utcDateTime.atZone(ZoneId.of("UTC")).withZoneSameInstant(zoneId).toLocalDateTime();
    }
}