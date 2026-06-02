package org.Job.command.event;

import org.Job.command.data.JobCategory;
import org.Job.command.data.JobCategoryRepository;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class JobCategoryEventHandler {

    @Autowired
    private JobCategoryRepository jobCategoryRepository;

    @EventHandler
    @Transactional
    public void on(JobCategoryCreatedEvent event) {
        JobCategory category = JobCategory.builder()
                .id(event.getId())
                .name(event.getName())
                .slug(event.getSlug())
                .description(event.getDescription())
                .active(event.getActive())
                .build();
        jobCategoryRepository.save(category);
    }

    @EventHandler
    @Transactional
    public void on(JobCategoryUpdatedEvent event) {
        JobCategory category = jobCategoryRepository.findById(event.getId())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Không tìm thấy danh mục công việc"));

        if (event.getName() != null) category.setName(event.getName());
        if (event.getSlug() != null) category.setSlug(event.getSlug());
        if (event.getDescription() != null) category.setDescription(event.getDescription());
        if (event.getActive() != null) category.setActive(event.getActive());

        jobCategoryRepository.save(category);
    }
}

