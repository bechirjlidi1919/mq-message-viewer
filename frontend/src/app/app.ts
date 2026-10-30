import { Component, OnInit, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-root',
  imports: [DatePipe],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {

  private http = inject(HttpClient);

  messages = signal<any[]>([]);
  page = signal(0);
  totalPages = signal(0);
  total = signal(0);
  selected = signal<any>(null);
  search = '';

  ngOnInit() {
    this.load(0);
  }

  rechercher(texte: string) {
    this.search = texte.trim();
    this.selected.set(null);
    this.load(0);
  }

  load(page: number) {
    let url = `/api/messages?page=${page}&size=20`;
    if (this.search) {
      url += `&search=${encodeURIComponent(this.search)}`;
    }
    this.http.get<any>(url).subscribe(res => {
      this.messages.set(res.content);
      this.page.set(res.page);
      this.totalPages.set(res.totalPages);
      this.total.set(res.totalElements);
    });
  }

  select(id: number) {
    this.http.get<any>(`/api/messages/${id}`).subscribe(m => this.selected.set(m));
  }
}