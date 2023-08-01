import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Owner } from '../dto/owner';

const baseUri = environment.backendUrl + '/owners';

@Injectable({
  providedIn: 'root'
})
export class OwnerService {

  constructor(
    private http: HttpClient,
  ) { }

  public searchByName(name: string, limitTo: number): Observable<Owner[]> {
    const params = new HttpParams()
      .set('name', name)
      .set('maxAmount', limitTo);
    return this.http.get<Owner[]>(baseUri, { params });
  }

  /**
   * Get all horses stored in the system
   *
   * @return observable list of found horses.
   */
  getAll(): Observable<Owner[]> {
    return this.http.get<Owner[]>(baseUri);
  }

  /**
   * Create a new horse in the system.
   *
   * @param owner the data for the horse that should be created
   * @return an Observable for the created horse
   */
  create(owner: Owner): Observable<Owner> {
    return this.http.post<Owner>(
      baseUri,
      owner
    );
  }

  getById(id: number) {
    console.log('Load horse details for ' + id);
    return this.http.get<Owner>(baseUri + '/' + id);
  }
}
