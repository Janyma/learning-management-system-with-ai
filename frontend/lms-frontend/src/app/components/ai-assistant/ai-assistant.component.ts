import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, inject, Input, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { LoginService } from '../../services/login-service/login.service';

@Component({
  selector: 'app-ai-assistant',
  imports: [FormsModule],
  templateUrl: './ai-assistant.component.html',
  styleUrl: './ai-assistant.component.scss',
})
export class AiAssistantComponent {
  @Input() context = '';

  private http = inject(HttpClient);
  private loginService = inject(LoginService);

  open = signal(false);
  message = '';
  messages = signal<{ role: string; text: string }[]>([]);
  loading = signal(false);
  chatSessionId: number | null = null;

  toggle() {
    this.open.update(o => !o);
  }

  send() {
    const text = this.message.trim();
    if (!text) return;

    this.messages.update(msgs => [...msgs, { role: 'user', text }]);
    this.message = '';
    this.loading.set(true);

    const headers = new HttpHeaders({
      Authorization: `Bearer ${this.loginService.getToken()}`
    });

    const body: any = { message: text, context: this.context };
    if (this.chatSessionId) {
      body.chatSessionId = this.chatSessionId;
    }

    this.http.post<{ reply: string; chatSessionId: number }>('http://localhost:8080/api/chat', body, { headers })
      .subscribe({
        next: (res) => {
          this.chatSessionId = res.chatSessionId;
          this.messages.update(msgs => [...msgs, { role: 'assistant', text: res.reply }]);
          this.loading.set(false);
        },
        error: () => {
          this.messages.update(msgs => [...msgs, { role: 'assistant', text: 'Error: Could not get response.' }]);
          this.loading.set(false);
        }
      });
  }
}
