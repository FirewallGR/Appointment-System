import React, { useEffect, useState } from 'react';
import DoctorScheduleEditor from "./DoctorScheduleEditor";
import Login from "./Login";
const ManageSchedulesTab = () => {
    const [doctors, setDoctors] = useState([]);
    const [error, setError] = useState("");
    const [selectedDoctorId, setSelectedDoctorId] = useState(null);
    const [isScheduleModalOpen, setIsScheduleModalOpen] = useState(false);

    useEffect(() => {
        const fetchDoctors = async () => {
            try {
                const token = localStorage.getItem('token');
                const response = await fetch('http://localhost:8080/api/doctors', {
                    method: 'GET',
                    headers: {
                        Authorization: `${token}`,
                        'Content-Type': 'application/json',
                    },
                });

                if (response.ok) {
                    const data = await response.json();
                    setDoctors(data);
                } else {
                    setError('Ошибка при получении данных');
                }
            } catch (error) {
                setError('Проблема с подключением к серверу');
            }
        };

        fetchDoctors();
    }, []);

    const openScheduleModal = (doctorId) => {
        setSelectedDoctorId(doctorId);
        setIsScheduleModalOpen(true);
    };

    return (
        <div>
            <h2>Управление расписанием врачей</h2>
            {error && <p className="error">{error}</p>}
            <table>
                <thead>
                <tr>
                    <th>Имя</th>
                    <th>Фамилия</th>
                    <th>Отчество</th>
                    <th>Логин</th>
                    <th>Действия</th>
                </tr>
                </thead>
                <tbody>
                {doctors.map((doctor) => (
                    <tr key={doctor.id}>
                        <td>{doctor.name}</td>
                        <td>{doctor.secondName}</td>
                        <td>{doctor.thirdName}</td>
                        <td>{doctor.username}</td>
                        <td>
                            <button className="button" onClick={() => openScheduleModal(doctor.id)}
                            >Редактировать расписание</button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
            {isScheduleModalOpen && selectedDoctorId && (
                <DoctorScheduleEditor
                    doctorId={selectedDoctorId}
                    isOpen={isScheduleModalOpen}
                    onClose={() => setIsScheduleModalOpen(false)}
                />
            )}
        </div>
    );
};

export default ManageSchedulesTab;
