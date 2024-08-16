package entities;

//create table employee (name varchar(20),
//id bigint auto_increment,
//role_id bigint,
//doj date,
//dob date,
//gender ENUM('male', 'female', 'others'),
//reporting_to bigint,
//dept_id bigint,
//leave_available int,
//foreign key (reporting_to) references employee(id),
//foreign key (dept_id) references dept(id),
//foreign key (role_id) references role(id),
//primary key (id)
//);

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class Employee{

    private String name;
    private BigDecimal id;
    @SerializedName("role_id")
    private BigDecimal roleId;
    private String doj;
    private String dob;
    private String gender;
    @SerializedName("reporting_to")
    private BigDecimal reportingTo;
    @SerializedName("dept_id")
    private BigDecimal deptId;
    @SerializedName("leave_available")
    private int leaveAvailable;

    public Employee(String name, BigDecimal id, BigDecimal roleId, String doj, String dob, String gender, BigDecimal reportingTo, BigDecimal deptId, int leaveAvailable) {
        this.name = name;
        this.id = id;
        this.roleId = roleId;
        this.doj = doj;
        this.dob = dob;
        this.gender = gender;
        this.reportingTo = reportingTo;
        this.deptId = deptId;
        this.leaveAvailable = leaveAvailable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public BigDecimal getRoleId() {
        return roleId;
    }

    public void setRoleId(BigDecimal roleId) {
        this.roleId = roleId;
    }

    public String getDoj() {
        return doj;
    }

    public void setDoj(String doj) {
        this.doj = doj;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender){
        this.gender = gender;
    }

    public BigDecimal getReportingTo() {
        return reportingTo;
    }

    public void setReportingTo(BigDecimal reportingTo) {
        this.reportingTo = reportingTo;
    }

    public BigDecimal getDeptId() {
        return deptId;
    }

    public void setDeptId(BigDecimal deptId) {
        this.deptId = deptId;
    }

    public void setLeaveAvailable(int leaveAvailable) {
        this.leaveAvailable = leaveAvailable;
    }

    public int getLeaveAvailable() {
        return leaveAvailable;
    }

}
