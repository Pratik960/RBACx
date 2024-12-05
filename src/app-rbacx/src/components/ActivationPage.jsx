import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import * as styles from './ActivationPage.module.css';

const ActivationPage = () => {
  const navigate = useNavigate();

  useEffect(() => {
    // Automatically redirect to the login page after 10 seconds
    const timer = setTimeout(() => {
        navigate('/login', { replace: true });
    }, 10000);

    // Cleanup the timer on component unmount
    return () => clearTimeout(timer);
  }, [navigate]);

  return (
    <div className={styles.container}>
      <div className={styles.card}>
        <h1 className={styles.title}>Account Verified Successfully!</h1>
        <p className={styles.message}>You will be redirected to the login page shortly.</p>
        <p className={styles.redirectText}>
          If not, <span className={styles.link} onClick={() => navigate('/login', { replace: true })}>click here</span> to go to the login page.
        </p>
      </div>
    </div>
  );
};

export default ActivationPage;
