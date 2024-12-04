import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import apiClient from "./APIs/ApiClient";
import toast from "react-hot-toast";
import * as styles from "./UserDashboard.module.css";

const UserDashboard = () => {
  const navigate = useNavigate();
  const [user, setUser] = useState({});
  const [tasks, setTasks] = useState([]);
  const [page, setPage] = useState(1);  
  const [totalPages, setTotalPages] = useState(1);
  const [isEditing, setIsEditing] = useState(false);
  const [editedUser, setEditedUser] = useState({});

  useEffect(() => {
    fetchUserData();
    fetchTasks();
  }, [page]);

  const fetchUserData = async () => {
    try {
      const userId = localStorage.getItem("userId");
      const response = await apiClient.get(`/api/user/${userId}`);
      const userData = response.data.data;
      setUser(userData);
      setEditedUser(userData);
    } catch (error) {
      console.error("Error fetching user data:", error);
      toast.error("Failed to fetch user data.");
    }
  };

  const fetchTasks = async () => {
    try {
      const userId = localStorage.getItem("userId");
      const response = await apiClient.get("/api/user/allTask", {
        params: {
          page: page,
          perPage: 5,
          search: "",
          sort: "ASC",
          userId: userId,
          status: "",
          sortBy: "",
          startDate: "",
          endDate: "",
          pageable: true,
        }
      });

      const taskData = response.data.data;
      setTasks(taskData);
      setTotalPages(response.data.totalPage);
    } catch (error) {
      console.error("Error fetching tasks:", error);
      toast.error("Failed to fetch tasks.");
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("authToken");
    localStorage.removeItem("refreshToken");
    localStorage.removeItem("userRole");
    localStorage.removeItem("userId");
    navigate("/login");
  };

  const handleEditProfile = () => {
    setIsEditing(true);
  };

  const handleCancelEdit = () => {
    setIsEditing(false);
    setEditedUser(user);
  };

  const handleSaveProfile = async () => {
  
    const updatedUserData = {
      userId: user.userId, 
      firstName: editedUser.firstName,
      lastName: editedUser.lastName,
      email: editedUser.email,
    };

    try {
      const response = await apiClient.put(
        "/api/user",
        updatedUserData,
      );

      if (response.status === 200) {
        setUser(editedUser); 
        setIsEditing(false); 
        toast.success("Profile updated successfully!"); 
      }
    } catch (error) {
      console.error("Error updating profile:", error);
      toast.error("Failed to update profile."); 
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setEditedUser((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  // Function to mark task as completed
  const markAsCompleted = async (taskId) => {
    try {
      const response = await apiClient.put("/api/user/update/status", {
        taskId,
        status: "COMPLETED"
      });      
      if (response.status === 200) {
        // Update the task list to reflect the change
        const updatedTasks = tasks.map((task) =>
          task.taskId === taskId ? { ...task, status: "COMPLETED" } : task
        );
        setTasks(updatedTasks);
        toast.success("Task marked as completed!");
      }
    } catch (error) {
      console.error("Error marking task as completed:", error);
      toast.error("Failed to mark task as completed.");
    }
  };

  const handlePrevPage = () => {
    if (page > 1) {
      setPage(page - 1);
    }
  };

  const handleNextPage = () => {
    if (page < totalPages) {
      setPage(page + 1);
    }
  };

  return (
    <div className={styles.dashboardContainer}>
      {/* Left Section (User Profile) */}
      <div className={styles.profileSection}>
        <h2>User Profile</h2>
        <div>
          <label>First Name:</label>
          {isEditing ? (
            <input
              type="text"
              name="firstName"
              value={editedUser.firstName}
              onChange={handleInputChange}
            />
          ) : (
            <span>{user.firstName}</span>
          )}
        </div>

        <div>
          <label>Last Name:</label>
          {isEditing ? (
            <input
              type="text"
              name="lastName"
              value={editedUser.lastName}
              onChange={handleInputChange}
            />
          ) : (
            <span>{user.lastName}</span>
          )}
        </div>

        <div>
          <label>Username:</label>
          <span>{user.username}</span>
        </div>

        <div>
          <label>Email:</label>
          {isEditing ? (
            <input
              type="email"
              name="email"
              value={editedUser.email}
              onChange={handleInputChange}
            />
          ) : (
            <span>{user.email}</span>
          )}
        </div>

        {!isEditing ? (
          <button onClick={handleEditProfile}>Edit Profile</button>
        ) : (
          <div>
            <button onClick={handleSaveProfile}>Save Profile</button>
            <button onClick={handleCancelEdit}>Cancel</button>
          </div>
        )}
      </div>

      {/* Right Section (Assigned Tasks) */}
      <div className={styles.tasksSection}>
        <h2>Assigned Tasks</h2>
        <ul>
          {tasks.length === 0 ? (
            <li>No tasks assigned</li>
          ) : (
            tasks.map((task) => (
              <li key={task.taskId}>
                <h3>{task.title}</h3>
                <p>{task.description}</p>
                {task.status !== "COMPLETED" && (
                  <button
                    className={styles.markAsCompletedButton}
                    onClick={() => markAsCompleted(task.taskId)}
                  >
                    Mark as Completed
                  </button>
                )}
              </li>
            ))
          )}
        </ul>

        {/* Pagination */}
        <div className={styles.pagination}>
          <button onClick={handlePrevPage} disabled={page <= 1}>
            Previous
          </button>
          <span>
            Page {page} of {totalPages}
          </span>
          <button onClick={handleNextPage} disabled={page >= totalPages}>
            Next
          </button>
        </div>
      </div>

      {/* Logout Button */}
      <div className={styles.logoutButton}>
        <button onClick={handleLogout}>Logout</button>
      </div>
    </div>
  );
};

export default UserDashboard;
