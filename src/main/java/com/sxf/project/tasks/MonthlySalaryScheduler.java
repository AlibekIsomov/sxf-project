//package com.sxf.project.tasks;
//
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.jpa.domain.support.AuditingEntityListener;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import javax.persistence.EntityListeners;
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//@Component
//@EntityListeners(AuditingEntityListener.class)
//public class MonthlySalaryScheduler {
//
//    @Autowired
//     WorkerService workerService;
//
//
//
//    // Schedule to run on the first day of every month at 1:00 AM
//    @Scheduled(cron = "0 0 0 1 * ?")
//    public void generateMonthlySalariesForAllWorkers() {
//        try {
//            MonthlySalaryDTO data = new MonthlySalaryDTO();
//            // Generate MonthlySalaryDTO for all existing workers
//            workerService.createForSchedule(data);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
