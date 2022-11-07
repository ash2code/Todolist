package repository.impl;

import mapper.ProjectItemMapper;
import model.ProjectItem;
import org.springframework.jdbc.core.JdbcTemplate;
import repository.ProjectItemDao;

import java.util.List;
import java.util.Optional;

public class ProjectItemDaoImpl implements ProjectItemDao {
    private static final String SQL_CREATE_PROJECT_ITEM = "insert into project_items(projectId, title, is_completed) values (?, ?, ?);";
    private static final String SQL_DELETE_PROJECT_ITEM = "delete from project_items where id = ?";
    private static final String SQL_SELECT_BY_ID = "select * from project_items where id = ? limit 1";
    private static final String SQL_SELECT_BY_PROJECT_ID = "select * from project_items where projectId = ?";

    private static final String SQL_UPDATE_PROJECT_ITEM_TITLE = "update project_items set title = ? where id=?;";
    private static final String SQL_UPDATE_PROJECT_ITEM_IS_COMPLETED = "update project_items set is_completed = ? where id=?;";


    private final ProjectItemMapper projectItemMapper;
    private final JdbcTemplate jdbcTemplate;

    public ProjectItemDaoImpl(ProjectItemMapper projectItemMapper, JdbcTemplate jdbcTemplate) {
        this.projectItemMapper = projectItemMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void insert(ProjectItem item) {
        // projectId, title, is_completed
        jdbcTemplate.update(SQL_CREATE_PROJECT_ITEM, item.getProjectId(), item.getTitle(), item.isCompleted());
    }

    @Override
    public void delete(ProjectItem item) {
        jdbcTemplate.update(SQL_DELETE_PROJECT_ITEM, item.getId());
    }

    @Override
    public void update(ProjectItem item) {
        ProjectItem notUpdated = findProjectItem(item.getId()).get();
        if (item.isCompleted() != notUpdated.isCompleted()) {
            jdbcTemplate.update(SQL_UPDATE_PROJECT_ITEM_IS_COMPLETED, item.isCompleted(), item.getId());
        }
        if (!item.getTitle().equals(notUpdated.getTitle())) {
            jdbcTemplate.update(SQL_UPDATE_PROJECT_ITEM_TITLE, item.getTitle(), item.getId());
        }
    }

    @Override
    public Optional<List<ProjectItem>> findProjectsLinkedToProject(long projectId) {
        List<ProjectItem> res = jdbcTemplate.query(SQL_SELECT_BY_PROJECT_ID, projectItemMapper, projectId);
        return Optional.of(res);
    }

    @Override
    public Optional<ProjectItem> findProjectItem(long projectItemId) {
        ProjectItem res = jdbcTemplate.queryForObject(SQL_SELECT_BY_ID, projectItemMapper, projectItemId);
        return Optional.ofNullable(res);
    }
}
