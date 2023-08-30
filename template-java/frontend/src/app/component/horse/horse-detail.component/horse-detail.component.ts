import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {Horse} from 'src/app/dto/horse';
import {Sex} from 'src/app/dto/sex';
import {HorseService} from 'src/app/service/horse.service';
import {OwnerService} from 'src/app/service/owner.service';
import {Owner} from '../../../dto/owner';

@Component({
  selector: 'app-horse-detail',
  templateUrl: './horse-detail.component.html',
  styleUrls: ['./horse-detail.component.scss']
})
export class HorseDetailComponent implements OnInit {

  horse: Horse = {
    //father: 0, mother: 0,
    name: '',
    description: '',
    dateOfBirth: new Date(),
    sex: Sex.female
  };

  bannerError: string | null = null;

  constructor(
    private service: HorseService,
    private ownerService: OwnerService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {
  }

  public get heading(): string {
    return 'Details of existing horse';

  }
  ngOnInit(): void {
    const detailID: string | null = this.route.snapshot.paramMap.get('id');
    this.loadHorse(Number(detailID));
  }

  /**
   * Loads the horse for the specified id
   *
   * @param id the id of the horse
   */
  private loadHorse(id: number) {
    this.service.getById(id).subscribe(
      (horse: Horse) => {
        this.horse = horse;
      },
      error => {
        console.error('Error loading horse details...');
      }
    );
  }

  // eslint-disable-next-line @typescript-eslint/member-ordering
  delete(id: number) {
    this.service.delete(id).subscribe(
      (res) => {
        this.router.navigate(['/horses']);
        this.notification.success(`Horse ${this.horse.name} successfully deleted.`);
        console.log('Successfully deleted horse');
      },
      error => {
        console.error('Error deleting horse', error);
      }
    );
  }

  // eslint-disable-next-line @typescript-eslint/member-ordering
  loadMother(id: number) {
        this.router.navigate(['/horses/' + id]);
        //this.notification.success(`Horse ${this.horse.name} successfully deleted.`);
        console.log('Successfully loaded horse');
  }

  // eslint-disable-next-line @typescript-eslint/member-ordering
  ownerName(owner: Owner | null): string {
    return owner
      ? `${owner.firstName} ${owner.lastName}`
      : '';
  }

  // eslint-disable-next-line @typescript-eslint/member-ordering
  fatherName(father: Horse | null): string {
    return father
      ? `${father.name}`
      : '';
  }

  // eslint-disable-next-line @typescript-eslint/member-ordering
  motherName(mother: Horse | null): string {
    return mother
      ? `${mother.name}`
      : '';
  }
}
