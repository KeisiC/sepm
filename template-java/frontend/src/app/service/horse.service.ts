import {HttpClient, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from 'src/environments/environment';
import {Horse, HorseSearch} from '../dto/horse';

const baseUri = environment.backendUrl + '/horses';

@Injectable({
  providedIn: 'root'
})
export class HorseService {

  constructor(
    private http: HttpClient,
  ) { }

  /**
   * Get all horses stored in the system
   *
   * @return observable list of found horses.
   */
  getAll(): Observable<Horse[]> {
    return this.http.get<Horse[]>(baseUri);
  }


  /**
   * Create a new horse in the system.
   *
   * @param horse the data for the horse that should be created
   * @return an Observable for the created horse
   */
  create(horse: Horse): Observable<Horse> {
    return this.http.post<Horse>(
      baseUri,
      horse
    );
  }

  /**
   * Delete a horse in the system given its id.
   *
   * @param id of the horse to be deleted
   * @return horse permanently deleted from the database
   */
  delete(id: number): Observable<Horse> {
    return this.http.delete<Horse>(baseUri + '/' + id);
  }

  /**
   * Edit an existing horse in the system.
   *
   * @param horse the data for the horse that is going to be edited
   * @return an Observable for the edited horse
   */
  update(horse: Horse): Observable<Horse> {
    return this.http.put<Horse>(
      baseUri + '/' + horse.id,
      horse
    );
  }

  getById(id: number) {
    console.log('Load horse details for ' + id);
    return this.http.get<Horse>(baseUri + '/' + id);
  }

  public searchFathersByName(name: string, limitTo: number): Observable<Horse[]> {
    const params = new HttpParams()
      .set('name', name)
      .set('sex', 'MALE')
      .set('maxAmount', limitTo);
    return this.http.get<Horse[]>(baseUri, {params});
  }

  public searchMothersByName(name: string, limitTo: number): Observable<Horse[]> {
    const params = new HttpParams()
      .set('name', name)
      .set('sex', 'FEMALE')
      .set('maxAmount', limitTo);
    return this.http.get<Horse[]>(baseUri, {params});
  }

  /**
   * Fetches all horses from the backend that match te specified parameters
   *
   * @param horse with parameters to be searched
   */
  getSearchedHorses(horse: HorseSearch): Observable<Horse[]> {
    console.log('Load all searched horses', horse);
    let params = new HttpParams();
    if (horse.name !== null && horse.name!== undefined) {params = params.append('name', horse.name);}
    if (horse.description !== null && horse.name!== undefined) {params = params.append('description', horse.description);}
    if (horse.bornBefore !== null) {params = params.append('bornBefore', horse.bornBefore);}
    if (horse.sex !== null) {params = params.append('sex', horse.sex);}
    if (horse.ownerName !== null ) {params = params.append('ownerName', horse.ownerName);}
    return this.http.get<Horse[]>(baseUri, {params,});
  }

  /*public loadMaleHorses(horse: HorseSearch): Observable<Horse[]> {
    console.log('Load all male horses', horse);
    let params = new HttpParams();
    if (horse.name !== null && horse.name!== undefined) {params = params.append('name', horse.name);}
    if (horse.sex !== null) {params = params.append('sex', 'MALE');}
    return this.http.get<Horse[]>(baseUri, {params,});
  }

   */
}
