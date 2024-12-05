package com.reliaquest.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.exceptions.EmployeeApiException;
import com.reliaquest.api.exceptions.EmployeeNotFoundException;
import com.reliaquest.api.model.EmployeeDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {
    private final RestTemplate restTemplate;
    private static final String BASE_URL = "http://localhost:8112/api/v1/employee";

    @Autowired
    public EmployeeServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<EmployeeDTO> getAllEmployees() {
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    BASE_URL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            List<EmployeeDTO> employees = extractEmployeesFromResponse(response);
            log.info("Retrieved {} employees", employees.size());
            return employees;
        } catch (RestClientException e) {
            log.error("Error retrieving employees", e);
            throw new EmployeeApiException("Failed to retrieve employees", e);
        }
    }

    @Override
    public List<EmployeeDTO> getEmployeesByNameSearch(String nameFragment) {
        // Validate input
        if (nameFragment == null || nameFragment.trim().isEmpty()) {
            throw new IllegalArgumentException("Name fragment cannot be null or empty");
        }
        return getAllEmployees().stream()
                .filter(emp -> emp.getEmployeeName().toLowerCase().contains(nameFragment.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeDTO getEmployeeById(String id) {
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    BASE_URL + "/{id}",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {},
                    id
            );

            EmployeeDTO employee = extractSingleEmployeeFromResponse(response);
            log.info("Retrieved employee with ID: {}", id);
            return employee;
        } catch (RestClientException e) {
            log.error("Error retrieving employee with ID: {}", id, e);
            throw new EmployeeNotFoundException("Employee not found with ID: " + id);
        }
    }

    @Override
    public Integer getHighestSalaryOfEmployees() {
        return getAllEmployees().stream()
                .mapToInt(EmployeeDTO::getEmployeeSalary)
                .max()
                .orElse(0);
    }

    @Override
    public List<String> getTop10HighestEarningEmployeeNames() {
        return getAllEmployees().stream()
                .sorted(Comparator.comparing(EmployeeDTO::getEmployeeSalary).reversed())
                .limit(10)
                .map(EmployeeDTO::getEmployeeName)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeDTO createEmployee(EmployeeDTO employee) {
        // Validate employee creation
        validateEmployeeCreation(employee);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    BASE_URL,
                    HttpMethod.POST,
                    new HttpEntity<>(employee),
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            EmployeeDTO createdEmployee = extractSingleEmployeeFromResponse(response);
            log.info("Created employee: {}", createdEmployee.getEmployeeName());
            return createdEmployee;
        } catch (RestClientException e) {
            log.error("Error creating employee", e);
            throw new EmployeeApiException("Failed to create employee", e);
        }
    }

    @Override
    public String deleteEmployeeById(String name) {
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    BASE_URL + "/{name}",
                    HttpMethod.DELETE,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {},
                    name
            );

            log.info("Deleted employee: {}", name);
            return name;
        } catch (RestClientException e) {
            log.error("Error deleting employee: {}", name, e);
            throw new EmployeeNotFoundException("Employee not found: " + name);
        }
    }

    private List<EmployeeDTO> extractEmployeesFromResponse(ResponseEntity<Map<String, Object>> response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            // Extract the 'data' field from the response
            Object dataObj = response.getBody().get("data");

            // Convert the data to a list of EmployeeDTO
            return objectMapper.convertValue(dataObj, new TypeReference<List<EmployeeDTO>>() {});
        } catch (IllegalArgumentException | NullPointerException e) {
            log.error("Error extracting employees from response", e);
            throw new EmployeeApiException("Failed to parse employee data", e);
        }
    }

    private EmployeeDTO extractSingleEmployeeFromResponse(ResponseEntity<Map<String, Object>> response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            // Extract the 'data' field from the response
            Object dataObj = response.getBody().get("data");

            // Convert the data to an EmployeeDTO
            return objectMapper.convertValue(dataObj, EmployeeDTO.class);
        } catch (IllegalArgumentException | NullPointerException e) {
            log.error("Error extracting single employee from response", e);
            throw new EmployeeApiException("Failed to parse employee data", e);
        }
    }

    private void validateEmployeeCreation(EmployeeDTO employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }

        if (employee.getEmployeeName() == null || employee.getEmployeeName().trim().isEmpty()) {
            throw new IllegalArgumentException("Employee name is required");
        }

        if (employee.getEmployeeSalary() == null || employee.getEmployeeSalary() <= 0) {
            throw new IllegalArgumentException("Salary must be greater than zero");
        }

        if (employee.getEmployeeAge() == null || employee.getEmployeeAge() < 16 || employee.getEmployeeAge() > 75) {
            throw new IllegalArgumentException("Age must be between 16 and 75");
        }

        if (employee.getEmployeeTitle() == null || employee.getEmployeeTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Employee title is required");
        }
    }
}



