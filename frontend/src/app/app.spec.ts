import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { App } from './app';
 
describe('App', () => {
 
  let httpMock: HttpTestingController;
 
  // Fausse réponse de l'API : 2 messages sur une seule page
  const faussePage = {
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
  function demarrer(page = faussePage) {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges(); // déclenche ngOnInit()
    httpMock.expectOne('/api/messages?page=0&size=20').flush(page);
    fixture.detectChanges(); // met l'écran à jour avec la réponse
    return fixture;
  }
 
  // Trouve un bouton par son texte (indépendant de sa position dans la page)
  function bouton(ecran: HTMLElement, texte: string): HTMLButtonElement {
    return Array.from(ecran.querySelectorAll('button'))
      .find(b => b.textContent?.includes(texte)) as HTMLButtonElement;
  }
 
  it('charge la première page au démarrage', () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();
 
    const req = httpMock.expectOne('/api/messages?page=0&size=20');
    expect(req.request.method).toBe('GET');
    req.flush(faussePage);
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
 
  it('désactive les boutons inutilisables au démarrage', () => {
    const fixture = demarrer();
    const ecran: HTMLElement = fixture.nativeElement;
 
    expect(bouton(ecran, 'Précédent').disabled).toBe(true);      // première page
    expect(bouton(ecran, 'Suivant').disabled).toBe(true);        // une seule page
    expect(bouton(ecran, 'Réinitialiser').disabled).toBe(true);  // aucune recherche active
  });
 
  it('charge la page suivante au clic sur Suivant', () => {
    const fixture = demarrer({ ...faussePage, totalElements: 60, totalPages: 3 });
    const ecran: HTMLElement = fixture.nativeElement;
 
    bouton(ecran, 'Suivant').click();
 
    httpMock.expectOne('/api/messages?page=1&size=20').flush({ ...faussePage, page: 1, totalPages: 3 });
  });
 
  it('lance une recherche avec le texte saisi', () => {
    const fixture = demarrer();
    const ecran: HTMLElement = fixture.nativeElement;
 
    const champ = ecran.querySelector('input') as HTMLInputElement;
    champ.value = 'test-42';
    bouton(ecran, 'Rechercher').click();
 
    httpMock.expectOne('/api/messages?page=0&size=20&search=test-42')
      .flush({ ...faussePage, totalElements: 1, content: [faussePage.content[0]] });
    fixture.detectChanges();
 
    expect(ecran.querySelector('h1')?.textContent).toContain('Messages MQ (1)');
    expect(bouton(ecran, 'Réinitialiser').disabled).toBe(false); // une recherche est active
  });
 
  it('réinitialise la recherche', () => {
    const fixture = demarrer();
    const ecran: HTMLElement = fixture.nativeElement;
    const champ = ecran.querySelector('input') as HTMLInputElement;
 
    // 1. On lance une recherche
    champ.value = 'test-42';
    bouton(ecran, 'Rechercher').click();
    httpMock.expectOne('/api/messages?page=0&size=20&search=test-42').flush(faussePage);
    fixture.detectChanges();
 
    // 2. On réinitialise
    bouton(ecran, 'Réinitialiser').click();
 
    // 3. La liste complète est rechargée sans paramètre search, et le champ est vide
    httpMock.expectOne('/api/messages?page=0&size=20').flush(faussePage);
    expect(champ.value).toBe('');
  });
});