//package com.sxf.project.response;
//
//
//import com.sxf.project.entity.User;
//import com.sxf.project.entity.Worker;
//import com.sxf.project.repository.UserRepository;
//import com.sxf.project.repository.WorkerRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.PermissionEvaluator;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//
//import java.io.Serializable;
//
//@Component
//public class CustomPermissionEvaluator implements PermissionEvaluator {
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private WorkerRepository workerRepository;
//
//    @Override
//    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
//        // Not used
//        return false;
//    }
//
//    @Override
//    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
//        if (authentication == null) {
//            return false;
//        }
//        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
//        if (targetType.equals("Filial")) {
//            return isFilialAccessible(username, (Long) targetId);
//        } else if (targetType.equals("Worker")) {
//            return isWorkerAccessible(username, (Long) targetId);
//        }
//        return false;
//    }
//
//    public boolean isFilialAccessible(Authentication authentication, Long filialId) {
//        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
//        User user = userRepository.findByUsername(username);
//        return user != null && user.getAssignedFilial() != null && user.getAssignedFilial().getId().equals(filialId);
//    }
//
//    public boolean isWorkerAccessible(Authentication authentication, Long workerId) {
//        Worker worker = workerRepository.findById(workerId).orElse(null);
//        if (worker == null) {
//            return false;
//        }
//        return isFilialAccessible(authentication, worker.getFilial().getId());
//    }
//}
//}
