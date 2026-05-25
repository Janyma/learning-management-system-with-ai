import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Component, inject, OnInit, signal } from "@angular/core";
import { LoginService } from "../../services/login-service/login.service";
import { Router } from "@angular/router";
import { DatePipe } from "@angular/common";

interface ChatSession {
    id: number;
    title: string;
    createdAt: string;
}

@Component({
    selector: 'app-chat-history',
    imports: [DatePipe],
    templateUrl: './chat-history.component.html',
    styleUrl: './chat-history.component.scss',
})
export class ChatHistoryComponent implements OnInit {
    private http = inject(HttpClient);
    private loginService = inject(LoginService);
    private router = inject(Router);

    sessions = signal<ChatSession[]>([]);
    loading = signal(false);

    ngOnInit() {
        this.loadSessions();
    }

    loadSessions() {
        this.loading.set(true);
        const headers = new HttpHeaders({
            Authorization: `Bearer ${this.loginService.getToken()}`
        });

        this.http.get<ChatSession[]>('http://localhost:8080/api/chat/sessions', { headers })
            .subscribe({
                next: (sessions) => {
                    this.sessions.set(sessions);
                    this.loading.set(false);
                },
                error: () => {
                    this.loading.set(false);
                }
            });
    }

    openSession(sessionId: number) {
        this.router.navigate(['/chat'], { queryParams: { sessionId } });
    }

    startNewChat() {
        this.router.navigate(['/chat']);
    }
}
