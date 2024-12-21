import React, { useState, useEffect } from "react";
import "../styles/DoctorScheduleEditor.css";

const DoctorScheduleEditor = ({ doctorId, isOpen, onClose }) => {
    const [schedules, setSchedules] = useState([]);
    const [selectedSchedule, setSelectedSchedule] = useState(null);
    const [date, setDate] = useState("");
    const [slots, setSlots] = useState([]);
    const [isAdding, setIsAdding] = useState(false);

    useEffect(() => {
        if (isOpen && doctorId) fetchSchedules();
        console.log("Updated schedules:", schedules);
    }, [isOpen, doctorId]);

    const fetchSchedules = async () => {
        try {
            const response = await fetch(`http://localhost:8080/api/schedules/doctor/${doctorId}`);
            if (response.ok) {
                const data = await response.json();
                setSchedules(data);
                console.log("Fetched schedules:", data);
            } else {
                console.error("Failed to fetch schedules");
                setSchedules([]);
            }
        } catch (error) {
            console.error("Error fetching schedules:", error);
            setSchedules([]);
        }
    };

    const handleScheduleClick = (schedule) => {
        setSelectedSchedule(schedule);
        setDate(schedule.date);
        setSlots(schedule.slots || []);
        setIsAdding(false);
    };

    const handleAddNewSchedule = () => {
        setSelectedSchedule(null);
        setDate("");
        setSlots([]);
        setIsAdding(true);
    };

    const handleSlotChange = (index, field, value) => {
        const updatedSlots = [...slots];
        updatedSlots[index][field] = value;
        setSlots(updatedSlots);
    };

    const handleSave = async () => {
        const payload = { date, doctorId, slots };
        const method = selectedSchedule ? "PUT" : "POST";
        const endpoint = selectedSchedule
            ? `http://localhost:8080/api/schedules/${selectedSchedule.id}`
            : "http://localhost:8080/api/schedules";

        const response = await fetch(endpoint, {
            method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload),
        });

        if (response.ok) {
            console.log("Payload being sent:", JSON.stringify(payload));
            alert("Schedule saved successfully!");
            fetchSchedules();
            setSelectedSchedule(null);
            setIsAdding(false);
        } else {
            alert("Failed to save schedule.");
        }
    };

    const handleClose = () => {
        onClose();
        setSchedules([]);
        setSelectedSchedule(null);
        setIsAdding(false);
    };

    if (!isOpen) return null;

    return (
        <div className="modal">
            <div className="modal-content">
                {!selectedSchedule && !isAdding && (
                    <>
                        <h2>Расписания</h2>
                        <div className="schedules-container">
                            {schedules.map((schedule) => (
                                <div
                                    key={schedule.id}
                                    className="schedule-box"
                                    onClick={() => handleScheduleClick(schedule)}
                                >
                                    <h3>{schedule.date}</h3>
                                    <div className="slots-container">
                                        {schedule.slots.map((slot, index) => (
                                            <div
                                                key={index}
                                                className={`slot-box ${slot.booked ? "booked" : "available"}`}
                                            >
                                                {slot.time}
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            ))}
                        </div>
                        <button onClick={handleAddNewSchedule}>Добавить расписание</button>
                        <button onClick={handleClose}>Закрыть</button>
                    </>
                )}
                {(selectedSchedule || isAdding) && (
                    <>
                        <h2>{isAdding ? "Новое расписание" : `Редактировать (${selectedSchedule.date})`}</h2>
                        <label>
                            Дата:
                            <input
                                type="date"
                                value={date}
                                onChange={(e) => setDate(e.target.value)}
                            />
                        </label>
                        <div>
                            <h3>Слоты</h3>
                            {slots.map((slot, index) => (
                                <div key={index} className="slot-editor">
                                    <input
                                        type="time"
                                        value={slot.time}
                                        onChange={(e) => handleSlotChange(index, "time", e.target.value)}
                                    />
                                    <label>
                                        Занято:
                                        <input
                                            type="checkbox"
                                            checked={slot.booked}
                                            onChange={(e) => handleSlotChange(index, "booked", e.target.checked)}
                                        />
                                    </label>
                                    {slot.booked && <p>ID пациента: {slot.patientId || "N/A"}</p>}
                                </div>
                            ))}
                            <button onClick={() => setSlots([...slots, { time: "", booked: false }])}>
                                Добавить слот
                            </button>
                        </div>
                        <button onClick={handleSave}>Сохранить</button>
                        <button onClick={handleClose}>Закрыть</button>
                    </>
                )}
            </div>
        </div>
    );
};
export default DoctorScheduleEditor;
