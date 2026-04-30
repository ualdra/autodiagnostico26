import { isPlatformBrowser } from '@angular/common';
import { ChangeDetectorRef, Component, Inject, NgZone, OnDestroy, OnInit, PLATFORM_ID } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ChatApiService } from '../../../services/chat-api.service';
import { ChatMessageRequest, ChatMessageResponse, ChatRoomType, ChatSenderRole } from '../../../services/api.models';
import { AuthStateService } from '../../../services/auth-state.service';

type ChatAuthor = 'mecanico' | 'usuario';

interface ChatMessage {
  id: number;
  author: ChatAuthor;
  own: boolean;
  text: string;
  at: string;
  read: boolean;
}

@Component({
  selector: 'app-seguimiento-chat',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './chat.html',
  styleUrl: './chat.css'
})
export class SeguimientoChatComponent implements OnInit, OnDestroy {
  private readonly roomType: ChatRoomType = 'SEGUIMIENTO';
  private messageRefreshTimerId: number | null = null;
  private latestMessageId: number | null = null;

  userOnline = false;
  draft = '';
  sending = false;

  messages: ChatMessage[] = [];
  
  get currentUserId(): number {
    return this.authStateService.userId() ?? 0;
  }
  
  get participantId(): number {
    return this.currentUserId;
  }

  get isMechanic(): boolean {
    const role = this.authStateService.role();
    return role === 'TALLER' || role === 'ADMIN';
  }

  get senderRole(): ChatSenderRole {
    return this.isMechanic ? 'MECANICO' : 'USUARIO';
  }

  get canSend(): boolean {
    if (this.isMechanic) {
      return true;
    }
    return this.messages.some((message) => message.author === 'mecanico');
  }

  get sessionUuid(): string {
    // Usar un UUID único por usuario - en producción, esto debería venir del servidor
    const userId = this.currentUserId;
    return `seguimiento-user-${userId}`;
  }

  constructor(
    private readonly chatApiService: ChatApiService,
    private readonly authStateService: AuthStateService,
    private readonly cdr: ChangeDetectorRef,
    private readonly ngZone: NgZone,
    @Inject(PLATFORM_ID) private readonly platformId: object
  ) {}

  ngOnInit(): void {
    if (!isPlatformBrowser(this.platformId) || !this.authStateService.canAccessSeguimiento()) {
      return;
    }

    // Validar que tenemos un usuario logueado
    const userId = this.currentUserId;
    if (!userId || userId === 0) {
      console.error('Chat: No hay usuario logueado válido. UserId:', userId);
      this.userOnline = false;
      return;
    }

    this.refreshPresence();
    this.chatApiService.joinRoom(this.roomType, userId).subscribe({
      next: () => {
        this.userOnline = true;
        this.fetchMessages();
        this.startMessageRefresh();
        this.chatApiService.markReadByUser(this.roomType).subscribe();
      },
      error: (err) => {
        console.error('Error joining chat room:', err);
        this.userOnline = false;
      }
    });
  }

  ngOnDestroy(): void {
    if (!isPlatformBrowser(this.platformId) || !this.authStateService.canAccessSeguimiento()) {
      return;
    }

    const userId = this.currentUserId;
    if (!userId || userId === 0) {
      return;
    }

    this.stopMessageRefresh();
    this.chatApiService.leaveRoom(this.roomType, userId).subscribe();
  }

  get unreadCount(): number {
    return this.messages.filter((msg) => msg.author === 'mecanico' && !msg.read).length;
  }

  sendMessage(): void {
    const value = this.draft.trim();
    if (!value) {
      return;
    }

    if (!this.canSend) {
      console.warn('Solo el mecanico puede iniciar la conversacion');
      return;
    }

    const userId = this.currentUserId;
    if (!userId || userId === 0) {
      console.error('Cannot send message: No valid user ID');
      return;
    }

    this.sending = true;

    const payload: ChatMessageRequest = {
      participantId: userId,
      roomType: this.roomType,
      senderRole: this.senderRole,
      sessionUuid: this.sessionUuid,
      commentText: value
    };

    this.chatApiService.sendMessage(payload).subscribe({
      next: (sentMessage) => {
        this.sending = false;
        this.upsertMessages([sentMessage]);
        this.draft = '';
      },
      error: (err) => {
        console.error('Error sending message:', err);
        this.sending = false;
      }
    });
  }

  private fetchMessages(): void {
    this.chatApiService.listMessages(this.roomType, 60).subscribe({
      next: (messages) => {
        this.messages = messages.map((message) => this.toViewMessage(message));
        this.latestMessageId = messages.length > 0 ? messages[messages.length - 1].id : null;
      }
    });
  }

  private fetchNewMessages(): void {
    if (!this.latestMessageId) {
      return;
    }

    this.chatApiService.listMessages(this.roomType, 60, this.latestMessageId).subscribe({
      next: (messages) => {
        this.upsertMessages(messages);
      }
    });
  }

  private refreshPresence(): void {
    const userId = this.currentUserId;
    if (!userId || userId === 0) {
      return;
    }

    this.chatApiService.isUserOnline(this.roomType, userId).subscribe({
      next: (isOnline) => {
        this.userOnline = isOnline;
      }
    });
  }

  private startMessageRefresh(): void {
    this.stopMessageRefresh();
    this.ngZone.runOutsideAngular(() => {
      this.messageRefreshTimerId = window.setInterval(() => {
        this.ngZone.run(() => {
          this.fetchNewMessages();
          this.refreshPresence();
          this.cdr.detectChanges();
        });
      }, 2500);
    });
  }

  private stopMessageRefresh(): void {
    if (this.messageRefreshTimerId !== null) {
      window.clearInterval(this.messageRefreshTimerId);
      this.messageRefreshTimerId = null;
    }
  }

  private upsertMessages(incoming: ChatMessageResponse[]): void {
    if (!incoming.length) {
      return;
    }

    const lastId = this.latestMessageId ?? 0;
    const newMessages = incoming.filter((message) => message.id > lastId);
    if (!newMessages.length) {
      return;
    }

    this.messages = [
      ...this.messages,
      ...newMessages.map((message) => this.toViewMessage(message))
    ].slice(-120);

    this.latestMessageId = this.messages[this.messages.length - 1].id;
  }

  private toViewMessage(message: ChatMessageResponse): ChatMessage {
    const parsedDate = new Date(message.createdAt);
    const hasValidDate = !Number.isNaN(parsedDate.getTime());
    
    const isMyMessage = message.participantId === this.currentUserId;
    const isMechanicMessage = message.senderRole === 'MECANICO';

    return {
      id: message.id,
      author: isMechanicMessage ? 'mecanico' : 'usuario',
      own: isMyMessage,
      text: message.commentText,
      at: hasValidDate ? parsedDate.toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' }) : '--:--',
      read: message.readByUser
    };
  }
}
