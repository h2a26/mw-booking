package org.codigo.middleware.mwbooking.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.codigo.middleware.mwbooking.service.ClassService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ClassEndScheduler {

    private final ClassService classService;

    public ClassEndScheduler(ClassService classService) {
        this.classService = classService;
    }

    /**
     * Run daily every hour to be when class end waitlist user credit need to be refunded
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void refundWaitlistUserCreditsWhenClassEnd() {
        log.info("refundWaitlistUserCreditsWhenClassEnd job run.");
        classService.refundWaitlistUserCreditsWhenClassEnd();
    }
}

