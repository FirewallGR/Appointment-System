import React, { useState, useEffect, useRef } from "react";

const DoctorChatTab = () => {
    const [clients, setClients] = useState([]);
    const [selectedClient, setSelectedClient] = useState(null);
    const [messages, setMessages] = useState([]);
    const [inputValue, setInputValue] = useState("");
    const socketRef = useRef(null);
    const userInfo = JSON.parse(localStorage.getItem("userInfo"));

    useEffect(() => {
        const fetchClients = async () => {
            try {
                const response = await fetch("http://localhost:8080/api/users");
                if (!response.ok) {
                    throw new Error("Ошибка загрузки списка клиентов.");
                }
                const data = await response.json();
                console.log("Загруженные клиенты:", data);
                setClients(data);
            } catch (error) {
                console.error("Ошибка:", error.message);
            }
        };
        fetchClients();
    }, []);

    useEffect(() => {
        if (selectedClient) {
            if (socketRef.current) {
                socketRef.current.close();
            }

            socketRef.current = new WebSocket(
                `ws://localhost:8080/chat/doctor/${userInfo.id}`
            );

            socketRef.current.onopen = () => {
                console.log(`Соединение установлено с врачом: ${selectedClient.firstName}`);
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
                console.log(`Соединение закрыто с врачом: ${selectedClient.firstName}`);
            };

            return () => {
                if (socketRef.current) {
                    socketRef.current.close();
                }
            };
        }
    }, [selectedClient]);

    const sendMessage = () => {
        if (!inputValue.trim()) return;

        const message = {
            targetId: selectedClient.id,
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
                <h3>Список клиентов</h3>
                <ul>
                    {clients.map((client) => (
                        <li
                            key={client.id}
                            className={selectedClient?.id === client.id ? "selected" : ""}
                            onClick={() => setSelectedClient(client)}
                        >
                            {client.firstName}
                        </li>
                    ))}
                </ul>
            </div>
            <div className="chat-window">
                {selectedClient ? (
                    <>
                        <h3>Чат с {selectedClient.firstName}</h3>
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
                    <p>Выберите клиента для начала чата</p>
                )}
            </div>
        </div>
    );
};

export default DoctorChatTab;
