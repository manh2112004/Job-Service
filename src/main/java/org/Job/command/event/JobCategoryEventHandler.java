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
}
