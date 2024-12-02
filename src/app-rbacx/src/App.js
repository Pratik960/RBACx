import React from "react";
import Register from "./components/Register";
import Login from "./components/Login";
import ErrorPage from "./components/ErrorPage";
import UserDashboard from "./components/UserDashboard";
import ProtectedRoute from "./components/ProtectedRoute";
import { Toaster } from "react-hot-toast";
import { Routes, Route } from "react-router-dom";
function App() {
  return (
    <div>
      {/* <Toaster position="top-center" reverseOrder={false} /> */}
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

      <>
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/signup" element={<Register />} />
          <Route path="/login" element={<Login />} />
          <Route
            path="/userdashboard"
            element={
              <ProtectedRoute
                element={<UserDashboard />}
                requiredRole={"ROLE_USER"}
              />
            }
          />
          <Route path="*" element={<ErrorPage />} />
          <Route path="/error" element={<ErrorPage />} />
        </Routes>
      </>
    </div>
  );
}

export default App;
