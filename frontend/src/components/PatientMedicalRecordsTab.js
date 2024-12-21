import React, { useState, useEffect } from 'react';
import MedicalRecordModal from './MedicalRecordModal';

const PatientMedicalRecordsTab = () => {
    const userInfo = JSON.parse(localStorage.getItem('userInfo')) || null;
    const [patients, setPatients] = useState([]);
    const [loading, setLoading] = useState(false);
    const [modalVisible, setModalVisible] = useState(false);
    const [selectedPatientId, setSelectedPatientId] = useState(null);
    const [selectedDoctorId] = useState(userInfo?.id);

    useEffect(() => {
        fetchPatients();
    }, []);

    const fetchPatients = async () => {
        setLoading(true);
        try {
            const response = await fetch('http://localhost:8080/api/users');
            const patientsData = await response.json();

            const patientsWithRecords = await Promise.all(
                patientsData.map(async (patient) => {
                    const recordResponse = await fetch(`http://localhost:8080/api/medical-records/user/${patient.id}`);
                    const medicalRecord = recordResponse.ok ? await recordResponse.json() : [];
                    return { ...patient, medicalRecord };
                })
            );
            setPatients(patientsWithRecords);
        } catch (error) {
            console.error('Ошибка загрузки данных о пациентах:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleUpdateMedicalRecord = (patientId) => {
        setSelectedPatientId(patientId);
        setModalVisible(true);
    };


    const handleCloseModal = () => {
        setModalVisible(false);
        setSelectedPatientId(null);
    };

    if (loading) return <div>Загрузка...</div>;

    return (
        <div>
            <h2>Медицинские карты пациентов</h2>
            {patients.length > 0 ? (
                <div>
                    {patients.map((patient) => (
                        <div key={patient.id} className="patient-record">
                            <p>
                                {patient.lastName} {patient.firstName} {patient.middleName || ''}
                            </p>
                            {patient.medicalRecord?.length > 0 ? (
                                <button onClick={() => handleUpdateMedicalRecord(patient.id)}>
                                    Редактировать медкарту
                                </button>
                            ) : (
                                <button onClick={() => handleUpdateMedicalRecord(patient.id)}>
                                    Создать медкарту
                                </button>
                            )}
                        </div>
                    ))}
                </div>
            ) : (
                <p>Нет данных о пациентах</p>
            )}

            {modalVisible && (
                <MedicalRecordModal
                    patientId={selectedPatientId}
                    medicalCard={patients.find((p) => p.id === selectedPatientId)?.medicalRecord}
                    doctorId={selectedDoctorId}
                    onClose={handleCloseModal}
                />
            )}
        </div>
    );
};

export default PatientMedicalRecordsTab;
