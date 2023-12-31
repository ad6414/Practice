package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工
     * @param employeeDTO
     */
    void createEmp(EmployeeDTO employeeDTO);

    /**
     * 分页查询展示
     * @param employeePageQueryDTO
     * @return
     */
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 员工状态修改
     * @param id
     * @param status
     */
    void status(Long id, Integer status);

    /**
     * 员工信息修改
     * @param employeeDTO
     */
    void modify(EmployeeDTO employeeDTO);

    /**
     * 根据ID查询员工
     * @param id
     * @return
     */
    Employee getById(Integer id);
}
