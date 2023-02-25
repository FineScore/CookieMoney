package com.finescore.moneycookie.repository;

import com.finescore.moneycookie.models.MemberInfo;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.Map;

public class MemberRepository {
    private final NamedParameterJdbcTemplate template;

    public MemberRepository(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }

    public void save(MemberInfo member) {
        String sql = "insert into users (username, password, create_date) " +
                "values (:username, :password, :createDate)";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("username", member.getUsername())
                .addValue("password", member.getPassword())
                .addValue("createDate", LocalDateTime.now());

        template.update(sql, param);
    }

    public MemberInfo findByUsername(String username) {
        String sql = "select username, password from users where username = :username";

        Map<String, String> param = Map.of("username", username);

        try {
            return template.queryForObject(sql, param, memberRowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "미등록된 회원");
        }
    }

    public Boolean checkUsername(String username) {
        String sql = "select username from users where username = :username";

        Map<String, String> param = Map.of("username", username);

        try {
            template.queryForObject(sql, param, memberRowMapper());
            return false;
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.OK, "사용 가능한 아이디입니다.");
        }
    }

    public void update(String username, String newPassword) {
        String sql = "update users set password = :password where username = :username";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("username", username)
                .addValue("password", newPassword);

        template.update(sql, param);
    }

    public void delete(String username) {
        String sql = "delete from users where username = :username";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("username", username);

        template.update(sql, param);
    }

    private RowMapper<MemberInfo> memberRowMapper() {
        return BeanPropertyRowMapper.newInstance(MemberInfo.class);
    }
}
