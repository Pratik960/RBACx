import React from "react";
import { Navigate } from "react-router-dom";

const ProtectedRoute = ({ element, requiredRole }) => {
    const token = localStorage.getItem("authToken");
    const role = localStorage.getItem("userRole");

    const hasRequiredRole = role?.toLowerCase() === requiredRole.toLowerCase();

    if (!token) {
        return <Navigate to="/login" />;
    }

    if (!hasRequiredRole) {
        return <Navigate to="/app/error" />;
    }

    return element;
};

export default ProtectedRoute;
