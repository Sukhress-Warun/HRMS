# HRMS (Human Resource Management System)



## Employee Management

- **Profile Management**: Create and maintain detailed employee records including personal information and joining details.
- **Reporting Hierarchy**: Built-in support for organizational hierarchy, allowing queries for reporting managers and subordinates.
- **Dynamic Search & Sort**: APIs support filtering employees by name and sorting by various attributes like Date of Joining (DOJ) or Name.

## Attendance Tracking

- **Daily Check-In/Check-Out**: Precise tracking of work hours with daily check-in and check-out functionality.
- **Activity Logging**: All attendance actions are logged to maintain an audit trail of employee timings.
- **Status Reporting**: APIs to fetch the current day's status or historical attendance records.

## Leave & Holiday Management

- **Leave Application**: Employees can apply for leaves for specific dates.
- **Leave Cancellation**: functionality to cancel previously applied leaves.
- **Holiday Calender**: System manages a list of company holidays.
- **Unified Calendar View**: A comprehensive calendar feature that aggregates attendance history, leave records, and holidays into a single view for the employee.

## Operational Constraints & Rules

The system enforces strict business rules to ensure data accuracy:

- **Attendance Validation**:
    - Users cannot check-in or check-out on marked holidays.
    - Users cannot check-in or check-out on days they have applied for leave.
- **Conflict Prevention**: 
    - Prevents overlapping leave applications.
    - Ensures "Check-Out" can only be performed after a valid "Check-In".
- **Data Integrity**: 
    - Validates date formats and logical sequences (e.g., *To Date* cannot be before *From Date*).
    - Ensures employees cannot perform conflicting actions simultaneously.


## Tech Stack

- **Language**: Java 11
- **Build Tool**: Maven
- **Web Framework**: Java Servlet API 4.0
- **Database**: MySQL 8.1
- **JSON Processing**: Gson, Org.Json

## Setup & Installation

### Prerequisites

- Java JDK 11 or higher
- Maven 3.6+
- MySQL Server

### Database Setup

1.  Create a MySQL database named `employee`.
2.  The application expects the following tables (based on the code):
    - `employee`
    - `attendance`
    - `log`
    - `holiday`

3.  The database details can be configured in `src/main/java/database/DatabaseConnection.java`.

### Build and Run

1.  Clone the repository:
    ```bash
    git clone <repository-url>
    cd HRMS
    ```

2.  Build the project using Maven:
    ```bash
    mvn clean install
    ```

3.  This generates a WAR file in the `target/` directory. You can deploy this WAR file to any Servlet Container like **Apache Tomcat** or **Jetty**.

## API Endpoints

The application exposes the following RESTful endpoints. All responses are in JSON format.

### Employee Management (`/employee/*`)

- **GET** `/employee` - List all employees (Supports pagination, sorting, and searching).
    - Params: `page`, `per_page`, `sort_column`, `sort_order`, `search_name`
- **GET** `/employee/{id}` - Get details of a specific employee.
- **GET** `/employee/higher?id={id}` - Get reporting manager(s) for an employee.
- **POST** `/employee` - Add a new employee.
- **PUT** `/employee/{id}` - Update an existing employee.
- **DELETE** `/employee/{id}` - Delete an employee.

### Attendance Management (`/attendance/*`)

- **POST** `/attendance/check-in` - Record employee check-in.
    - Payload: `{ "id": 123, "date": "YYYY-MM-DD", "time": "HH:MM:SS" }`
- **POST** `/attendance/check-out` - Record employee check-out.
    - Payload: `{ "id": 123, "date": "YYYY-MM-DD", "time": "HH:MM:SS" }`
- **POST** `/attendance/apply-leave` - Apply for leave.
- **POST** `/attendance/cancel-leave` - Cancel applied leave.
- **GET** `/attendance/status` - Get attendance status for a specific date.
    - Params: `id`, `date`
- **GET** `/attendance/calendar` - Get attendance calendar between dates.
    - Params: `id`, `from_date`, `to_date`

### Holiday Management (`/holiday/*`)

- **GET** `/holiday` - List all holidays (Supports pagination and sorting).
- **GET** `/holiday/{id}` - Get details of a specific holiday.
- **POST** `/holiday` - Add a new holiday.
- **PUT** `/holiday/{id}` - Update a holiday.
- **DELETE** `/holiday/{id}` - Delete a holiday.
