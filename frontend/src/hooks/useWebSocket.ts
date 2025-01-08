import { useEffect, useState } from "react";
import { Client, Message } from "@stomp/stompjs"; 

interface UseSocketHookProps {
  userId: string; 
}

const useWebSocket = ({ userId }: UseSocketHookProps) => {
  const [message, setMessage] = useState<string | null>(null);
  const [client, setClient] = useState<Client | null>(null); 
  const serverUrl = "ws://localhost:8888/ws";

  useEffect(() => {
    const socketClient = new Client({
      brokerURL: serverUrl,
      connectHeaders: {

      },
      debug: (str: string) => console.log(str),
      onConnect: () => {
        console.log("Connected to WebSocket");
        
        socketClient.subscribe(`/user/${userId}/queue/notifications`, (msg: Message) => {
          setMessage(msg.body); 
        });
      },
      onStompError: (frame: any) => {
        console.error("Error occurred: " + frame.headers["message"]);
      },
    });

    socketClient.activate();
    setClient(socketClient);

    return () => {
      socketClient.deactivate();
      setClient(null); 
    };
  }, [userId]);


  return message;
};

export default useWebSocket;
