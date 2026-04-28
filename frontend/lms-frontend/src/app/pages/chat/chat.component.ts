import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Component, inject, signal } from "@angular/core";
import { LoginService } from "../../services/login-service/login.service";
import { FormsModule } from "@angular/forms";

@Component({
    selector: 'app-chat',
    imports: [FormsModule],
    templateUrl: './chat.component.html',
    styleUrl: './chat.component.scss',
})
export class ChatComponent{
    private http = inject(HttpClient);
    private loginService = inject(LoginService);

    message="";
    messages = signal<{role: string; text: string}[]>([]);
    loading=signal(false);

    send(){
        const text = this.message.trim();
        if(!text) return;

        this.messages.update((msgs: {role: string; text: string }[]) => [...msgs, {role: 'user', text}]);
        this.message = '';
        this.loading.set(true);

        const headers = new HttpHeaders({
            Authorization: `Bearer ${this.loginService.getToken()}`
        })

        this.http.post<{reply: string}>('http://localhost:8080/api/chat', {message: text}, {headers})
            .subscribe({
                next:(res: {reply: string})=> {
                    this.messages.update((msgs: {role: string; text:string}[])=>[...msgs, {role: 'assistant', text: res.reply}]);
                    this.loading.set(false);
                },
                error: () => {
                    this.messages.update((msgs: {role: string; text:string}[])=>[...msgs, {role: 'assistant', text: 'Error: Could not get response.'}]);
                    this.loading.set(false);

                }
            })

    }
}