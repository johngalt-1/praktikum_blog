package ru.yandex.praktikum.blog.utils;

import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Utils {

//    public static Timestamp toSqlTimestamp(OffsetDateTime dateTime) {
//        return Timestamp.from(dateTime.toInstant());
//    }
//
//    public static OffsetDateTime fromSqlTimestamp(Timestamp timestamp) {
//        return timestamp.toInstant()
//                .atZone(ZoneId.systemDefault())
//                .toOffsetDateTime();
//    }
//
//    public static ZoneOffset ZONE_OFFSET = ZoneOffset.of(ZoneId.systemDefault().getId());

    public static Array toSqlArray(List<String> list, JdbcTemplate jdbcTemplate) throws SQLException {
        return Objects.requireNonNull(jdbcTemplate.getDataSource())
                .getConnection()
                .createArrayOf("text", list.toArray());
    }

    public static List<String> fromSqlArray(ResultSet rs, String columnLabel) throws SQLException {
        Array sqlArray = rs.getArray(columnLabel);
        if (sqlArray == null) {
            return null;
        }
        String[] array = (String[]) sqlArray.getArray();
        return Arrays.stream(array).toList();
    }
}
