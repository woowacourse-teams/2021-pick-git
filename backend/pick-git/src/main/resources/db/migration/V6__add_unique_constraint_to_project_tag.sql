alter table project_tag
    add constraint uk_project_tag_tag_project unique (tag_id, project_id);
