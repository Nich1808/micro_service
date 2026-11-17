package kh.edu.istad.platform.iaminfrastructure.admin;

import kh.edu.istad.platform.iamapplicationservice.port.out.AdminStatisticsQuery;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Aggregates the overview counters with a handful of cheap SQL queries instead of loading
 * entities, so the dashboard stays fast as the data grows.
 */
@Component
public class AdminStatisticsAdapter implements AdminStatisticsQuery {

    private static final int RECENT_EVENT_LIMIT = 8;

    private final JdbcTemplate jdbcTemplate;

    public AdminStatisticsAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional(readOnly = true)
    public Snapshot snapshot() {
        OffsetDateTime startOfDay = LocalDate.now()
                .atStartOfDay()
                .atOffset(ZoneOffset.UTC);

        return new Snapshot(
                count("select count(*) from iam_users where is_deleted = false"),
                count("select count(*) from iam_users "
                        + "where is_deleted = false and enabled = true "
                        + "and account_non_locked = true"),
                count("select count(*) from iam_users "
                        + "where is_deleted = false and account_non_locked = false"),
                count("select count(*) from iam_users "
                        + "where is_deleted = false and enabled = false"),
                count("select count(*) from iam_roles where is_deleted = false"),
                count("select count(*) from iam_permissions where is_deleted = false"),
                count("select count(*) from oauth2_registered_client"),
                count("select count(*) from iam_user_sessions where revoked_at is null"),
                count("select count(*) from iam_login_attempts where attempted_at >= ?",
                        Timestamp.from(startOfDay.toInstant())),
                count("select count(*) from iam_login_attempts "
                        + "where attempted_at >= ? and successful = false",
                        Timestamp.from(startOfDay.toInstant())),
                recentEvents()
        );
    }

    private long count(String sql, Object... args) {
        Long value = jdbcTemplate.queryForObject(sql, Long.class, args);
        return value == null ? 0L : value;
    }

    private List<RecentEvent> recentEvents() {
        return jdbcTemplate.query(
                """
                        select type, description, actor, created_at
                        from iam_account_activities
                        order by created_at desc
                        limit ?
                        """,
                (rs, rowNum) -> new RecentEvent(
                        rs.getString("type"),
                        rs.getString("description"),
                        rs.getString("actor"),
                        rs.getTimestamp("created_at").toInstant()
                                .atOffset(ZoneOffset.UTC)
                ),
                RECENT_EVENT_LIMIT
        );
    }
}
