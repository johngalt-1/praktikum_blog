package ru.yandex.praktikum.blog.utils;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Utils {

    public static Array toSqlArray(List<String> list, JdbcTemplate jdbcTemplate) throws SQLException {
        return Objects.requireNonNull(jdbcTemplate.getDataSource())
                .getConnection()
                .createArrayOf("text", list.toArray());
    }

    @NonNull
    public static List<String> fromSqlArray(ResultSet rs, String columnLabel) throws SQLException {
        Array sqlArray = rs.getArray(columnLabel);
        if (sqlArray == null) {
            return Collections.emptyList();
        }
        String[] array = (String[]) sqlArray.getArray();
        return Arrays.stream(array).toList();
    }
}
