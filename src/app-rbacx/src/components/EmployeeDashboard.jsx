import React, { useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import apiClient from "./APIs/ApiClient";
import * as styles from "./EmployeeDashboard.module.css";

const EmployeeDashboard = () => {
  const navigate = useNavigate();

  const [pendingTasks, setPendingTasks] = useState([]);
  const [assignedTasks, setAssignedTasks] = useState([]);
  const [completedTasks, setCompletedTasks] = useState([]);
  const [users, setUsers] = useState([]);
  const [loadingUsers, setLoadingUsers] = useState(false);
  const [userPage, setUserPage] = useState(1);
  const [hasMoreUsers, setHasMoreUsers] = useState(true);

  // Pagination states for tasks
  const [pendingPage, setPendingPage] = useState(1);
  const [assignedPage, setAssignedPage] = useState(1);
  const [completedPage, setCompletedPage] = useState(1);
  const [hasMorePendingTasks, setHasMorePendingTasks] = useState(true);
  const [hasMoreAssignedTasks, setHasMoreAssignedTasks] = useState(true);
  const [hasMoreCompletedTasks, setHasMoreCompletedTasks] = useState(true);

  // Pagination for total pages
  const [totalPagesPending, setTotalPagesPending] = useState(1);
  const [totalPagesAssigned, setTotalPagesAssigned] = useState(1);
  const [totalPagesCompleted, setTotalPagesCompleted] = useState(1);

  // Fetch tasks based on status and pagination
  const fetchTasks = async (status, setState, setHasMore, setTotalPages, page) => {
    try {
      const response = await apiClient.get(
        "/api/user/allTask",
        {
          params: {
            page: page,
            perPage: 5,
            search: "",
            sort: "ASC",
            userId: 0,
            status,
            sortBy: "",
            startDate: "",
            endDate: "",
            pageable: true,
          }
        }
      );

      if (response.data && Array.isArray(response.data.data)) {
        setState(response.data.data);
        setTotalPages(response.data.totalPage); // Save the total pages
        if (response.data.data.length < 5) {
          setHasMore(false); // Stop loading if fewer than 5 tasks were returned
        } else {
          setHasMore(true); // Keep loading if 5 tasks are returned
        }
      } else {
        setState([]);
        setHasMore(false); // No more tasks
      }
    } catch (error) {
      console.error("Error fetching tasks:", error);
      setState([]);
      setHasMore(false);
    }
  };

  // Fetch users for the dropdown with infinite scroll
  const fetchUsers = useCallback(async () => {
    if (loadingUsers || !hasMoreUsers) return;

    try {
      setLoadingUsers(true);
      const response = await apiClient.get(
        `/api/user/allUsers`,
        {
          params: {
            page: userPage,
            perPage: 5,
            search: "",
            sort: "ASC",
            status: "ACTIVE",
            role: "ROLE_USER",
            sortBy: "",
            startDate: "",
            endDate: "",
            pageable: true,
          }
        }
      );

      if (response.data && Array.isArray(response.data.data)) {
        setUsers((prevUsers) => [...prevUsers, ...response.data.data]);
        if (response.data.data.length < 5) {
          setHasMoreUsers(false); // Stop loading if fewer than 5 users were returned
        }
      }
    } catch (error) {
      console.error("Error fetching users:", error);
    } finally {
      setLoadingUsers(false);
    }
  }, [userPage, loadingUsers, hasMoreUsers]);

  useEffect(() => {
    fetchTasks("CREATED", setPendingTasks, setHasMorePendingTasks, setTotalPagesPending, pendingPage);
    fetchTasks("ASSIGNED", setAssignedTasks, setHasMoreAssignedTasks, setTotalPagesAssigned, assignedPage);
    fetchTasks("COMPLETED", setCompletedTasks, setHasMoreCompletedTasks, setTotalPagesCompleted, completedPage);
    fetchUsers(); // Initial fetch of users
  }, [pendingPage, assignedPage, completedPage]);

  const handleLogout = () => {
    localStorage.removeItem("authToken");
    localStorage.removeItem("refreshToken");
    localStorage.removeItem("userRole");
    localStorage.removeItem("userId");
    navigate("/login");
  };

  const handleAssignTask = async (taskId, selectedUser) => {
    if (!selectedUser) return;

    try {
      const token = localStorage.getItem("authToken");
      const user = users.find((user) => user.username === selectedUser);

      if (!user) {
        console.error("User not found");
        return;
      }

      const response = await apiClient.put(
        "/api/emp",
        { taskId, userId: user.userId },
        {
          headers: {
            Authorization: `Bearer ${token}`,
            accept: "*/*",
            "Content-Type": "application/json",
          },
        }
      );

      if (response.data && response.data.status === 200) {
        setPendingTasks((prev) =>
          prev.filter((task) => task.taskId !== taskId)
        );
        fetchTasks("ASSIGNED", setAssignedTasks, setHasMoreAssignedTasks, setTotalPagesAssigned, assignedPage);
      }
    } catch (error) {
      console.error("Error assigning task:", error);
    }
  };

  const handleDropdownChange = (taskId, selectedUser) => {
    setPendingTasks((prevTasks) =>
      prevTasks.map((task) =>
        task.taskId === taskId ? { ...task, selectedUser } : task
      )
    );
  };

  const handleDropdownScroll = (e) => {
    if (loadingUsers || !hasMoreUsers) return;

    const { scrollTop, scrollHeight, clientHeight } = e.target;
    if (scrollHeight - scrollTop === clientHeight) {
      setUserPage((prevPage) => prevPage + 1);
    }
  };

  const renderPagination = (page, setPage, hasMore, tasks, totalPages) => {
    if (tasks.length === 0) return null; // Do not show pagination if no tasks

    return (
      <div className={styles.pagination}>
        <button
          disabled={page === 1}
          onClick={() => setPage((prev) => prev - 1)}
        >
          Previous
        </button>
        <span>
          Page {page} of {totalPages}
        </span>
        <button
          disabled={!hasMore || page === totalPages}
          onClick={() => setPage((prev) => prev + 1)}
        >
          Next
        </button>
      </div>
    );
  };

  return (
    <div className={styles.dashboardContainer}>
      <div className={styles.profileSection}>
        <h2>Employee Dashboard</h2>
        <button className={styles.btnLogout} onClick={handleLogout}>
          Logout
        </button>
      </div>

      {/* Pending Tasks Section */}
      <div className={styles.tasksSection}>
        <h3>Pending Tasks</h3>
        {pendingTasks.length === 0 ? (
          <p>No pending tasks</p>
        ) : (
          <ul>
            {pendingTasks.map((task) => (
              <li key={task.taskId} className={styles.taskItem}>
                <span>{task.title}</span>
                <select
                  value={task.selectedUser || ""}
                  onChange={(e) =>
                    handleDropdownChange(task.taskId, e.target.value)
                  }
                  onScroll={handleDropdownScroll} // Infinite scroll in dropdown
                >
                  <option value="">Select User</option>
                  {users.map((user) => (
                    <option key={user.userId} value={user.username}>
                      {user.username}
                    </option>
                  ))}
                </select>
                <button
                  className={styles.btnAssignTask}
                  onClick={() =>
                    handleAssignTask(task.taskId, task.selectedUser)
                  }
                >
                  Assign
                </button>
              </li>
            ))}
          </ul>
        )}
        {renderPagination(
          pendingPage,
          setPendingPage,
          hasMorePendingTasks,
          pendingTasks,
          totalPagesPending // Pass totalPages
        )}
      </div>

      {/* Assigned Tasks Section */}
      <div className={styles.tasksSection}>
        <h3>Assigned Tasks</h3>
        {assignedTasks.length === 0 ? (
          <p>No assigned tasks</p>
        ) : (
          <ul>
            {assignedTasks.map((task) => (
              <li key={task.taskId} className={styles.taskItem}>
                <span>{task.title}</span>
                <span>Assigned to: {task.assignedUserName}</span>
              </li>
            ))}
          </ul>
        )}
        {renderPagination(
          assignedPage,
          setAssignedPage,
          hasMoreAssignedTasks,
          assignedTasks,
          totalPagesAssigned // Pass totalPages
        )}
      </div>

      {/* Completed Tasks Section */}
      <div className={styles.tasksSection}>
        <h3>Completed Tasks</h3>
        {completedTasks.length === 0 ? (
          <p>No completed tasks</p>
        ) : (
          <ul>
            {completedTasks.map((task) => (
              <li key={task.taskId} className={styles.taskItem}>
                <span>{task.title}</span>
                <span>Completed by: {task.assignedUserName}</span>
              </li>
            ))}
          </ul>
        )}
        {renderPagination(
          completedPage,
          setCompletedPage,
          hasMoreCompletedTasks,
          completedTasks,
          totalPagesCompleted // Pass totalPages
        )}
      </div>
    </div>
  );
};

export default EmployeeDashboard;
