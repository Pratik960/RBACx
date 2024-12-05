import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import apiClient from "./APIs/ApiClient";
import toast from "react-hot-toast";
import * as styles from "./AdminDashboard.module.css";

const AdminDashboard = () => {
  const navigate = useNavigate();

  // States for pagination, data and form validation
  const [users, setUsers] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [userPage, setUserPage] = useState(1);
  const [employeePage, setEmployeePage] = useState(1);
  const [totalUsersPages, setTotalUsersPages] = useState(1);
  const [totalEmployeesPages, setTotalEmployeesPages] = useState(1);
  const [taskTitle, setTaskTitle] = useState("");
  const [taskDescription, setTaskDescription] = useState("");
  const [errors, setErrors] = useState({});

  // Fetch data for users and employees
  useEffect(() => {
    fetchUsers(userPage);
    fetchEmployees(employeePage);
  }, [userPage, employeePage]);

  const fetchUsers = async (page) => {
    try {
      const response = await apiClient.get(`/api/user/allUsers`, {
        params: {
          page,
          perPage: 5,
          search: "",
          sort: "ASC",
          status: "ACTIVE",
          role: "ROLE_USER",
          pageable: true,
        },
      });
      setUsers(response.data.data || []);
      setTotalUsersPages(response.data.totalPage || 1);
    } catch (error) {
      toast.error("Failed to fetch users");
    }
  };

  const fetchEmployees = async (page) => {
    try {
      const response = await apiClient.get(`/api/user/allUsers`, {
        params: {
          page,
          perPage: 5,
          search: "",
          sort: "ASC",
          status: "ACTIVE",
          role: "ROLE_EMP",
          pageable: true,
        },
      });
      setEmployees(response.data.data || []);
      setTotalEmployeesPages(response.data.totalPage || 1);
    } catch (error) {
      toast.error("Failed to fetch employees");
    }
  };

  // Handle delete user
  const handleDeleteUser = async (userId) => {
    try {
      const response = await apiClient.put(
        `/api/admin/deleteAccount/${userId}`,
        {}
      );
      setUsers(users.filter((user) => user.userId !== userId));
      setEmployees(employees.filter((emp) => emp.userId != userId));
      toast.success("User deleted successfully");
    } catch (error) {
      toast.error("Failed to delete user");
    }
  };

  // Handle task creation
  const handleCreateTask = async (e) => {
    e.preventDefault();
    const validationErrors = {};

    if (!taskTitle) {
      validationErrors.title = "Title is required.";
    }

    if (!taskDescription) {
      validationErrors.description = "Description is required.";
    }

    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      return;
    }

    try {
      const response = await apiClient.post(`/api/admin/add/task`, {
        title: taskTitle,
        description: taskDescription,
      });
      toast.success("Task created successfully");
      setTaskTitle("");
      setTaskDescription("");
    } catch (error) {
      toast.error("Failed to create task");
    }
  };

  // Handle logout
  const handleLogout = () => {
    localStorage.removeItem("authToken");
    localStorage.removeItem("refreshToken");
    localStorage.removeItem("userRole");
    localStorage.removeItem("userId");
    navigate("/login");
  };

  return (
    <div className={styles.dashboardContainer}>
      <header className={styles.header}>
        <h1>Admin Dashboard</h1>
        <button className={styles.logoutButton} onClick={handleLogout}>
          Logout
        </button>
      </header>

      <div className={styles.mainContent}>
        {/* User Section */}
        <section className={styles.section}>
          <h2>Users</h2>
          <div className={styles.tableContainer}>
            <div className={styles.tableHeader}>
              <div className={styles.tableCell}>Name</div>
              <div className={styles.tableCell}>Email</div>
              <div className={styles.tableCell}>Action</div>
            </div>
            {users && users.length > 0 ? (
              users.map((user) => (
                <div key={user.userId} className={styles.tableRow}>
                  <div className={styles.tableCell}>
                    {user.firstName} {user.lastName}
                  </div>
                  <div className={styles.tableCell}>{user.email}</div>
                  <div className={styles.tableCell}>
                    <button
                      className={styles.actionButton}
                      onClick={() => handleDeleteUser(user.userId)}
                    >
                      Delete
                    </button>
                  </div>
                </div>
              ))
            ) : (
              <p>No users found.</p>
            )}
          </div>
        </section>

        {/* Employee Section */}
        <section className={styles.section}>
          <h2>Employees</h2>
          <div className={styles.tableContainer}>
            <div className={styles.tableHeader}>
              <div className={styles.tableCell}>Name</div>
              <div className={styles.tableCell}>Email</div>
              <div className={styles.tableCell}>Action</div>
            </div>
            {employees && employees.length > 0 ? (
              employees.map((employee) => (
                <div key={employee.userId} className={styles.tableRow}>
                  <div className={styles.tableCell}>
                    {employee.firstName} {employee.lastName}
                  </div>
                  <div className={styles.tableCell}>{employee.email}</div>
                  <div className={styles.tableCell}>
                    <button
                      className={styles.actionButton}
                      onClick={() => handleDeleteUser(employee.userId)}
                    >
                      Delete
                    </button>
                  </div>
                </div>
              ))
            ) : (
              <p>No employees found.</p>
            )}
          </div>
        </section>

        {/* Task Creation Form */}
        <section className={styles.section}>
          <h2>Create Task</h2>
          <form onSubmit={handleCreateTask}>
            <div className={styles.formGroup}>
              <label htmlFor="taskTitle">Task Title</label>
              <input
                id="taskTitle"
                type="text"
                value={taskTitle}
                onChange={(e) => setTaskTitle(e.target.value)}
                placeholder="Enter the task title"
              />
              {errors.title && <p className={styles.error}>{errors.title}</p>}
            </div>

            <div className={styles.formGroup}>
              <label htmlFor="taskDescription">Task Description</label>
              <textarea
                id="taskDescription"
                value={taskDescription}
                onChange={(e) => setTaskDescription(e.target.value)}
                placeholder="Enter a brief description of the task"
              />
              {errors.description && (
                <p className={styles.error}>{errors.description}</p>
              )}
            </div>

            <button type="submit" className={styles.submitButton}>
              Create Task
            </button>
          </form>
        </section>
      </div>
    </div>
  );
};

export default AdminDashboard;
