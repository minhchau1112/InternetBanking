export class WebSocketService {
    private socket: WebSocket | null = null;

    connect(url: string, onMessage: (message: string) => void): void {
        this.socket = new WebSocket(url);

        this.socket.onopen = () => {
            console.log("WebSocket connected");
        };

        this.socket.onmessage = (event) => {
            onMessage(event.data);
        };

        this.socket.onclose = () => {
            console.log("WebSocket disconnected");
        };

        this.socket.onerror = (error) => {
            console.error("WebSocket error:", error);
        };
    }

    disconnect(): void {
        if (this.socket) {
            this.socket.close();
        }
    }
}
