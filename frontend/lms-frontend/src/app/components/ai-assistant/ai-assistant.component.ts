import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, computed, inject, input, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { LoginService } from '../../services/login-service/login.service';

interface SectionConversation {
  chatSessionId: number | null;
  messages: { role: string; text: string }[];
}

@Component({
  selector: 'app-ai-assistant',
  imports: [FormsModule],
  templateUrl: './ai-assistant.component.html',
  styleUrl: './ai-assistant.component.scss',
})
export class AiAssistantComponent {
  sectionId = input<number | null>(null);
  context = input('');

  private http = inject(HttpClient);
  private loginService = inject(LoginService);

  open = signal(false);
  message = '';
  loading = signal(false);

  private conversations = signal<Map<number, SectionConversation>>(new Map());

  messages = computed(() => {
    const id = this.sectionId();
    if (id === null) return [];
    return this.conversations().get(id)?.messages ?? [];
  });

  toggle() {
    this.open.update(o => !o);
  }

  private updateConversation(sectionId: number, updater: (c: SectionConversation) => SectionConversation) {
    this.conversations.update(map => {
      const next = new Map(map);
      const existing = next.get(sectionId) ?? { chatSessionId: null, messages: [] };
      next.set(sectionId, updater(existing));
      return next;
    });
  }

  send() {
    const text = this.message.trim();
    const sectionId = this.sectionId();
    if (!text || sectionId === null) return;

    this.updateConversation(sectionId, c => ({ ...c, messages: [...c.messages, { role: 'user', text }] }));
    this.message = '';
    this.loading.set(true);

    const chatSessionId = this.conversations().get(sectionId)?.chatSessionId ?? null;

    const headers = new HttpHeaders({
      Authorization: `Bearer ${this.loginService.getToken()}`
    });

    const body: any = { message: text, context: this.context() };
    if (chatSessionId) {
      body.chatSessionId = chatSessionId;
    }

    this.http.post<{ reply: string; chatSessionId: number }>('http://localhost:8080/api/chat', body, { headers })
      .subscribe({
        next: (res) => {
          this.updateConversation(sectionId, c => ({
            chatSessionId: res.chatSessionId,
            messages: [...c.messages, { role: 'assistant', text: res.reply }]
          }));
          this.loading.set(false);
        },
        error: () => {
          this.updateConversation(sectionId, c => ({
            ...c,
            messages: [...c.messages, { role: 'assistant', text: 'Error: Could not get response.' }]
          }));
          this.loading.set(false);
        }
      });
  }
}
