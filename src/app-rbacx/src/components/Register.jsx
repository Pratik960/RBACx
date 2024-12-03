import React, { useState } from "react";
import * as styles from "./Register.module.css";
import axios from "axios";
import toast from "react-hot-toast";
import registrationImage from "./images/RBACxLogo.png";
import { useNavigate } from "react-router-dom";

const Register = () => {
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    username: "",
    email: "",
    password: "",
    role: "",
  });

  const navigate = useNavigate();

  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleInputChange = ({ target: { name, value } }) => {
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const validateForm = () => {
    const { email, password, firstName, lastName, role } = formData;
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const passRegex =
      /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@.#$!%*?&^])[A-Za-z\d@.#$!%*?&]{8,15}$/;

    if (firstName.trim().length < 3) {
      toast.error("First name must be more than 3 characters");
      return false;
    }

    if (lastName.trim().length < 3) {
      toast.error("Last name must be more than 3 characters");
      return false;
    }

    if (!emailRegex.test(email)) {
      toast.error("Please enter a valid email address");
      return false;
    }
    if (!passRegex.test(password)) {
      toast.error(
        "Use a password 8-15 characters long with uppercase, lowercase, a number, and a special character."
      );
      return false;
    }
    if (!role) {
      toast.error("Please select a role");
      return false;
    }
    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;

    const roleMapping = {
      User: "ROLE_USER",
      Employee: "ROLE_EMP",
    };

    const transformedRole = roleMapping[formData.role];

    const trimmedData = {
      ...formData,
      role: transformedRole,
    };

    setIsSubmitting(true);

    try {
      const apiUrl = "http://localhost:5001";

      const response = await axios.post(
        `${apiUrl}/api/auth/signup`,
        trimmedData
      );

      if (response.status === 201) {
        const successMessage = "Registration successful!";
        toast.success(successMessage);
        toast.success("Please check your email to activate your account.");
        setTimeout(() => {
          navigate("/login");
        }, 3000);
        setFormData({
          firstName: "",
          lastName: "",
          username: "",
          email: "",
          password: "",
          role: "",
        });
      }
    } catch (error) {
      console.log(error);
      const errorMessages = error?.response?.data?.errors || [
        "Registration failed. Please try again.",
      ];
      errorMessages.forEach((msg) => toast.error(msg));
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className={styles.registerContainer}>
      <div className={styles.formSection}>
        <p>START YOUR JOURNEY</p>
        <h1>Create your account</h1>
        <p>
          Already a member? <a href="/login">Log in</a>
        </p>

        <form onSubmit={handleSubmit}>
          <div className={styles.formRow}>
            <InputField
              name="firstName"
              label="First Name"
              value={formData.firstName}
              onChange={handleInputChange}
            />
            <InputField
              name="lastName"
              label="Last Name"
              value={formData.lastName}
              onChange={handleInputChange}
            />
          </div>
          <InputField
            name="username"
            label="Username"
            value={formData.username}
            onChange={handleInputChange}
          />
          <InputField
            name="email"
            type="email"
            label="E-mail"
            value={formData.email}
            onChange={handleInputChange}
          />
          <InputField
            name="password"
            type="password"
            label="Password"
            value={formData.password}
            onChange={handleInputChange}
          />
          <div className={styles.formGroup}>
            <select
              id="role"
              name="role"
              value={formData.role}
              onChange={handleInputChange}
              className={styles.selectField}
              required
            >
              <option value="" disabled>
                Select a role
              </option>
              <option value="User">User</option>
              <option value="Employee">Employee</option>
            </select>
          </div>
          <button
            type="submit"
            className={styles.btnRegister}
            disabled={isSubmitting}
          >
            {isSubmitting ? "Creating account..." : "Create account"}
          </button>
        </form>
      </div>

      <div className={styles.imageSection}>
        <img
          src={registrationImage}
          alt="Person registering on a platform with a form"
        />
      </div>
    </div>
  );
};

const InputField = ({ name, label, value, onChange, type = "text" }) => (
  <div className={styles.formGroup}>
    <input
      type={type}
      id={name}
      name={name}
      value={value}
      onChange={onChange}
      required
      placeholder=" "
      aria-label={label}
    />
    <label htmlFor={name}>{label}</label>
  </div>
);

export default Register;
