import axios from "axios";

const apiClient = axios.create({
  baseURL: "http://localhost:5001",
  headers: {
    "Content-Type": "application/json",
  },
});

apiClient.interceptors.request.use(
    (config) => {
      const authToken = localStorage.getItem('authToken'); 
      if (authToken) {
        config.headers['Authorization'] = `Bearer ${authToken}`;
      }
      return config;
    },
    (error) => {
      return Promise.reject(error);
    }
  );

// Interceptor to handle token expiration and refresh
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (
      error.response &&
      error.response.status === 401 &&
      !originalRequest._retry
    ) {
      originalRequest._retry = true; // Mark the request as retried

      try {
        // Request a new auth token using the refresh token
        const refreshToken = localStorage.getItem("refreshToken");
        const response = await axios.post(
          "http://localhost:5001/api/auth/refreshToken",
          {
            refreshToken,
          }
        );

        const newAuthToken = response.data.data.token;

        localStorage.setItem("authToken", newAuthToken);

        // Retry the original request with the new token
        originalRequest.headers["Authorization"] = `Bearer ${newAuthToken}`;
        return apiClient(originalRequest);
      } catch (refreshError) {
        console.error("Token refresh failed:", refreshError);
        window.location.href = "/login";
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default apiClient;
