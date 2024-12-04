import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Register from "./components/Register";
import Login from "./components/Login";
import ErrorPage from "./components/ErrorPage";
import UserDashboard from "./components/UserDashboard";
import ProtectedRoute from "./components/ProtectedRoute";
import { Toaster } from "react-hot-toast";
import { Routes, Route } from "react-router-dom";
import EmployeeDashboard from "./components/EmployeeDashboard";
import AdminDashboard from "./components/AdminDashboard";

function App() {
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem("authToken");
    const role = localStorage.getItem("userRole");

    if (token && role) {
      // Redirect to the appropriate dashboard based on the user's role
      switch (role) {
        case "ROLE_USER":
          navigate("/app/user/dashboard");
          break;
        case "ROLE_EMP":
          navigate("/app/emp/dashboard");
          break;
        case "ROLE_ADMIN":
          navigate("/app/admin/dashboard");
          break;
        default:
          navigate("/login");
          break;
      }
    }
  }, [navigate]);

  return (
    <div>
      {/* Toaster Configuration */}
      <Toaster
        position="top-right"
        toastOptions={{
          duration: 4000,
          style: {
            width: "500px",
            animation: "slide-in-right 0.5s ease-out",
          },
          success: {
            iconTheme: {
              primary: "green",
              secondary: "white",
            },
          },
          error: {
            iconTheme: {
              primary: "red",
              secondary: "white",
            },
          },
        }}
      />

      {/* Routes Configuration */}
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/signup" element={<Register />} />
        <Route path="/login" element={<Login />} />
        <Route
          path="/app/user/dashboard"
          element={
            <ProtectedRoute
              element={<UserDashboard />}
              requiredRole={"ROLE_USER"}
            />
          }
        />
        <Route
          path="/app/emp/dashboard"
          element={
            <ProtectedRoute
              element={<EmployeeDashboard />}
              requiredRole={"ROLE_EMP"}
            />
          }
        />
        <Route
          path="/app/admin/dashboard"
          element={
            <ProtectedRoute
              element={<AdminDashboard />}
              requiredRole={"ROLE_ADMIN"}
            />
          }
        />
        <Route path="*" element={<ErrorPage />} />
        <Route path="/app/error" element={<ErrorPage />} />
      </Routes>
    </div>
  );
}

export default App;
