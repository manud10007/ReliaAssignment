package com.reliaquest.api.service;

import com.reliaquest.api.model.EmployeeDTO;

import java.util.List;

public interface EmployeeService {
    List<EmployeeDTO> getAllEmployees();
    List<EmployeeDTO> getEmployeesByNameSearch(String nameFragment);
    EmployeeDTO getEmployeeById(String id);
    Integer getHighestSalaryOfEmployees();
    List<String> getTop10HighestEarningEmployeeNames();
    EmployeeDTO createEmployee(EmployeeDTO employee);
    String deleteEmployeeById(String name);
}
