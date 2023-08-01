import {Component, OnInit} from '@angular/core';
import {ToastrService} from 'ngx-toastr';
import {OwnerService} from 'src/app/service/owner.service';
import {Owner} from '../../dto/owner';

@Component({
  selector: 'app-horse',
  templateUrl: './owner.component.html',
  styleUrls: ['./owner.component.scss']
})
export class OwnerComponent implements OnInit {
  search = false;
  owners: Owner[] = [];
  bannerError: string | null = null;

  constructor(
    private service: OwnerService,
    private notification: ToastrService,
  ) { }

  ngOnInit(): void {
    this.reloadOwners();
  }

  reloadOwners() {
    this.service.getAll()
      .subscribe({
        next: data => {
          this.owners = data;
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

}
