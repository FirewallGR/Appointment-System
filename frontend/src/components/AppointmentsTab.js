import React, { useState, useEffect } from 'react';

function AppointmentsTab() {
    const [doctors, setDoctors] = useState([]);
    const [userAppointments, setUserAppointments] = useState([]);
    const [selectedSlot, setSelectedSlot] = useState(null);
    const [view, setView] = useState('appointments');
    const [loading, setLoading] = useState(false);

    const userInfo = JSON.parse(localStorage.getItem("userInfo")) || null;

    useEffect(() => {
        fetchDoctors();
    }, []);

    const fetchDoctors = async () => {
        try {
            setLoading(true);
            const response = await fetch('http://localhost:8080/api/doctors');
            const doctorsData = await response.json();

            const doctorsWithSchedule = await Promise.all(
                doctorsData.map(async (doctor) => {
                    try {
                        const scheduleResponse = await fetch(
                            `http://localhost:8080/api/schedules/doctor/${doctor.id}`
                        );
                        const scheduleData = await scheduleResponse.json();
                        return { ...doctor, schedule: Array.isArray(scheduleData) ? scheduleData : [] };
                    } catch (error) {
                        console.error(`Ошибка загрузки расписания для врача ${doctor.id}:`, error);
                        return { ...doctor, schedule: [] };
                    }
                })
            );

            setDoctors(doctorsWithSchedule);
        } catch (error) {
            console.error('Ошибка загрузки списка врачей:', error);
        } finally {
            setLoading(false);
        }
    };

    const fetchUserAppointments = async () => {
        if (!userInfo || !userInfo.id) return;
        try {
            setLoading(true);
            const response = await fetch(`http://localhost:8080/api/schedules/user/${userInfo.id}`);
            const appointments = await response.json();
            setUserAppointments(appointments);
        } catch (error) {
            console.error('Ошибка загрузки приёмов пользователя:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleSlotClick = (doctor, schedule, slot) => {
        setSelectedSlot({ doctor, schedule, slot });
    };

    const handleConfirmAppointment = async () => {
        if (!selectedSlot || !userInfo) return;
        const { doctor, schedule, slot } = selectedSlot;

        try {
            setLoading(true);

            const updatedSlots = schedule.slots.map((s) =>
                s.time === slot.time ? { ...s, booked: true, patientId: userInfo.id } : s
            );

            const response = await fetch(`http://localhost:8080/api/schedules/${schedule.id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    date: schedule.date,
                    doctorId: doctor.id,
                    slots: updatedSlots,
                }),
            });

            if (response.ok) {
                alert('Запись успешна!');
                fetchDoctors();
                fetchUserAppointments();
            } else {
                alert('Ошибка записи!');
            }
        } catch (error) {
            console.error('Ошибка при подтверждении записи:', error);
        } finally {
            setSelectedSlot(null);
            setLoading(false);
        }
    };

    const handleCancelConfirmation = () => {
        setSelectedSlot(null);
    };

    const handleToggleView = (viewType) => {
        setView(viewType);
        if (viewType === 'myAppointments') {
            fetchUserAppointments();
        }
    };

    if (loading) return <div>Загрузка...</div>;

    return (
        <div className="tab-content-item">
            <h2>Записи на приём</h2>
            <div className="appointments">
                <button onClick={() => handleToggleView('appointments')}>Записаться</button>
                <button onClick={() => handleToggleView('myAppointments')}>Мои приёмы</button>

                {view === 'myAppointments' ? (
                    <div>
                        <h3>Мои приёмы</h3>
                        {userAppointments.length > 0 ? (
                            userAppointments.map((appointment, index) => (
                                <p key={index}>
                                    <strong>{appointment.date}</strong>, Врач: {appointment.doctorName}
                                </p>
                            ))
                        ) : (
                            <p>Нет записей</p>
                        )}
                    </div>
                ) : (
                    <div>
                        <h3>Врачи и их график</h3>
                        {doctors.length > 0 ? (
                            doctors.map((doctor) => (
                                <div key={doctor.id} className="doctor-schedule">
                                    <h4>{doctor.name} {}</h4>
                                    <div className="schedule">
                                        {doctor.schedule.length > 0 ? (
                                            doctor.schedule.map((schedule) => (
                                                <div
                                                    key={schedule.id}
                                                    className="schedule-box"
                                                >
                                                    <h3>{schedule.date}</h3>
                                                    <div className="slots-container">
                                                        {schedule.slots.map((slot, index) => (
                                                            <div
                                                                key={index}
                                                                className={`slot-box ${slot.booked ? "booked" : "available"}`}
                                                                onClick={() => !slot.booked && handleSlotClick(doctor, schedule, slot)}
                                                            >
                                                                {slot.time}
                                                            </div>
                                                        ))}
                                                    </div>
                                                </div>
                                            ))
                                        ) : (
                                            <p>Нет доступных слотов</p>
                                        )}
                                    </div>
                                </div>
                            ))
                        ) : (
                            <p>Нет доступных врачей</p>
                        )}
                    </div>
                )}

                {selectedSlot && (
                    <div className="confirmation-modal">
                        <h3>
                            Записаться на приём к {selectedSlot.doctor.name} в {selectedSlot.slot.date} в {selectedSlot.slot.time}?
                        </h3>
                        <button onClick={handleConfirmAppointment}>Да</button>
                        <button onClick={handleCancelConfirmation}>Нет</button>
                    </div>
                )}
            </div>
        </div>
    );
}

export default AppointmentsTab;
