import React, { useState, useEffect } from "react";

const WorkloadStatsTab = () => {
    const [overallWorkload, setOverallWorkload] = useState(null);
    const [doctorsWorkload, setDoctorsWorkload] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchWorkloadStats = async () => {
            setLoading(true);
            try {
                // 1. Получаем общее расписание
                const schedulesResponse = await fetch("http://localhost:8080/api/schedules");
                if (!schedulesResponse.ok) {
                    throw new Error("Ошибка загрузки расписания.");
                }
                const schedules = await schedulesResponse.json();

                // Рассчитываем общую занятость по слотам
                let totalSlots = 0;
                let occupiedSlots = 0;

                schedules.forEach((schedule) => {
                   totalSlots += schedule.slots.length;
                   schedule.slots.forEach((slot) => {
                        if(slot.booked) {
                            occupiedSlots += 1;
                        }
                   });
                });

                const overallWorkloadPercentage = totalSlots
                    ? ((occupiedSlots / totalSlots) * 100).toFixed(2)
                    : 0;

                setOverallWorkload(overallWorkloadPercentage);

                // 2. Получаем список врачей
                const doctorsResponse = await fetch("http://localhost:8080/api/doctors");
                if (!doctorsResponse.ok) {
                    throw new Error("Ошибка загрузки списка врачей.");
                }
                const doctors = await doctorsResponse.json();

                // Рассчитываем занятость по врачам
                const doctorsWorkloadData = await Promise.all(
                    doctors.map(async (doctor) => {
                        try {
                            const doctorScheduleResponse = await fetch(
                                `http://localhost:8080/api/schedules/doctor/${doctor.id}`
                            );
                            const doctorSchedules = doctorScheduleResponse.ok
                                ? await doctorScheduleResponse.json()
                                : [];

                            let doctorTotalSlots = 0;
                            let doctorOccupiedSlots = 0;

                            // Проверяем наличие расписания
                            if (doctorSchedules.length > 0) {
                                doctorSchedules.forEach((schedule) => {
                                    doctorTotalSlots += schedule.slots.length;
                                    schedule.slots.forEach((slot) => {
                                        if(slot.booked) {
                                            doctorOccupiedSlots += 1;
                                        }
                                    });
                                });
                            }

                            const doctorWorkloadPercentage = doctorTotalSlots
                                ? ((doctorOccupiedSlots / doctorTotalSlots) * 100).toFixed(2)
                                : "Нет данных";

                            return {
                                doctorName: doctor.name,
                                workload: doctorSchedules.length > 0 ? doctorWorkloadPercentage : "Нет расписания",
                            };
                        } catch {
                            return {
                                doctorName: doctor.name,
                                workload: "Ошибка загрузки расписания",
                            };
                        }
                    })
                );
                console.log(doctorsWorkloadData)
                setDoctorsWorkload(doctorsWorkloadData);
            } catch (error) {
                setError(error.message);
            } finally {
                setLoading(false);
            }
        };

        fetchWorkloadStats();
    }, []);

    if (loading) {
        return <div>Загрузка статистики...</div>;
    }

    if (error) {
        return <div>Ошибка: {error}</div>;
    }

    return (
        <div>
            <h2>Статистика нагрузки</h2>
            <p>Данные о загруженности врачебного персонала на основе текущего расписания и приемов.</p>

            <div>
                <h3>Общая занятость по слотам</h3>
                <p>
                    {overallWorkload !== null
                        ? `Общая загруженность: ${overallWorkload}%`
                        : "Данные отсутствуют."}
                </p>
            </div>

            <div>
                <h3>Занятость по врачам</h3>
                {doctorsWorkload.length > 0 ? (
                    <ul>
                        {doctorsWorkload.map((doctor) => (
                            <li key={doctor.doctorName}>
                                {doctor.doctorName}: {doctor.workload} %
                            </li>
                        ))}
                    </ul>
                ) : (
                    <p>Нет данных о занятости врачей.</p>
                )}
            </div>
        </div>
    );
};

export default WorkloadStatsTab;
