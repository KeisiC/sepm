import {Component, OnInit} from '@angular/core';
import {ToastrService} from 'ngx-toastr';
import {HorseService} from 'src/app/service/horse.service';
import {Horse, HorseDelete, HorseSearch} from '../../dto/horse';
import {Owner} from '../../dto/owner';
import {ActivatedRoute, Router} from '@angular/router';
import {Sex} from '../../dto/sex';

@Component({
  selector: 'app-horse',
  templateUrl: './horse.component.html',
  styleUrls: ['./horse.component.scss']
})
export class HorseComponent implements OnInit {
  search = false;
  horses: Horse[] = [];
  bannerError: string | null = null;

  horse: HorseDelete = {
    name: '',
    description: '',
    dateOfBirth: '',
    sex: Sex.unassigned,
    ownerName: ''
  };

  horseSearch: HorseSearch = {
    name: '',
    description: '',
    bornBefore: '',
    sex: Sex.unassigned,
    ownerName: ''
  };

  constructor(
    private service: HorseService,
    private notification: ToastrService,
    private route: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.reloadHorses();
  }

  reloadHorses() {
    this.service.getAll()
      .subscribe({
        next: data => {
          this.horses = data;
        },
        error: error => {
          console.error('Error fetching horses', error);
          this.bannerError = 'Could not fetch horses: ' + error.message;
          const errorMessage = error.status === 0
            ? 'Is the backend up?'
            : error.message.message;
          this.notification.error(errorMessage, 'Could Not Fetch Horses');
        }
      });
  }

  searchHorses() {
    this.service.getSearchedHorses(this.horseSearch)
      .subscribe({
        next: data => {
          this.horses = data;
        },
        error: error => {
          console.error('Error fetching horses', error);
          this.bannerError = 'Could not fetch horses: ' + error.message;
          const errorMessage = error.status === 0
            ? 'Is the backend up?'
            : error.message.message;
          this.notification.error(errorMessage, 'Could Not Fetch Horses');
        }
      });
  }

  ownerName(owner: Owner | null): string {
    return owner
      ? `${owner.firstName} ${owner.lastName}`
      : '';
  }

  dateOfBirthAsLocaleDate(horse: Horse): string {
    return new Date(horse.dateOfBirth).toLocaleDateString();
  }

  delete(id: number) {
    this.service.delete(id).subscribe(
      (res) => {
        this.router.navigate(['/horses']);
        this.notification.success(`Horse ${this.horse.name} successfully deleted.`);
        console.log('Successfully deleted horse');
        this.ngOnInit();
      },
      error => {
        console.error('Error deleting horse', error);
      }
    );
  }

}
