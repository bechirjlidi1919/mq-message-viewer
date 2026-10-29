import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { App } from './app';

describe('App', () => {

  let httpMock: HttpTestingController;

  // Fausse réponse de l'API : 2 messages
  const fausePage = {
    content: [
      { id: 2, messageId: 'ID:2', queueName: 'DEV.PAYMENT.IN', receivedAt: '2026-10-29T15:00:00Z' },
      { id: 1, messageId: 'ID:1', queueName: 'DEV.PAYMENT.IN', receivedAt: '2026-10-29T14:00:00Z' }
    ],
    page: 0,
    size: 20,
    totalElements: 2,
    totalPages: 1
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [App],
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify(); // vérifie qu'aucun appel HTTP inattendu n'a eu lieu
  });

  // Crée le composant et répond au premier appel avec la page donnée
  function demarrer(page = fausePage) {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges(); // déclenche ngOnInit()
    httpMock.expectOne('/api/messages?page=0&size=20').flush(page);
    fixture.detectChanges(); // met l'écran à jour avec la réponse
    return fixture;
  }

  it('charge la première page au démarrage', () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();

    const req = httpMock.expectOne('/api/messages?page=0&size=20');
    expect(req.request.method).toBe('GET');
    req.flush(fausePage);
  });

  it('affiche le total et les messages', () => {
    const fixture = demarrer();
    const ecran: HTMLElement = fixture.nativeElement;

    expect(ecran.querySelector('h1')?.textContent).toContain('Messages MQ (2)');
    expect(ecran.querySelectorAll('tr').length).toBe(3); // 1 en-tête + 2 messages
  });

  it('affiche le contenu d\'un message au clic', () => {
    const fixture = demarrer();
    const ecran: HTMLElement = fixture.nativeElement;

    const premiereLigne = ecran.querySelectorAll('tr')[1] as HTMLElement;
    premiereLigne.click();

    httpMock.expectOne('/api/messages/2').flush({ id: 2, payload: 'Bonjour MQ' });
    fixture.detectChanges();

    expect(ecran.querySelector('pre')?.textContent).toContain('Bonjour MQ');
  });

  it('désactive les boutons quand il n\'y a qu\'une page', () => {
    const fixture = demarrer();
    const boutons = fixture.nativeElement.querySelectorAll('button');

    expect(boutons[0].disabled).toBe(true); // Précédent
    expect(boutons[1].disabled).toBe(true); // Suivant
  });

  it('charge la page suivante au clic sur Suivant', () => {
    const fixture = demarrer({ ...fausePage, totalElements: 60, totalPages: 3 });
    const boutons = fixture.nativeElement.querySelectorAll('button');

    boutons[1].click(); // Suivant

    httpMock.expectOne('/api/messages?page=1&size=20').flush({ ...fausePage, page: 1, totalPages: 3 });
  });
});