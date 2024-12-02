import React, { useState } from "react";
import toast from "react-hot-toast";
import * as styles from "./Login.module.css";
import axios from "axios";
import { HiEye, HiEyeOff } from "react-icons/hi";
import { MdClose } from "react-icons/md";
import { useNavigate } from "react-router-dom";

const Login = () => {
    const [formData, setFormData] = useState({
        username: "",
        password: "",
    });
    
    const navigate = useNavigate();

    const [isPasswordVisible, setIsPasswordVisible] = useState(false);

    const handleTogglePasswordVisibility = () => {
        setIsPasswordVisible((prev) => !prev);
    };

    const handleInputChange = ({ target: { name, value } }) => {
        setFormData((prev) => ({ ...prev, [name]: value.trim() }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const trimmedData = {
            ...formData,
            username: formData.username.trim().toLowerCase(),
            password: formData.password.trim()
        };

        try {
            const response = await axios.post("http://localhost:5001/api/auth/authenticate", trimmedData);
            console.log(response);
        } catch (error) {
            const errorMessages = error?.response?.data?.errors || ["Login failed. Please try again."];
            errorMessages.forEach((msg) => toast.error(msg));
        }
    }

    return (
        <div className={styles.loginContainer}>
            <div className={styles.formSection}>
                {/* Close button to navigate back */}
                <button className={styles.closeButton} onClick={() => navigate(-1)}>
                    <MdClose size={24} />
                </button>
                <p className={styles.welcomeText}>WELCOME BACK</p>
                <h1>Sign in</h1>
                <form onSubmit={handleSubmit}>
                    <InputField name="username" label="Username" value={formData.username} onChange={handleInputChange} />
                    <div className={styles.passwordFieldWrapper}>
                        <InputField name="password" type={isPasswordVisible ? "text" : "password"}  label="Password" value={formData.password} onChange={handleInputChange} />
                        <button type="button" onClick={handleTogglePasswordVisibility} className={styles.passwordToggleBtn}>
                                {isPasswordVisible ? <HiEyeOff /> : <HiEye />}
                        </button>
                    </div>
                    <button type="submit" className={styles.btnRegister}>
                        Sign in
                    </button>
                </form>
                <p className={styles.signupText}>Don't have an account? <a href="/signup">Sign Up</a></p>
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

export default Login;
