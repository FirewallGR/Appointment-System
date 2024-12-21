import React, { useEffect, useState } from "react";

const DoctorScheduleTab = () => {
    const [schedules, setSchedules] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const userInfo = JSON.parse(localStorage.getItem("userInfo"));
        console.log(userInfo.id)
        fetchSchedules(userInfo.id);
    }, []);

    const fetchSchedules = async (id) => {
        try {
            const response = await fetch(`http://localhost:8080/api/schedules/doctor/${id}`);
            if (response.ok) {
                const data = await response.json();
                setSchedules(data);
            } else {
                setError("Не удалось загрузить расписание.");
            }
        } catch (err) {
            setError("Произошла ошибка при загрузке расписания.");
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return <p>Загрузка расписания...</p>;
    }

    if (error) {
        return <p>{error}</p>;
    }

    if (schedules.length === 0) {
        return <p>Расписание отсутствует.</p>;
    }

    return (
        <div>
            <h2>Расписание врача</h2>
            {schedules.map((schedule) => (
                <div key={schedule.id} className="schedule-container">
                    <h3>{schedule.date}</h3>
                    <div className="slots-container">
                        {schedule.slots.map((slot, index) => (
                            <div
                                key={index}
                                className={`slot ${slot.booked ? "booked" : "available"}`}
                                title={slot.booked ? `Занято (Пациент ID: ${slot.patientId || "N/A"})` : "Свободно"}
                            >
                                {slot.time}
                            </div>
                        ))}
                    </div>
                </div>
            ))}
        </div>
    );
};

export default DoctorScheduleTab;

