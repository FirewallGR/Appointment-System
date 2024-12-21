import React, { useState, useEffect } from 'react';

const MedicalRecordModal = ({ patientId, doctorId, onClose, medicalCard }) => {
    const [formData, setFormData] = useState({
        patientId,
        doctorId,
        date: '',
        time: '',
        complaints: '',
        diagnosis: '',
        examinations: '',
        treatment: '',
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (medicalCard && medicalCard.length > 0) {
            const record = medicalCard[0].report;
            setFormData({
                patientId,
                doctorId,
                date: medicalCard[0].date,
                time: medicalCard[0].time,
                complaints: record.complaints,
                diagnosis: record.diagnosis,
                examinations: record.examinations,
                treatment: record.treatment,
            });
        }
    }, [medicalCard, patientId, doctorId]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prevState) => ({
            ...prevState,
            [name]: value,
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const method = medicalCard && medicalCard.length > 0 ? "PUT" : "POST";
        const endpoint = medicalCard && medicalCard.length > 0
            ? `http://localhost:8080/api/medical-records/${medicalCard[0].id}`
            : "http://localhost:8080/api/medical-records";

        const payload = {
            ...formData,
        };


        try {
            setLoading(true);
            setError(null);

            const response = await fetch(endpoint, {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(payload),
            });

            if (!response.ok) {
                throw new Error('Ошибка при отправке данных');
            }

            alert('Медицинская карта успешно сохранена!');
            onClose();
        } catch (error) {
            setError(error.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="modal">
            <div className="modal-content">
                <h2>{medicalCard && medicalCard.length > 0 ? 'Редактировать медкарту' : 'Создать медкарту'}</h2>
                {error && <div className="error">{error}</div>}
                <form onSubmit={handleSubmit}>
                    <div>
                        <label>Дата:</label>
                        <input
                            type="date"
                            name="date"
                            value={formData.date}
                            onChange={handleChange}
                        />
                    </div>
                    <div>
                        <label>Время:</label>
                        <input
                            type="time"
                            name="time"
                            value={formData.time}
                            onChange={handleChange}
                        />
                    </div>
                    <div>
                        <label>Жалобы:</label>
                        <textarea
                            name="complaints"
                            value={formData.complaints}
                            onChange={handleChange}
                            required
                        />
                    </div>
                    <div>
                        <label>Диагноз:</label>
                        <textarea
                            name="diagnosis"
                            value={formData.diagnosis}
                            onChange={handleChange}
                            required
                        />
                    </div>
                    <div>
                        <label>Обследования:</label>
                        <textarea
                            name="examinations"
                            value={formData.examinations}
                            onChange={handleChange}
                            required
                        />
                    </div>
                    <div>
                        <label>Лечение:</label>
                        <textarea
                            name="treatment"
                            value={formData.treatment}
                            onChange={handleChange}
                            required
                        />
                    </div>
                    <button type="submit" disabled={loading}>
                        {loading ? 'Сохранение...' : (medicalCard && medicalCard.length > 0 ? 'Редактировать медкарту' : 'Создать медкарту')}
                    </button>
                </form>
                <button onClick={onClose}>Закрыть</button>
            </div>
        </div>
    );
};

export default MedicalRecordModal;
