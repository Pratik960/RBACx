import React from "react";
import { useNavigate } from "react-router-dom";

const UserDashboard = () => {
    const navigate = useNavigate();

    const handleLogout = () => {
        // Clear user data from localStorage
        localStorage.removeItem("authToken");
        localStorage.removeItem("userRole");

        // Redirect to login page
        navigate("/login");
    };

    return (
        <div style={{ padding: "20px", textAlign: "center" }}>
            <h1>This is the User Dashboard</h1>
            <button
                onClick={handleLogout}
                style={{
                    marginTop: "20px",
                    padding: "10px 20px",
                    fontSize: "16px",
                    backgroundColor: "#f44336",
                    color: "white",
                    border: "none",
                    borderRadius: "5px",
                    cursor: "pointer",
                }}
            >
                Logout
            </button>
        </div>
    );
};

export default UserDashboard;
