
import React, { useState, useEffect } from "react";

function MedicalCardTab() {
    const userInfo = JSON.parse(localStorage.getItem("userInfo")) || {};
    const [medicalCard, setMedicalCard] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (!userInfo.id) {
            setError("Пользователь не авторизован");
            setLoading(false);
            return;
        }

        const fetchMedicalCard = async () => {
            try {
                const response = await fetch(`http://localhost:8080/api/medical-records/user/${userInfo.id}`);
                if (!response.ok) {
                    throw new Error("Ошибка загрузки медицинской карты");
                }
                const data = await response.json();

                setMedicalCard(data[0]);
            } catch (error) {
                setError(error.message);
            } finally {
                setLoading(false);
            }
        };

        fetchMedicalCard();
    }, [userInfo.id]);

    if (loading) {
        return <div className="tab-content-item">Загрузка...</div>;
    }

    if (error) {
        return <div className="tab-content-item">Ошибка: {error}</div>;
    }

    if (!medicalCard) {
        return (
            <div className="tab-content-item">
                <h2>Медицинская карта</h2>
                <p>Медицинская карта отсутствует.</p>
            </div>
        );
    }

    return (
        <div className="tab-content-item">
            <h2>Медицинская карта</h2>
            <div className="medical-card">
                <p>
                    <strong>Жалобы:</strong> {medicalCard.report.complaints || "Не указано"}
                </p>
                <p>
                    <strong>Диагноз:</strong> {medicalCard.report.diagnosis || "Не указано"}
                </p>
                <p>
                    <strong>Обследования:</strong> {medicalCard.report.examinations || "Не указано"}
                </p>
                <p>
                    <strong>Лечение</strong> {medicalCard.report.treatment || "Не указано"}
                </p>
                <p>
                    <strong>Последний визит:</strong> {medicalCard.time + " / " + medicalCard.date || "Не указано"}
                </p>
            </div>
        </div>
    );
}

export default MedicalCardTab;
