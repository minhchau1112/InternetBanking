import React from "react";

interface NotificationsProps {
    messages: string[];
}

const Notifications: React.FC<NotificationsProps> = ({ messages }) => {
    return (
        <div>
            <h2>Thông báo</h2>
            <ul>
                {messages.map((message, index) => (
                    <li key={index}>{message}</li>
                ))}
            </ul>
        </div>
    );
};

export default Notifications;