import React from "react";
import * as styles from "./ErrorPage.module.css";

const ErrorPage = () => {
    return (
        <div className={styles.errorContainer}>
            <div className={styles.errorCard}>
                <h1 className={styles.errorTitle}>Something Went Wrong!</h1>
                <p className={styles.errorMessage}>Oops, an unexpected error occurred. Please try again later.</p>
            </div>
        </div>
    );
};

export default ErrorPage;
