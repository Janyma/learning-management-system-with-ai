import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Component, inject, OnInit, signal } from "@angular/core";
import { LoginService } from "../../services/login-service/login.service";
import { FormsModule } from "@angular/forms";
import { ActivatedRoute } from "@angular/router";

@Component({
    selector: 'app-chat',
    imports: [FormsModule],
    templateUrl: './chat.component.html',
    styleUrl: './chat.component.scss',
})
export class ChatComponent implements OnInit {
    private http = inject(HttpClient);
    private loginService = inject(LoginService);
    private route = inject(ActivatedRoute);

    message = "";
    messages = signal<{role: string; text: string}[]>([]);
    loading = signal(false);
    chatSessionId: number | null = null;

    ngOnInit() {
        this.route.queryParams.subscribe(params => {
            const sessionId = params['sessionId'];
            if (sessionId) {
                this.loadSession(+sessionId);
            }
        });
    }

    loadSession(sessionId: number) {
        this.loading.set(true);
        this.messages.set([]);
        this.chatSessionId = sessionId;

        const headers = new HttpHeaders({
            Authorization: `Bearer ${this.loginService.getToken()}`
        });

        this.http.get<any[]>(`http://localhost:8080/api/chat/sessions/${sessionId}`, { headers })
            .subscribe({
                next: (msgs) => {
                    const mapped = msgs.map(m => ({
                        role: m.role === 'user' ? 'user' : 'assistant',
                        text: m.content
                    }));
                    this.messages.set(mapped);
                    this.loading.set(false);
                },
                error: () => {
                    this.loading.set(false);
                }
            });
    }

    send() {
        const text = this.message.trim();
        if (!text) return;

        this.messages.update((msgs: {role: string; text: string}[]) => [...msgs, {role: 'user', text}]);
        this.message = '';
        this.loading.set(true);

        const headers = new HttpHeaders({
            Authorization: `Bearer ${this.loginService.getToken()}`
        });

        const body: any = { message: text };
        if (this.chatSessionId) {
            body.chatSessionId = this.chatSessionId;
        }

        this.http.post<{reply: string; chatSessionId: number}>('http://localhost:8080/api/chat', body, { headers })
            .subscribe({
                next: (res) => {
                    this.chatSessionId = res.chatSessionId;
                    this.messages.update((msgs: {role: string; text: string}[]) => [...msgs, {role: 'assistant', text: res.reply}]);
                    this.loading.set(false);
                },
                error: () => {
                    this.messages.update((msgs: {role: string; text: string}[]) => [...msgs, {role: 'assistant', text: 'Error: Could not get response.'}]);
                    this.loading.set(false);
                }
            });
    }
}