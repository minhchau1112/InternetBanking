import React, { useEffect } from "react";
import useWebSocket from '../hooks/useWebSocket';
import { useSnackbar } from 'notistack';

interface NotificationsProps {
  userId: string; 
}

const Notifications: React.FC<NotificationsProps> = ({ userId }) => {
    const message = useWebSocket({ userId });
    const { enqueueSnackbar } = useSnackbar();

    useEffect(() => {
        if (message) {
            enqueueSnackbar(message, { variant: 'success', autoHideDuration: 3000 });
        }
    }, [message, enqueueSnackbar]);

    return (
        <div>
        </div>
    );
};

export default Notifications;
