package kh.edu.istad.platform.iampersistence;

import liquibase.changelog.ChangeLogParameters;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.parser.core.yaml.YamlChangeLogParser;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LiquibaseChangeLogTest {

    @Test
    void masterChangeLogIncludesEveryMigration() throws Exception {
        DatabaseChangeLog changeLog = new YamlChangeLogParser().parse(
                "db/changelog/iam-master.yaml",
                new ChangeLogParameters(),
                new ClassLoaderResourceAccessor()
        );

        Set<String> ids = changeLog.getChangeSets()
                .stream()
                .map(changeSet -> changeSet.getId())
                .collect(Collectors.toSet());

        assertTrue(ids.contains("001-create-iam-tables"),
                "Base DDL changeset is missing: " + ids);

        assertTrue(ids.stream().anyMatch(id -> id.startsWith("002-")),
                "Schema alignment changesets are missing: " + ids);

        assertTrue(ids.stream().anyMatch(id -> id.startsWith("003-")),
                "Seed data changesets are missing: " + ids);
    }
}
