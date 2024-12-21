import React, { useState, useEffect, useRef } from "react";

const ChatTab = () => {
    const [doctors, setDoctors] = useState([]);
    const [selectedDoctor, setSelectedDoctor] = useState(null);
    const [messages, setMessages] = useState([]);
    const [inputValue, setInputValue] = useState("");
    const socketRef = useRef(null);
    const userInfo = JSON.parse(localStorage.getItem("userInfo"));

    useEffect(() => {
        const fetchDoctors = async () => {
            try {
                const response = await fetch("http://localhost:8080/api/doctors");
                if (!response.ok) {
                    throw new Error("Ошибка загрузки списка врачей.");
                }
                const data = await response.json();
                setDoctors(data);
                console.log(data);
            } catch (error) {
                console.error("Ошибка:", error.message);
            }
        };
        fetchDoctors();
    }, []);

    useEffect(() => {
        if (selectedDoctor) {
            if (socketRef.current) {
                socketRef.current.close();
            }

            socketRef.current = new WebSocket(
                `ws://localhost:8080/chat/client/${userInfo.id}`
            );

            socketRef.current.onopen = () => {
                console.log(`Соединение установлено с врачом: ${selectedDoctor.name}`);
            };

            socketRef.current.onmessage = (event) => {
                let message;

                try {
                    message = JSON.parse(event.data);
                    console.log("Получено сообщение в формате JSON:", message);
                    setMessages((prevMessages) => [
                        ...prevMessages,
                        { sender: "doctor", text: message.message },
                    ]);
                } catch (error) {
                    console.log("Получено сообщение как строка:", event.data);
                    setMessages((prevMessages) => [
                        ...prevMessages,
                        { sender: "doctor", text: event.data },
                    ]);
                }
            };

            socketRef.current.onclose = () => {
                console.log(`Соединение закрыто с врачом: ${selectedDoctor.name}`);
            };

            return () => {
                if (socketRef.current) {
                    socketRef.current.close();
                }
            };
        }
    }, [selectedDoctor]);

    const sendMessage = () => {
        if (!inputValue.trim()) return;

        const message = {
            targetId: selectedDoctor.id,
            message: inputValue,
        };

        if (socketRef.current && socketRef.current.readyState === WebSocket.OPEN) {
            socketRef.current.send(JSON.stringify(message));
            console.log("Сообщение отправлено:", message);

            setMessages((prevMessages) => [
                ...prevMessages,
                { sender: "client", text: inputValue },
            ]);

            setInputValue("");
        } else {
            console.error("WebSocket соединение не открыто. Сообщение не может быть отправлено.");
        }
    };

    return (
        <div className="chat-tab">
            <div className="doctor-list">
                <h3>Список врачей</h3>
                <ul>
                    {doctors.map((doctor) => (
                        <li
                            key={doctor.id}
                            className={selectedDoctor?.id === doctor.id ? "selected" : ""}
                            onClick={() => setSelectedDoctor(doctor)}
                        >
                            {doctor.name}
                        </li>
                    ))}
                </ul>
            </div>
            <div className="chat-window">
                {selectedDoctor ? (
                    <>
                        <h3>Чат с {selectedDoctor.name}</h3>
                        <div className="messages">
                            {messages.map((msg, index) => (
                                <p
                                    key={index}
                                    className={msg.sender === "client" ? "client-message" : "doctor-message"}
                                >
                                    {msg.text}
                                </p>
                            ))}
                        </div>
                        <div className="message-input">
                            <input
                                type="text"
                                placeholder="Введите сообщение..."
                                value={inputValue}
                                onChange={(e) => setInputValue(e.target.value)}
                                onKeyDown={(e) => e.key === "Enter" && sendMessage()}
                            />
                            <button onClick={sendMessage}>Отправить</button>
                        </div>
                    </>
                ) : (
                    <p>Выберите врача для начала чата</p>
                )}
            </div>
        </div>
    );
};

export default ChatTab;
