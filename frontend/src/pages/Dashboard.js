import React, { useEffect, useState } from 'react';
import "../styles/Dashboard.css";
import ProfileTab from "../components/ProfileTab";
import MedicalCardTab from "../components/MedicalCardTab";
import AppointmentsTab from "../components/AppointmentsTab";
import ChatTab from "../components/ChatTab";
import DoctorScheduleTab from "../components/DoctorScheduleTab";
import PatientMedicalRecordsTab from "../components/PatientMedicalRecordsTab";
import ManageSchedulesTab from "../components/ManageSchedulesTab";
import WorkloadStatsTab from "../components/WorkloadStatsTab";
import ApplicationsTab from "../components/ApplicationsTab";
import {useNavigate} from "react-router-dom";
import DoctorChatTab from "../components/DoctorChatTab";

function Dashboard() {
    const [activeTab, setActiveTab] = useState(1);
    const [userInfo, setUserInfo] = useState(null);
    const [loading, setLoading] = useState(true);
    const [userRole, setUserRole] = useState(null);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    // Функция для получения информации с сервера
    const fetchUserInfo = async () => {
        try {
            const token = localStorage.getItem('token');
            if (!token) throw new Error("Токен не найден");

            const response = await fetch('http://localhost:8080/api/info', {
                method: 'GET',
                headers: {
                    Authorization: `${token}`,
                },
            });

            if (response.ok) {
                const data = await response.json();
                if(data.roles[0] == "ROLE_PATIENT") {
                    data.roles[0] = "Клиент"
                } else if(data.roles[0] == "ROLE_DOCTOR") {
                    data.roles[0] = "Доктор"
                } else if(data.roles[0] == "ROLE_MANAGER") {
                    data.roles[0] = "Менеджер"
                }
                setUserRole(data.roles[0]);
                setUserInfo(data);
                localStorage.setItem("userInfo", JSON.stringify(data));
                console.log("Данные пользователя:", data);
            } else {
                const errorData = await response.json();
                throw new Error(errorData.message || "Ошибка при получении данных пользователя");
            }
        } catch (err) {
            console.error(err);
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchUserInfo();

    }, []);

    const handleTabClick = (tabIndex) => {
        setActiveTab(tabIndex);
    };
    const handleLogout = () => {
        localStorage.clear();
        navigate('/');
    };

    const handleGoToMenu = () => {
        navigate('/');
    };

    if (loading) {
        return <div className="loading">Загрузка...</div>;
    }

    if (error) {
        return <div className="error">Ошибка: {error}</div>;
    }

    const renderTabsAndContent = () => {
        const roleTabs = {
            Клиент: [
                { id: 2, label: "Медицинская карта", component: <MedicalCardTab /> },
                { id: 3, label: "Записи приема", component: <AppointmentsTab /> },
                { id: 4, label: "Онлайн чат", component: <ChatTab /> },
            ],
            Доктор: [
                { id: 2, label: "Расписание", component: <DoctorScheduleTab /> },
                { id: 3, label: "Медицинские карты пациентов", component: <PatientMedicalRecordsTab /> },
                { id: 4, label: "Онлайн чат", component: <DoctorChatTab /> },
            ],
            Менеджер: [
                { id: 2, label: "Расписание врачей", component: <ManageSchedulesTab /> },
                { id: 3, label: "Статистика нагрузки", component: <WorkloadStatsTab /> },
                { id: 4, label: "Заявки", component: <ApplicationsTab /> },
            ],
        };

        const commonTabs = [{ id: 1, label: "Профиль", component: <ProfileTab userInfo={userInfo} /> }];

        const tabs = commonTabs.concat(roleTabs[userRole] || []);

        return {
            tabs: tabs.map((tab) => (
                <button
                    key={tab.id}
                    className={activeTab === tab.id ? 'tab active' : 'tab'}
                    onClick={() => handleTabClick(tab.id)}
                >
                    {tab.label}
                </button>
            )),
            content: tabs.find((tab) => tab.id === activeTab)?.component || null,
        };
    };

    const { tabs, content } = renderTabsAndContent();

    return (
        <div className="client-dashboard">
            <div className="tabs">{tabs}</div>
            <div className="tab-content">{content}</div>
            <div className="dashboard-footer">
                <button className="button" onClick={handleGoToMenu}>Главное меню</button>
                <button className="button logout" onClick={handleLogout}>Выход</button>
            </div>
        </div>
    );
};
export default Dashboard;
