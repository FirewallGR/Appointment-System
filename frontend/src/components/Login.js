import React, { useState } from 'react';
import '../styles/Login.css';

function Login({ isOpen, onClose, onLoginSuccess }) {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');



    const handleLogin = async (e) => {
        e.preventDefault();
        setErrorMessage("");

        try {
            const response = await fetch('http://localhost:8080/api/auth', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    username: username,
                    password: password,
                }),
            });

            if (response.ok) {
                const data = await response.json();
                console.log('JWT Token:', data.token);

                localStorage.setItem('token', data.token);
                onLoginSuccess();
                onClose();
            } else {
                const errorData = await response.json();
                if (response.status === 401) {
                    setErrorMessage(errorData.message);
                } else {
                    setErrorMessage('Ошибка авторизации. Пожалуйста, повторите попытку.');
                }
            }
        } catch (error) {
            console.error(error);
            setErrorMessage("Проблемы с сервером, пожалуйста, подождите");
        }
    };

    if (!isOpen) return null;

    return (
        <div className="modal">
            <div className="modal-content">
                <h2>Вход</h2>

                {/* Сообщение об ошибке */}
                {errorMessage && <div className="error-message">{errorMessage}</div>}

                <form>
                    <div className="input-group">
                        <label htmlFor="username">Логин</label>
                        <input
                            type="text"
                            id="username"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                        />
                    </div>
                    <div className="input-group">
                        <label htmlFor="password">Пароль</label>
                        <input
                            type="password"
                            id="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                        />
                    </div>

                    <button type="button" className="button" onClick={handleLogin}>Войти</button>
                </form>
                <button className="button close" onClick={onClose}>Закрыть</button>
            </div>
        </div>
    );
}

export default Login;
