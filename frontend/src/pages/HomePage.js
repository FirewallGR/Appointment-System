
import React, {useEffect, useState} from 'react';
import { useNavigate } from 'react-router-dom';
import "../styles/HomePage.css"
import Login from '../components/Login';
import Registration from "../components/Registration";

function HomePage() {
    const [isLoginModalOpen, setIsLoginModalOpen] = useState(false);
    const [isRegistrationModalOpen, setIsRegistrationModalOpen] = useState(false);
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [forceCheck, setForceCheck] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        const token = localStorage.getItem('token');
        if (token) {
            setIsAuthenticated(true);
        }
    }, [forceCheck]);

    const handleLogout = () => {
        localStorage.clear();
        setIsAuthenticated(false);
    };

    const handleNavigateToDashboard = () => {
        navigate('/dashboard');
    };

    return (
        <div className="container">
            <div className="header">
                <div className="left">
                    <h1>SuperMed</h1>
                </div>
                <div className="right">
                    {isAuthenticated ? (
                        <>
                            <button className="button" onClick={handleNavigateToDashboard}>
                                Личный кабинет
                            </button>
                            <button className="button" onClick={handleLogout}>
                                Выход
                            </button>
                        </>
                    ) : (
                        <>
                            <button
                                className="button"
                                onClick={() => setIsLoginModalOpen(true)}
                            >
                                Вход
                            </button>
                            <button
                                className="button"
                                onClick={() => setIsRegistrationModalOpen(true)}
                            >
                                Регистрация
                            </button>
                        </>
                    )}
                </div>
            </div>

            <Login
                isOpen={isLoginModalOpen}
                onClose={() => setIsLoginModalOpen(false)}
                onLoginSuccess={() => {
                    setForceCheck(!forceCheck);
                }}
            />

            {/* Модальное окно регистрации */}
            <Registration
                isOpen={isRegistrationModalOpen}
                onClose={() => setIsRegistrationModalOpen(false)}
            />
        </div>
    );
}

export default HomePage;
