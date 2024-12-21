
import React from 'react';

function ProfileTab({userInfo}) {
    return (
        <div className="tab-content-item">
            <h2>Профиль</h2>
            {userInfo ? (
                <div>
                    <p>Имя: {userInfo.name}</p>
                    <p>Фамилия: {userInfo.secondName}</p>
                    <p>Отчество: {userInfo.thirdName}</p>
                    <p>Логин: {userInfo.username}</p>
                    <p>Роли: {userInfo.roles}</p>
                </div>
            ) : (
                <p>Информация о профиле недоступна.</p>
            )}
        </div>
    );
}

export default ProfileTab;
