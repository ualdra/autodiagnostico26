import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from './api.config';
import { ChatJoinResponse, ChatMessageRequest, ChatMessageResponse, ChatRoomType } from './api.models';

@Injectable({ providedIn: 'root' })
export class ChatApiService {
  private readonly baseUrl = `${API_BASE_URL}/chat`;

  constructor(private readonly http: HttpClient) {}

  joinRoom(roomType: ChatRoomType, participantId: number): Observable<ChatJoinResponse> {
    return this.http.post<ChatJoinResponse>(`${this.baseUrl}/${roomType}/join?participantId=${participantId}`, {});
  }

  leaveRoom(roomType: ChatRoomType, participantId: number): Observable<ChatJoinResponse> {
    return this.http.post<ChatJoinResponse>(`${this.baseUrl}/${roomType}/leave?participantId=${participantId}`, {});
  }

  listMessages(roomType: ChatRoomType, limit = 60, afterId?: number): Observable<ChatMessageResponse[]> {
    let url = `${this.baseUrl}/${roomType}/mensajes?limit=${limit}`;
    if (typeof afterId === 'number' && afterId > 0) {
      url += `&afterId=${afterId}`;
    }
    return this.http.get<ChatMessageResponse[]>(url);
  }

  sendMessage(payload: ChatMessageRequest): Observable<ChatMessageResponse> {
    return this.http.post<ChatMessageResponse>(`${this.baseUrl}/mensajes`, payload);
  }

  unreadCount(roomType: ChatRoomType): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/${roomType}/unread`);
  }

  markReadByUser(roomType: ChatRoomType): Observable<number> {
    return this.http.post<number>(`${this.baseUrl}/${roomType}/mark-read`, {});
  }

  isUserOnline(roomType: ChatRoomType, participantId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.baseUrl}/${roomType}/presence?participantId=${participantId}`);
  }
}
