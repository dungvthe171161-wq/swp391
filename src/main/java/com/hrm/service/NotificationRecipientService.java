package com.hrm.service;

import com.hrm.dao.SystemUserDAO;
import java.util.List;

public class NotificationRecipientService {

    private static final String DEPT_MANAGER_ROLE = "Dept Manager";

    private final RecipientRepository repository;

    public NotificationRecipientService() {
        this(new SystemUserRecipientRepository(new SystemUserDAO()));
    }

    public NotificationRecipientService(RecipientRepository repository) {
        this.repository = repository;
    }

    public List<Integer> activeUsersByRole(String roleName) {
        if (roleName == null || roleName.trim().isEmpty()) {
            return List.of();
        }
        return repository.findActiveUserIdsByRoleName(roleName.trim());
    }

    public List<Integer> deptManagersByDepartment(int departmentId) {
        if (departmentId <= 0) {
            return List.of();
        }
        return repository.findDeptManagerUserIdsByDepartmentId(departmentId);
    }

    public List<Integer> hrStaffUsers() {
        return activeUsersByRole("HR Staff");
    }

    public List<Integer> hrManagerUsers() {
        return activeUsersByRole("HR Manager");
    }

    public List<Integer> adminUsers() {
        return activeUsersByRole("Admin");
    }

    public Integer activeUserByEmployeeId(int employeeId) {
        return employeeId <= 0 ? null : repository.findActiveUserIdByEmployeeId(employeeId);
    }

    public List<Integer> activeUsersByEmployeeIds(List<Integer> employeeIds) {
        if (employeeIds == null || employeeIds.isEmpty()) {
            return List.of();
        }
        List<Integer> userIds = new java.util.ArrayList<>();
        for (Integer employeeId : employeeIds) {
            if (employeeId == null || employeeId <= 0) {
                continue;
            }
            Integer userId = repository.findActiveUserIdByEmployeeId(employeeId);
            if (userId != null && userId > 0) {
                userIds.add(userId);
            }
        }
        return userIds;
    }

    public interface RecipientRepository {
        List<Integer> findActiveUserIdsByRoleName(String roleName);

        List<Integer> findDeptManagerUserIdsByDepartmentId(int departmentId);

        Integer findActiveUserIdByEmployeeId(int employeeId);
    }

    private static class SystemUserRecipientRepository implements RecipientRepository {
        private final SystemUserDAO systemUserDAO;

        SystemUserRecipientRepository(SystemUserDAO systemUserDAO) {
            this.systemUserDAO = systemUserDAO;
        }

        @Override
        public List<Integer> findActiveUserIdsByRoleName(String roleName) {
            return systemUserDAO.findActiveUserIdsByRoleName(roleName);
        }

        @Override
        public List<Integer> findDeptManagerUserIdsByDepartmentId(int departmentId) {
            return systemUserDAO.findActiveUserIdsByRoleAndDepartment(DEPT_MANAGER_ROLE, departmentId);
        }

        @Override
        public Integer findActiveUserIdByEmployeeId(int employeeId) {
            return systemUserDAO.findActiveUserIdByEmployeeId(employeeId);
        }
    }
}
